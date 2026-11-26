package com.jcbsh.weather.feature.locations.presentation.locationsearch

import androidx.compose.runtime.Immutable
import com.jcbsh.weather.core.domain.model.Location

/**
 * @property query what the user has typed, untrimmed.
 * @property saveStatus outcome of the last tap on a result.
 */
@Immutable
data class LocationSearchUiState(
    val query: String = "",
    val results: SearchResults = SearchResults.Idle,
    val saveStatus: SaveStatus = SaveStatus.Idle,
)

@Immutable
sealed interface SearchResults {
    /** The query is too short to search. */
    data object Idle : SearchResults

    data object Loading : SearchResults

    data object Error : SearchResults

    /** The search ran but found nothing for [query]. */
    data class Empty(val query: String) : SearchResults

    data class Content(val results: List<SearchResult>) : SearchResults
}

/** A geocoding match, marked when it is already a Saved Location. */
data class SearchResult(
    val location: Location,
    val isSaved: Boolean,
)

enum class SaveStatus {
    Idle,

    /** The tapped place was saved; the screen should close. */
    Saved,

    /** Saving failed; the screen should tell the user, then call `onSaveErrorShown()`. */
    Failed,
}
