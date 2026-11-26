plugins {
    alias(libs.plugins.weather.android.library)
    alias(libs.plugins.weather.hilt)
}

android {
    namespace = "com.jcbsh.weather.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    implementation(libs.timber)
}
