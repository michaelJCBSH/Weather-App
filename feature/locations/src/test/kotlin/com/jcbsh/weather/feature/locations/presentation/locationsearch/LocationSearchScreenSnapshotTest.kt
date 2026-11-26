package com.jcbsh.weather.feature.locations.presentation.locationsearch

import com.jcbsh.weather.core.testing.data.TestLocations
import com.jcbsh.weather.core.testing.snapshotAllVariants
import com.jcbsh.weather.core.testing.weatherPaparazzi
import org.junit.Rule
import org.junit.Test

class LocationSearchScreenSnapshotTest {
    @get:Rule
    val paparazzi = weatherPaparazzi()

    @Test
    fun idle() = snapshot(LocationSearchUiState())

    @Test
    fun loading() = snapshot(LocationSearchUiState(query = "Springfield", results = SearchResults.Loading))

    @Test
    fun error() = snapshot(LocationSearchUiState(query = "Springfield", results = SearchResults.Error))

    @Test
    fun empty() = snapshot(LocationSearchUiState(query = "zzqqxx", results = SearchResults.Empty("zzqqxx")))

    @Test
    fun content() = snapshot(
        LocationSearchUiState(
            query = "Springfield",
            results = SearchResults.Content(
                listOf(
                    SearchResult(TestLocations.SpringfieldMissouri, isSaved = true),
                    SearchResult(TestLocations.SpringfieldIllinois, isSaved = false),
                    SearchResult(TestLocations.Singapore, isSaved = false),
                ),
            ),
        ),
    )

    private fun snapshot(uiState: LocationSearchUiState) {
        paparazzi.snapshotAllVariants {
            LocationSearchScreen(
                uiState = uiState,
                onQueryChange = {},
                onRetry = {},
                onLocationSelected = {},
                onSaveErrorShown = {},
                onBack = {},
            )
        }
    }
}
