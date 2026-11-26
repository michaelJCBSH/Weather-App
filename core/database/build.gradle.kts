plugins {
    alias(libs.plugins.weather.android.library)
    alias(libs.plugins.weather.android.room)
    alias(libs.plugins.weather.hilt)
}

android {
    namespace = "com.jcbsh.weather.core.database"
}

dependencies {
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
}
