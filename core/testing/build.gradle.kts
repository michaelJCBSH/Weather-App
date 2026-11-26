plugins {
    alias(libs.plugins.weather.android.library)
    alias(libs.plugins.weather.android.compose)
}

android {
    namespace = "com.jcbsh.weather.core.testing"
    lint {
        // Flags a trust-all TrustManager inside Paparazzi's bundled BouncyCastle jar. This module only
        // ever runs on the test classpath, never in the app.
        disable += "TrustAllX509TrustManager"
    }
}

dependencies {
    api(projects.design)
    api(libs.junit4)
    api(libs.kotlinx.coroutines.test)
    api(libs.paparazzi)
}
