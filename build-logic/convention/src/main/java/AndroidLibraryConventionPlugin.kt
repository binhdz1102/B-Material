import com.android.build.gradle.LibraryExtension
import com.unknown.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import com.unknown.convention.configureKotlinAndroid
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

                buildTypes.named("release").configure {
                    isMinifyEnabled = true
                    consumerProguardFiles(
                        rootProject.file("config/proguard/library-consumer-rules.pro")
                    )
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        rootProject.file("config/proguard/library-proguard-rules.pro")
                    )
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                explicitApiWarning()
            }

            dependencies{
                add("androidTestImplementation", kotlin("test"))
                add("testImplementation", kotlin("test"))
            }
        }
    }
}
