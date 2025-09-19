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

rootProject.name = "B-Material"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":ui-core:tokens")
include(":ui-core:resources")
include(":ui-core:foundation")
include(":ui-components:button")
include(":ui-components:slider")
include(":ui-components:text")
include(":ui-components:card")
include(":ui-components:indicator")
include(":ui-components:navigation")
include(":ui-components:textfield")
include(":ui-components:snackbar")
include(":ui-components:listitem")
include(":ui-components:dialog")
include(":ui-components:bottomsheet")
include(":ui-components:chip")
include(":ui-components:switch")
