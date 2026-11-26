package com.jcbsh.weather.feature.locations.presentation.savedlocations

import com.jcbsh.weather.core.testing.data.TestLocations
import com.jcbsh.weather.core.testing.snapshotAllVariants
import com.jcbsh.weather.core.testing.weatherPaparazzi
import org.junit.Rule
import org.junit.Test

class SavedLocationsScreenSnapshotTest {
    @get:Rule
    val paparazzi = weatherPaparazzi()

    @Test
    fun loading() = snapshot(SavedLocationsUiState.Loading)

    @Test
    fun empty() = snapshot(SavedLocationsUiState.Empty)

    @Test
    fun error() = snapshot(SavedLocationsUiState.Error)

    @Test
    fun content() = snapshot(
        SavedLocationsUiState.Content(
            listOf(TestLocations.Sydney, TestLocations.SpringfieldMissouri, TestLocations.Singapore),
        ),
    )

    private fun snapshot(uiState: SavedLocationsUiState) {
        paparazzi.snapshotAllVariants {
            SavedLocationsScreen(uiState = uiState, onAddLocation = {}, onRemoveLocation = {})
        }
    }
}
