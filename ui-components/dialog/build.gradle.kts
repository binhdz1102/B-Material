plugins {
    alias(libs.plugins.bmaterial.android.library)
    alias(libs.plugins.bmaterial.android.library.compose)
}

android {
    namespace = "com.b231001.bmaterial.uicomponents.dialog"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.uiCore.tokens)
    implementation(projects.uiComponents.button)

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.6")

    // SavedState
    implementation("androidx.savedstate:savedstate-ktx:1.2.1")
    implementation("androidx.compose.ui:ui-viewbinding")
}
