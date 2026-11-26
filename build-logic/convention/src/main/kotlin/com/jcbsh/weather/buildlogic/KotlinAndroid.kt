package com.jcbsh.weather.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

const val CompileSdk = 37
const val CompileSdkMinor = 2
const val MinSdk = 26
const val JdkVersion = 21

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk {
            version = release(CompileSdk) { minorApiLevel = CompileSdkMinor }
        }
        defaultConfig.minSdk = MinSdk
        compileOptions.sourceCompatibility = JavaVersion.VERSION_21
        compileOptions.targetCompatibility = JavaVersion.VERSION_21
        testOptions.unitTests.isIncludeAndroidResources = true
        lint.apply {
            warningsAsErrors = true
            abortOnError = true
            checkDependencies = false
            // Version-freshness checks depend on the day the build runs, not on the change.
            disable += setOf("GradleDependency", "AndroidGradlePluginVersion", "NewerVersionAvailable", "OldTargetApi")
        }
    }
    configureKotlin()
}

internal fun Project.configureKotlin() {
    extensions.configure<KotlinProjectExtension> {
        jvmToolchain(JdkVersion)
    }
}
