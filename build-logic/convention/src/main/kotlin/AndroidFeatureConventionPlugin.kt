import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.library
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("weather-android-library"))
        pluginManager.apply(libs.pluginId("weather-android-compose"))
        pluginManager.apply(libs.pluginId("weather-hilt"))
        pluginManager.apply(libs.pluginId("weather-paparazzi"))
        pluginManager.apply(libs.pluginId("kotlin-serialization"))
        dependencies {
            "implementation"(project(":core:common"))
            "implementation"(project(":core:domain"))
            "implementation"(project(":design"))
            "implementation"(libs.library("androidx-compose-material3"))
            "implementation"(libs.library("androidx-hilt-lifecycle-viewmodel-compose"))
            "implementation"(libs.library("androidx-lifecycle-runtime-compose"))
            "implementation"(libs.library("androidx-lifecycle-viewmodel-compose"))
            "implementation"(libs.library("androidx-navigation-compose"))
            "implementation"(libs.library("kotlinx-serialization-json"))
            "testImplementation"(project(":core:testing"))
            "testImplementation"(libs.library("androidx-compose-ui-test-junit4"))
            "debugImplementation"(libs.library("androidx-compose-ui-test-manifest"))
            "testImplementation"(libs.library("kotlinx-coroutines-test"))
            "testImplementation"(libs.library("robolectric"))
            "testImplementation"(libs.library("turbine"))
        }
    }
}
