plugins {
    alias(libs.plugins.bmaterial.android.library)
    alias(libs.plugins.bmaterial.android.library.compose)
    alias(libs.plugins.bmaterial.android.publish)
}

android {
    namespace = "com.b231001.bmaterial.uicomponents.scrollbar"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.animation.core)
    implementation(projects.uiCore.tokens)
}
