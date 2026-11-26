import com.jcbsh.weather.buildlogic.configureKotlin
import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.library
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("kotlin-jvm"))
        pluginManager.apply("com.android.lint")
        pluginManager.apply(libs.pluginId("weather-detekt"))
        configureKotlin()
        dependencies {
            "testImplementation"(libs.library("junit4"))
        }
    }
}
