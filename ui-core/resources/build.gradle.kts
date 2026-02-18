plugins {
    alias(libs.plugins.bmaterial.android.library)
    alias(libs.plugins.bmaterial.android.library.compose)
    alias(libs.plugins.bmaterial.android.publish)
}

android {
    namespace = "com.b231001.bmaterial.uicore.resources"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
