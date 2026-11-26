package com.jcbsh.weather.feature.locations.presentation.savedlocations

import androidx.compose.runtime.Immutable
import com.jcbsh.weather.core.domain.model.Location

@Immutable
sealed interface SavedLocationsUiState {
    data object Loading : SavedLocationsUiState

    data object Empty : SavedLocationsUiState

    data object Error : SavedLocationsUiState

    /** @property locations Saved Locations, oldest first. */
    data class Content(val locations: List<Location>) : SavedLocationsUiState
}
