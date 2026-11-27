plugins {
    alias(libs.plugins.weather.android.application)
    alias(libs.plugins.weather.android.compose)
    alias(libs.plugins.weather.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.jcbsh.weather"

    defaultConfig {
        applicationId = "com.jcbsh.weather"
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.feature.locations)
    implementation(projects.design)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.timber)

    implementation(platform(libs.firebase.bom))
}
