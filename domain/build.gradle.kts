plugins {
    alias(libs.plugins.vartovyi.jvm.library)
}

group = libs.versions.appId.get()
version = libs.versions.versionName.get()

dependencies {
    implementation(libs.koin.core)
    implementation(libs.androidx.paging.common)
}
