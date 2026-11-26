package com.jcbsh.weather.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jcbsh.weather.feature.locations.navigation.SavedLocationsRoute
import com.jcbsh.weather.feature.locations.navigation.locationSearchScreen
import com.jcbsh.weather.feature.locations.navigation.navigateToLocationSearch
import com.jcbsh.weather.feature.locations.navigation.savedLocationsScreen

/** The app's single NavHost. Each feature wires its routes in here. */
@Composable
fun WeatherNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SavedLocationsRoute,
        modifier = modifier,
    ) {
        savedLocationsScreen(onAddLocation = { navController.navigateToLocationSearch() })
        locationSearchScreen(onBack = { navController.popBackStack() })
    }
}
