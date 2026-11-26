package com.jcbsh.weather.feature.locations.presentation.locationsearch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.domain.repository.LocationSearchRepository
import com.jcbsh.weather.core.domain.repository.SavedLocationRepository
import com.jcbsh.weather.core.domain.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val searchRepository: LocationSearchRepository,
    private val savedLocationRepository: SavedLocationRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val query: StateFlow<String> = savedStateHandle.getStateFlow(QueryKey, "")
    private val retryRequests = MutableStateFlow(0)
    private val saveStatus = MutableStateFlow(SaveStatus.Idle)

    private val savedLocationIds: Flow<Set<Long>> = savedLocationRepository.observeSavedLocations()
        .map { saved -> saved.mapTo(mutableSetOf()) { it.location.id } }

    private val searchResults: Flow<SearchResults> = combine(
        query.debounce(DebounceMillis).map { it.trim() }.distinctUntilChanged(),
        retryRequests,
    ) { trimmedQuery, _ -> trimmedQuery }
        .flatMapLatest { trimmedQuery ->
            if (trimmedQuery.length < MinQueryLength) {
                flowOf<SearchResults>(SearchResults.Idle)
            } else {
                flow<SearchResults> {
                    emit(SearchResults.Loading)
                    emit(search(trimmedQuery))
                }
            }
        }

    val uiState: StateFlow<LocationSearchUiState> = combine(
        query,
        combine(searchResults, savedLocationIds, ::markSaved),
        saveStatus,
    ) { query, results, saveStatus ->
        LocationSearchUiState(query = query, results = results, saveStatus = saveStatus)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LocationSearchUiState(query = query.value),
    )

    fun onQueryChange(query: String) {
        savedStateHandle[QueryKey] = query
    }

    fun onRetry() {
        retryRequests.value++
    }

    /** Saves [location] unless it is already a Saved Location. */
    fun onLocationSelected(location: Location) {
        val results = uiState.value.results
        val alreadySaved = results is SearchResults.Content &&
            results.results.any { it.location.id == location.id && it.isSaved }
        if (alreadySaved) return

        viewModelScope.launch {
            saveStatus.value = when (savedLocationRepository.save(location)) {
                is DataResult.Success -> SaveStatus.Saved
                is DataResult.Error -> SaveStatus.Failed
            }
        }
    }

    fun onSaveErrorShown() {
        saveStatus.value = SaveStatus.Idle
    }

    private suspend fun search(query: String): SearchResults =
        when (val result = searchRepository.search(query, Locale.getDefault().language)) {
            is DataResult.Error -> SearchResults.Error
            is DataResult.Success -> if (result.data.isEmpty()) {
                SearchResults.Empty(query)
            } else {
                SearchResults.Content(result.data.map { SearchResult(it, isSaved = false) })
            }
        }

    private fun markSaved(results: SearchResults, savedIds: Set<Long>): SearchResults =
        if (results is SearchResults.Content) {
            SearchResults.Content(results.results.map { it.copy(isSaved = it.location.id in savedIds) })
        } else {
            results
        }

    companion object {
        const val MinQueryLength = 2
        const val DebounceMillis = 300L
        private const val QueryKey = "query"
    }
}
