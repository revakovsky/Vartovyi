package com.revakovskyi.vartovyi.convention.application

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private val javaVersion = JavaVersion.VERSION_11
private val localJvmTarget = JvmTarget.JVM_11

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    val catalog = libs
    commonExtension.compileSdk {
        version = release(catalog.findVersion("compileSdk").get().toString().toInt()) {
            minorApiLevel = catalog.findVersion("compileSdkMinorApi").get().toString().toInt()
        }
    }
    commonExtension.defaultConfig.minSdk = catalog.findVersion("minSdk").get().toString().toInt()
    commonExtension.compileOptions.sourceCompatibility = javaVersion
    commonExtension.compileOptions.targetCompatibility = javaVersion
    configureKotlin()
}

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    configureKotlin()
}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(localJvmTarget)
        }
    }
}
