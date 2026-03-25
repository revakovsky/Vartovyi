plugins {
    `kotlin-dsl`
}

group = "com.revakovskyi.vartovyi.buildlogic"

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "vartovyi.android.application"
            implementationClass = "com.revakovskyi.vartovyi.convention.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "vartovyi.android.application.compose"
            implementationClass = "com.revakovskyi.vartovyi.convention.AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "vartovyi.android.library"
            implementationClass = "com.revakovskyi.vartovyi.convention.AndroidLibraryConventionPlugin"
        }
        register("jvmLibrary") {
            id = "vartovyi.jvm.library"
            implementationClass = "com.revakovskyi.vartovyi.convention.JvmLibraryConventionPlugin"
        }
        register("androidRoom") {
            id = "vartovyi.android.room"
            implementationClass = "com.revakovskyi.vartovyi.convention.AndroidRoomConventionPlugin"
        }
    }
}
