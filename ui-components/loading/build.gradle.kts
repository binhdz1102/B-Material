plugins {
    alias(libs.plugins.bmaterial.android.library)
    alias(libs.plugins.bmaterial.android.library.compose)
    alias(libs.plugins.bmaterial.android.publish)
}

android {
    namespace = "com.b231001.bmaterial.uicomponents.loading"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.uiCore.tokens)
}
