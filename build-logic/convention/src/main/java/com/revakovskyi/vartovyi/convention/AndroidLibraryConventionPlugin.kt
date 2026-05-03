package com.revakovskyi.vartovyi.convention

import com.android.build.api.dsl.LibraryExtension
import com.revakovskyi.vartovyi.convention.application.configureKotlinAndroid
import com.revakovskyi.vartovyi.convention.application.configureVartovyiLibraryBuildTypes
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                configureVartovyiLibraryBuildTypes(this)
            }

            dependencies {
                "testImplementation"(kotlin("test"))
            }
        }
    }
}
