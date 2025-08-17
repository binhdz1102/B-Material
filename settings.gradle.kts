pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "B-Material"

include(":app")
include(":ui-core:tokens")
include(":ui-core:foundation")
include(":ui-core:resources")
include(":ui-components:button")
include(":ui-components:slider")
include(":ui-components:text")
include(":ui-components:card")
include(":ui-components:indicator")
include(":ui-components:navigation")
include(":ui-components:textfield")
include(":ui-components:snackbar")
include(":ui-components:listitem")
