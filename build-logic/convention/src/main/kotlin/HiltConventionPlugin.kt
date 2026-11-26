import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.library
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("ksp"))
        dependencies {
            "ksp"(libs.library("hilt-compiler"))
        }
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            dependencies { "implementation"(libs.library("hilt-core")) }
        }
        pluginManager.withPlugin("com.android.base") {
            pluginManager.apply(libs.pluginId("hilt"))
            dependencies { "implementation"(libs.library("hilt-android")) }
        }
    }
}
