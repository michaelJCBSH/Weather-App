import com.android.build.api.dsl.ApplicationExtension
import com.jcbsh.weather.buildlogic.CompileSdk
import com.jcbsh.weather.buildlogic.configureKotlinAndroid
import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("android-application"))
        pluginManager.apply(libs.pluginId("weather-detekt"))
        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = CompileSdk
        }
    }
}
