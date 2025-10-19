plugins {
    alias(libs.plugins.bmaterial.android.library)
}

android {
    namespace = "com.b231001.bmaterial.runtime.ktx"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
