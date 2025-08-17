plugins {
    alias(libs.plugins.pockizzy.android.application)
    alias(libs.plugins.pockizzy.android.application.compose)
    alias(libs.plugins.pockizzy.android.application.jacoco)
    alias(libs.plugins.pockizzy.android.hilt)
    alias(libs.plugins.pockizzy.android.navigation)
}

android {
    namespace = "com.b231001.bmaterial"

    defaultConfig {
        applicationId = "com.b231001.bmaterial"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.core.ktx)
}