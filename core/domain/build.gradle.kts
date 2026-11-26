plugins {
    alias(libs.plugins.weather.jvm.library)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
