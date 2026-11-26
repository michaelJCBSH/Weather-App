package com.jcbsh.weather.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Renders the theme's key colours and type styles; used by previews and snapshot tests. */
@Composable
internal fun ThemeShowcase(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(WeatherTheme.spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(WeatherTheme.spacing.Small),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(WeatherTheme.spacing.Small)) {
                Swatch(WeatherTheme.colorScheme.primary)
                Swatch(WeatherTheme.colorScheme.secondary)
                Swatch(WeatherTheme.colorScheme.tertiary)
                Swatch(WeatherTheme.colorScheme.surfaceVariant)
                Swatch(WeatherTheme.colorScheme.error)
            }
            Text(text = "Display", style = WeatherTheme.typography.displaySmall)
            Text(text = "Headline", style = WeatherTheme.typography.headlineMedium)
            Text(text = "Title", style = WeatherTheme.typography.titleMedium)
            Text(text = "Body", style = WeatherTheme.typography.bodyLarge)
            Text(text = "Label", style = WeatherTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun Swatch(color: Color) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color = color, shape = WeatherTheme.shapes.medium),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ThemeShowcasePreview() {
    WeatherThemeForPreview {
        ThemeShowcase()
    }
}
