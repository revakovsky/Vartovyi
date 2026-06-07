plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.junit5) apply false
}

apply(from = "gradle/detekt.gradle")

gradle.projectsEvaluated {
    val unitTestTasks = subprojects.flatMap { subProject ->
        subProject.tasks.withType(Test::class.java)
    }.filter { testTask ->
        testTask.name == "test" || testTask.name.endsWith("DebugUnitTest")
    }

    tasks.register("testAllUnitTests") {
        group = "verification"
        description = "Runs every unit test in every module from scratch, without caching"
        dependsOn(unitTestTasks)
        unitTestTasks.forEach { testTask ->
            testTask.doNotTrackState("Forced re-run: testAllUnitTests always runs from scratch")
        }
    }
}
