import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class PaparazziConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("paparazzi"))
        dependencies {
            "testImplementation"(project(":core:testing"))
        }
    }
}
