package com.revakovskyi.vartovyi.convention

import com.revakovskyi.vartovyi.convention.application.configureKotlinJvm
import com.revakovskyi.vartovyi.convention.application.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            configureKotlinJvm()

            dependencies {
                "implementation"(libs.findLibrary("kotlinx-coroutines-core").get())
                "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}
