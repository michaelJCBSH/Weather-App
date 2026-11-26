plugins {
    alias(libs.plugins.weather.android.library)
    alias(libs.plugins.weather.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jcbsh.weather.core.network"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)

    testImplementation(projects.core.testing)
    testImplementation(libs.okhttp.mockwebserver)
}
