package com.jcbsh.weather.feature.locations.presentation.savedlocations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcbsh.weather.core.common.logging.Logger
import com.jcbsh.weather.core.domain.repository.SavedLocationRepository
import com.jcbsh.weather.core.domain.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsViewModel @Inject constructor(
    private val repository: SavedLocationRepository,
    private val logger: Logger,
) : ViewModel() {

    val uiState: StateFlow<SavedLocationsUiState> = repository.observeSavedLocations()
        .map { saved ->
            if (saved.isEmpty()) {
                SavedLocationsUiState.Empty
            } else {
                SavedLocationsUiState.Content(saved.map { it.location })
            }
        }
        .catch { error ->
            logger.error("Couldn't load saved locations", error)
            emit(SavedLocationsUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SavedLocationsUiState.Loading,
        )

    /** Removes a Saved Location the user has confirmed deleting. */
    fun onRemoveLocation(locationId: Long) {
        viewModelScope.launch {
            val result = repository.remove(locationId)
            if (result is DataResult.Error) {
                logger.warn("Couldn't remove saved location $locationId: ${result.error}")
            }
        }
    }
}
