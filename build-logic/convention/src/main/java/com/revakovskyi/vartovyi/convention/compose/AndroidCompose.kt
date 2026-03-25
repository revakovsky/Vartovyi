package com.revakovskyi.vartovyi.convention.compose

import com.android.build.api.dsl.CommonExtension
import com.revakovskyi.vartovyi.convention.application.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidApplicationCompose(
    commonExtension: CommonExtension,
) {
    configureAndroidLibraryCompose(commonExtension)

    dependencies {
        "implementation"(libs.findBundle("lifecycle").get())
        "implementation"(libs.findBundle("navigation").get())
        "implementation"(libs.findBundle("koin").get())
    }
}

internal fun Project.configureAndroidLibraryCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.buildFeatures.compose = true

    dependencies {
        val composeBom = libs.findLibrary("androidx-compose-bom").get()
        "implementation"(this.platform(composeBom))
        "implementation"(libs.findBundle("compose").get())
        "debugImplementation"(libs.findBundle("compose-debug").get())
    }
}
