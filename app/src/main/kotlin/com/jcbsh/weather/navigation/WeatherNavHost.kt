package com.jcbsh.weather.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jcbsh.weather.R
import com.jcbsh.weather.design.WeatherTheme

/** The app's single NavHost. Each feature wires its routes in here. */
@Composable
fun WeatherNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = PlaceholderRoute,
        modifier = modifier,
    ) {
        composable<PlaceholderRoute> {
            Scaffold { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = WeatherTheme.typography.headlineMedium,
                    )
                }
            }
        }
    }
}
