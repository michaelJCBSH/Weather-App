import com.android.build.api.dsl.CommonExtension
import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.library
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("compose-compiler"))
        extensions.getByType<CommonExtension>().buildFeatures.compose = true
        dependencies {
            val bom = platform(libs.library("androidx-compose-bom"))
            "implementation"(bom)
            "testImplementation"(bom)
            "implementation"(libs.library("androidx-compose-ui-tooling-preview"))
            "debugImplementation"(libs.library("androidx-compose-ui-tooling"))
            "lintChecks"(libs.library("compose-lint-checks"))
        }
    }
}
