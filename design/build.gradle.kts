plugins {
    alias(libs.plugins.weather.android.library)
    alias(libs.plugins.weather.android.compose)
    alias(libs.plugins.weather.paparazzi)
}

android {
    namespace = "com.jcbsh.weather.design"
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
}
