import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.unknown.convention.libs

class AndroidDomainConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply{
                apply("bmaterial.android.library")
                apply("bmaterial.android.library.jacoco")
                apply("com.google.devtools.ksp")
            }

            dependencies{
                // them cac modules vao day

                add("implementation", libs.findLibrary("javax.inject").get())
            }
        }
    }
}