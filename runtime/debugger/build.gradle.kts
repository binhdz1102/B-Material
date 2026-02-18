plugins {
    alias(libs.plugins.bmaterial.android.library)
    alias(libs.plugins.bmaterial.android.library.compose)
    alias(libs.plugins.bmaterial.android.publish)
}

android {
    namespace = "com.b231001.bmaterial.runtime.debugger"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
