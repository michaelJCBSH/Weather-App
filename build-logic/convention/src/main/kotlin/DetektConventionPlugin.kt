import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.library
import com.jcbsh.weather.buildlogic.pluginId
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("detekt"))
        extensions.configure<DetektExtension> {
            config.setFrom(rootProject.layout.projectDirectory.file("detekt-config.yml"))
            buildUponDefaultConfig.set(true)
            parallel.set(true)
        }
        dependencies {
            "detektPlugins"(libs.library("detekt-ktlint-wrapper"))
        }
    }
}
