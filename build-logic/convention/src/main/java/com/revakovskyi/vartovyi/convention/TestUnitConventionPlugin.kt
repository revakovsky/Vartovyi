package com.revakovskyi.vartovyi.convention

import com.revakovskyi.vartovyi.convention.application.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class TestUnitConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            val isAndroid = pluginManager.hasPlugin("com.android.application") ||
                pluginManager.hasPlugin("com.android.library")

            if (isAndroid) {
                pluginManager.apply("de.mannodermaus.android-junit5")
            }

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }

            dependencies {
                "testImplementation"(libs.findBundle("testing").get())
                "testRuntimeOnly"(libs.findLibrary("junit5-engine").get())
                "testRuntimeOnly"(libs.findLibrary("junit-platform-launcher").get())
            }
        }
    }
}
