plugins {
    alias(libs.plugins.bmaterial.android.library)
}

android {
    namespace = "com.b231001.bmaterial.runtime.flow"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
