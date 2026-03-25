package com.revakovskyi.vartovyi.convention

import com.android.build.api.dsl.ApplicationExtension
import com.revakovskyi.vartovyi.convention.application.configureKotlinAndroid
import com.revakovskyi.vartovyi.convention.application.configureVartovyiApplicationBuildTypes
import com.revakovskyi.vartovyi.convention.application.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    applicationId = libs.findVersion("appId").get().toString()
                    targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                    versionCode = libs.findVersion("versionCode").get().toString().toInt()
                    versionName = libs.findVersion("versionName").get().toString()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                configureKotlinAndroid(this)
                configureVartovyiApplicationBuildTypes(this)
            }
        }
    }
}
