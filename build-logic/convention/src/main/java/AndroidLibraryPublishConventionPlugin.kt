import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

class AndroidLibraryPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        // Only apply to library modules, not app
        pluginManager.withPlugin("com.android.library") {
            pluginManager.apply("maven-publish")

            extensions.configure<LibraryExtension> {
                publishing {
                    singleVariant("release") {
                        withSourcesJar()
                    }
                }
            }

            // Credentials take from ~/.gradle/gradle.properties
            val userProvider = providers.gradleProperty("gpr.user")
                .orElse(providers.environmentVariable("GITHUB_ACTOR"))
            val keyProvider = providers.gradleProperty("gpr.key")
                .orElse(providers.environmentVariable("GITHUB_TOKEN"))

            // You set these 2 properties in gradle.properties (root project) or hardcode
            val owner = providers.gradleProperty("gpr.owner").orElse(userProvider)
            val repo = providers.gradleProperty("gpr.repo").orElse("B-Material")

            // Maven coordinates: groupId: com.b231001.bmaterial, artifactId: ui-core-tokens, version: x.y.z
            val groupIdValue = providers.gradleProperty("POM_GROUP_ID")
                .orElse("com.b231001.bmaterial")
            val versionValue = providers.gradleProperty("POM_VERSION")
                .orElse("1.2.0")

            // artifactId: ui-core:tokens -> ui-core-tokens
            val artifactIdValue = path.removePrefix(":").replace(":", "-")

            afterEvaluate {
                extensions.configure<PublishingExtension> {
                    repositories {
                        maven {
                            name = "GitHubPackages"
                            url = uri("https://maven.pkg.github.com/${owner.get()}/${repo.get()}")
                            credentials {
                                username = userProvider.get()
                                password = keyProvider.get()
                            }
                        }
                    }

                    publications {
                        create<MavenPublication>("release") {
                            groupId = groupIdValue.get()
                            artifactId = artifactIdValue
                            version = versionValue.get()

                            // publish AAR release
                            from(components["release"])
                        }
                    }
                }
            }
        }
    }
}
