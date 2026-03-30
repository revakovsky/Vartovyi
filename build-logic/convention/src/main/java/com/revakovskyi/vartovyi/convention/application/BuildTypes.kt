package com.revakovskyi.vartovyi.convention.application

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

internal fun configureVartovyiApplicationBuildTypes(
    project: Project,
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.buildTypes.getByName("debug") {
        applicationIdSuffix = ".debug"
        isMinifyEnabled = false
        proguardFiles(
            applicationExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
    applicationExtension.buildTypes.getByName("release") {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            applicationExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}

internal fun configureVartovyiLibraryBuildTypes(
    libraryExtension: LibraryExtension,
) {
    libraryExtension.buildTypes.getByName("release") {
        isMinifyEnabled = false
        proguardFiles(
            libraryExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
