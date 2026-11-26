package com.jcbsh.weather.core.testing

import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import com.jcbsh.weather.design.WeatherThemeForPreview

/** The configurations every screen state is snapshotted in. */
enum class SnapshotVariant(val deviceConfig: DeviceConfig, val darkTheme: Boolean) {
    Light(DeviceConfig.PIXEL_5, darkTheme = false),
    Dark(DeviceConfig.PIXEL_5.copy(nightMode = NightMode.NIGHT), darkTheme = true),
    FontScale200(DeviceConfig.PIXEL_5.copy(fontScale = 2f), darkTheme = false),
    ExpandedWidth(DeviceConfig.PIXEL_C, darkTheme = false),
}

/** Creates a [Paparazzi] rule whose device is reconfigured by [snapshotAllVariants]. */
fun weatherPaparazzi(): Paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

/** Snapshots [content], wrapped in [WeatherThemeForPreview], once per [SnapshotVariant]. */
fun Paparazzi.snapshotAllVariants(name: String? = null, content: @Composable () -> Unit) {
    SnapshotVariant.entries.forEach { variant ->
        unsafeUpdateConfig(deviceConfig = variant.deviceConfig)
        snapshot(name = listOfNotNull(name, variant.name).joinToString("_")) {
            WeatherThemeForPreview(darkTheme = variant.darkTheme) {
                content()
            }
        }
    }
}
