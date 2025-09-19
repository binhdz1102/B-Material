plugins {
    alias(libs.plugins.bmaterial.android.application)
    alias(libs.plugins.bmaterial.android.application.compose)
    alias(libs.plugins.bmaterial.android.application.jacoco)
    alias(libs.plugins.bmaterial.android.hilt)
    alias(libs.plugins.bmaterial.android.navigation)
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
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.core.ktx)

    implementation(projects.uiCore.tokens)
    implementation(projects.uiCore.resources)
    implementation(projects.uiCore.foundation)

    implementation(projects.uiComponents.button)
    implementation(projects.uiComponents.switch)
}
