plugins {
    alias(libs.plugins.vartovyi.android.library)
}

android {
    namespace = "com.revakovskyi.vartovyi.data"
}

dependencies {
    implementation(projects.domain)
}
