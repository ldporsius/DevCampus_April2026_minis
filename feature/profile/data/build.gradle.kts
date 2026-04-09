plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "nl.codingwithlinda.adaptivedesigntokens.feature.profile.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":feature:profile:domain"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.android)
}