package com.unknown.convention

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Locale

private val coverageExclusions = listOf(
    // Android
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*_Hilt*.class",
    "**/Hilt_*.class",
)

private val coverageInclusions = listOf(
    // Android
    "**/*UseCase*.*",
    "**/*ViewModel*.*",
)

private fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

internal fun Project.configureJacoco(
    androidComponentsExtension: AndroidComponentsExtension<*, *, *>,
) {
    configure<JacocoPluginExtension> {
        toolVersion = libs.findVersion("jacoco").get().toString()
    }

    val jacocoTestReport = tasks.create("jacocoTestReport")
    val buildDir = layout.buildDirectory.get().asFile

    androidComponentsExtension.onVariants { variant ->
        val testTaskName = "test${variant.name.capitalize()}UnitTest"

        val reportTask =
            tasks.register("jacoco${testTaskName.capitalize()}Report", JacocoReport::class) {
                dependsOn(testTaskName)

                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }

                classDirectories.setFrom(
                    fileTree("$buildDir/tmp/kotlin-classes/${variant.name}"){
                        exclude(coverageExclusions)
                        include(coverageInclusions)
                    }
                )
            }

        jacocoTestReport.dependsOn(reportTask)
    }

    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            // Required for JaCoCo + Robolectric
            // https://github.com/robolectric/robolectric/issues/2230
            isIncludeNoLocationClasses = true

            // Required for JDK 11 with the above
            // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
            excludes = listOf("jdk.internal.*")
        }
    }
}


internal fun Project.configureJacocoFullReport(
    androidComponentsExtension: AndroidComponentsExtension<*, *, *>,
) {
    configure<JacocoPluginExtension> {
        toolVersion = libs.findVersion("jacoco").get().toString()
    }

    val jacocoFullReport = tasks.create("jacocoFullReport")
    val buildDir = layout.buildDirectory.get().asFile

    androidComponentsExtension.onVariants { variant ->
        val testTaskName = "test${variant.name.capitalize()}UnitTest"
        val jacocoReportTaskName ="jacoco${testTaskName.capitalize()}Report"
        val fullJacocoReportTask =
            tasks.register<JacocoReport> ("jacoco${testTaskName.capitalize()}FullReport") {
                group = "Verification"
                description = "Generate Jacoco aggregate report for all modules"

                val projects =
                    rootProject.subprojects.filter {it.tasks.names.contains(jacocoReportTaskName) }

                dependsOn(projects.map{ it.tasks.named(jacocoReportTaskName)})

                val sourceDirs =
                    projects.flatMap{ it.tasks.getByName<JacocoReport>(jacocoReportTaskName).sourceDirectories}
                sourceDirectories.setFrom(sourceDirs)

                val classDirs =
                    projects.flatMap { it.tasks.getByName<JacocoReport>(jacocoReportTaskName).classDirectories }
                classDirectories.setFrom(classDirs)

                val execData =
                    projects.flatMap { it.tasks.getByName<JacocoReport>(jacocoReportTaskName).executionData }
                executionData.setFrom(execData)

                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }
            }

        val fullAndroidTestReport =
            tasks.register<TestReport>("android${testTaskName.capitalize()}Report"){
                destinationDirectory.set(file("$buildDir/reports/tests/$testTaskName"))

                @Suppress("DEPRECATION")
                reportOn(
                    rootProject.subprojects.map{
                        it.tasks.withType(Test::class).filter{ test -> test.name == testTaskName}
                    },
                )
            }

        jacocoFullReport.dependsOn(fullJacocoReportTask, fullAndroidTestReport)
    }
}















