import androidx.room.gradle.RoomExtension
import com.jcbsh.weather.buildlogic.libs
import com.jcbsh.weather.buildlogic.library
import com.jcbsh.weather.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("room"))
        pluginManager.apply(libs.pluginId("ksp"))
        extensions.configure<RoomExtension> {
            schemaDirectory("$projectDir/schemas")
        }
        dependencies {
            "implementation"(libs.library("androidx-room-runtime"))
            "implementation"(libs.library("androidx-room-ktx"))
            "ksp"(libs.library("androidx-room-compiler"))
            "testImplementation"(libs.library("androidx-room-testing"))
        }
    }
}
