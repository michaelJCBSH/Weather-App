plugins {
    alias(libs.plugins.weather.android.library)
    alias(libs.plugins.weather.hilt)
}

android {
    namespace = "com.jcbsh.weather.core.data"
}

dependencies {
    api(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.network)
    implementation(libs.retrofit)

    testImplementation(projects.core.testing)
    testImplementation(libs.turbine)
}
