package com.jcbsh.weather.design

import com.jcbsh.weather.core.testing.snapshotAllVariants
import com.jcbsh.weather.core.testing.weatherPaparazzi
import org.junit.Rule
import org.junit.Test

class WeatherThemeSnapshotTest {
    @get:Rule
    val paparazzi = weatherPaparazzi()

    @Test
    fun themeShowcase() {
        paparazzi.snapshotAllVariants { ThemeShowcase() }
    }
}
