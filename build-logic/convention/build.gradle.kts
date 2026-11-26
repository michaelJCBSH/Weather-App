plugins {
    `kotlin-dsl`
}

group = "com.jcbsh.weather.buildlogic"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.weather.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.weather.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.weather.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.weather.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.weather.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("detekt") {
            id = libs.plugins.weather.detekt.get().pluginId
            implementationClass = "DetektConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.weather.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.weather.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("paparazzi") {
            id = libs.plugins.weather.paparazzi.get().pluginId
            implementationClass = "PaparazziConventionPlugin"
        }
    }
}
