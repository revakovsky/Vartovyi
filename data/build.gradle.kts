plugins {
    alias(libs.plugins.vartovyi.android.library)
    alias(libs.plugins.vartovyi.android.room)
}

android {
    namespace = "com.revakovskyi.vartovyi.data"
}

dependencies {
    implementation(projects.domain)

    implementation(libs.koin.android)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.kotlinx.serialization.json)
}
