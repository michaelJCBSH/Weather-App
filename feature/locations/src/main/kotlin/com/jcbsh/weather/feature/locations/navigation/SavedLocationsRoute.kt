package com.jcbsh.weather.feature.locations.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jcbsh.weather.feature.locations.presentation.savedlocations.SavedLocationsRoute
import kotlinx.serialization.Serializable

/** Destination for the Saved Locations list, the app's landing screen. */
@Serializable
data object SavedLocationsRoute

fun NavController.navigateToSavedLocations(navOptions: NavOptions? = null) =
    navigate(SavedLocationsRoute, navOptions)

fun NavGraphBuilder.savedLocationsScreen(onAddLocation: () -> Unit) {
    composable<SavedLocationsRoute> {
        SavedLocationsRoute(onAddLocation = onAddLocation)
    }
}
