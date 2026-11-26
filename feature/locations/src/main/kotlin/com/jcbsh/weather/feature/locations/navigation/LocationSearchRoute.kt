package com.jcbsh.weather.feature.locations.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jcbsh.weather.feature.locations.presentation.locationsearch.LocationSearchRoute
import kotlinx.serialization.Serializable

/** Destination for Location Search, where the user finds a place to save. */
@Serializable
data object LocationSearchRoute

fun NavController.navigateToLocationSearch(navOptions: NavOptions? = null) =
    navigate(LocationSearchRoute, navOptions)

/** @param onBack called on Back and after a place has been saved. */
fun NavGraphBuilder.locationSearchScreen(onBack: () -> Unit) {
    composable<LocationSearchRoute> {
        LocationSearchRoute(onBack = onBack)
    }
}
