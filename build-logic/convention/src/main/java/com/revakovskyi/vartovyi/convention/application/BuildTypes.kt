package com.revakovskyi.vartovyi.convention.application

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties

private const val RELEASE_SIGNING_CONFIG_NAME = "release"
private const val LOCAL_PROPERTIES_FILE = "local.properties"
private const val KEY_KEYSTORE_FILE = "KEYSTORE_FILE"
private const val KEY_KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD"
private const val KEY_KEY_ALIAS = "KEY_ALIAS"
private const val KEY_KEY_PASSWORD = "KEY_PASSWORD"

internal fun configureVartovyiApplicationBuildTypes(
    project: Project,
    applicationExtension: ApplicationExtension,
) {
    val keystoreFile = resolveSigningProperty(project, KEY_KEYSTORE_FILE)
    val keystorePassword = resolveSigningProperty(project, KEY_KEYSTORE_PASSWORD)
    val keyAlias = resolveSigningProperty(project, KEY_KEY_ALIAS)
    val keyPassword = resolveSigningProperty(project, KEY_KEY_PASSWORD)

    val hasSigningConfig = keystoreFile != null
            && keystorePassword != null
            && keyAlias != null
            && keyPassword != null

    if (hasSigningConfig) {
        applicationExtension.signingConfigs {
            create(RELEASE_SIGNING_CONFIG_NAME) {
                storeFile = project.file(keystoreFile)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    applicationExtension.buildTypes.getByName("debug") {
        applicationIdSuffix = ".debug"
        isMinifyEnabled = false
        proguardFiles(
            applicationExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }

    applicationExtension.buildTypes.getByName("release") {
        if (hasSigningConfig) {
            signingConfig = applicationExtension.signingConfigs.getByName(
                RELEASE_SIGNING_CONFIG_NAME
            )
        }
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            applicationExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}

private fun resolveSigningProperty(project: Project, key: String): String? {
    val envValue = System.getenv(key)
    if (!envValue.isNullOrBlank()) return envValue

    val localPropertiesFile = project.rootProject.file(LOCAL_PROPERTIES_FILE)
    if (!localPropertiesFile.exists()) return null

    val properties = Properties().apply {
        load(FileInputStream(localPropertiesFile))
    }

    return properties.getProperty(key)?.takeIf { it.isNotBlank() }
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
