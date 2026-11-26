import com.android.build.api.dsl.LibraryExtension
import com.jcbsh.weather.buildlogic.configureKotlinAndroid
import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.library
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("android-library"))
        pluginManager.apply(libs.pluginId("weather-detekt"))
        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            lint.targetSdk = com.jcbsh.weather.buildlogic.CompileSdk
            testOptions.targetSdk = com.jcbsh.weather.buildlogic.CompileSdk
        }
        dependencies {
            "testImplementation"(libs.library("junit4"))
        }
    }
}
