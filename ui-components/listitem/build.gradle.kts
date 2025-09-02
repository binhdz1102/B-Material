plugins {
    alias(libs.plugins.bmaterial.android.library)
    alias(libs.plugins.bmaterial.android.library.compose)
}

android {
    namespace = "com.b231001.bmaterial.uicomponents.listitem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
