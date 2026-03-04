# B-Material

**B-Material** is a Material Design component library for **Jetpack Compose** application development.

It provides reusable **design tokens, runtime utilities, and UI components** to help build consistent Android UI faster.

---

# Demonstration
<p align="center">
  <img src="demo.gif" width="200"/>
</p>

---

# Installation Guide

B-Material is distributed through **GitHub Packages**.

Follow the steps below to integrate it into your project.

---

# 1. Configure Version Catalog

Add the following configuration to your `libs.versions.toml`:

```toml
[versions]
bmaterial = "1.1.0"

[libraries]
bmaterial-ui-core-tokens = { group = "com.b231001.bmaterial", name = "ui-core-tokens", version.ref = "bmaterial" }
bmaterial-ui-core-resources = { group = "com.b231001.bmaterial", name = "ui-core-resources", version.ref = "bmaterial" }
bmaterial-ui-core-foundation = { group = "com.b231001.bmaterial", name = "ui-core-foundation", version.ref = "bmaterial" }

bmaterial-runtime-ktx = { group = "com.b231001.bmaterial", name = "runtime-ktx", version.ref = "bmaterial" }
bmaterial-runtime-flow = { group = "com.b231001.bmaterial", name = "runtime-flow", version.ref = "bmaterial" }
bmaterial-runtime-debugger = { group = "com.b231001.bmaterial", name = "runtime-debugger", version.ref = "bmaterial" }

bmaterial-ui-components-button = { group = "com.b231001.bmaterial", name = "ui-components-button", version.ref = "bmaterial" }
bmaterial-ui-components-slider = { group = "com.b231001.bmaterial", name = "ui-components-slider", version.ref = "bmaterial" }
bmaterial-ui-components-card = { group = "com.b231001.bmaterial", name = "ui-components-card", version.ref = "bmaterial" }
bmaterial-ui-components-listitem = { group = "com.b231001.bmaterial", name = "ui-components-listitem", version.ref = "bmaterial" }
bmaterial-ui-components-chip = { group = "com.b231001.bmaterial", name = "ui-components-chip", version.ref = "bmaterial" }
bmaterial-ui-components-switch = { group = "com.b231001.bmaterial", name = "ui-components-switch", version.ref = "bmaterial" }
bmaterial-ui-components-checkbox = { group = "com.b231001.bmaterial", name = "ui-components-checkbox", version.ref = "bmaterial" }
bmaterial-ui-components-loading = { group = "com.b231001.bmaterial", name = "ui-components-loading", version.ref = "bmaterial" }
bmaterial-ui-components-layout = { group = "com.b231001.bmaterial", name = "ui-components-layout", version.ref = "bmaterial" }
bmaterial-ui-components-scrollbar = { group = "com.b231001.bmaterial", name = "ui-components-scrollbar", version.ref = "bmaterial" }

[bundles]
bmaterialRuntime = [
    "bmaterial-runtime-ktx",
    "bmaterial-runtime-flow",
    "bmaterial-runtime-debugger"
]

bmaterialUiCore = [
    "bmaterial-ui-core-tokens",
    "bmaterial-ui-core-resources",
    "bmaterial-ui-core-foundation"
]

bmaterialUiComponents = [
    "bmaterial-ui-components-button",
    "bmaterial-ui-components-slider",
    "bmaterial-ui-components-card",
    "bmaterial-ui-components-listitem",
    "bmaterial-ui-components-chip",
    "bmaterial-ui-components-switch",
    "bmaterial-ui-components-checkbox",
    "bmaterial-ui-components-loading",
    "bmaterial-ui-components-layout",
    "bmaterial-ui-components-scrollbar"
]
```

# 2. Configure Repository
Add GitHub Packages repository to settings.gradle.kts:

```kotlin
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        val gprUser = providers.gradleProperty("gpr.user").orNull
            ?: System.getenv("GPR_USER")

        val gprKey = providers.gradleProperty("gpr.key").orNull
            ?: System.getenv("GPR_KEY")

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/binhdz1102/B-Material")

            credentials {
                username = gprUser
                password = gprKey
            }
        }
    }
}
```

# 3. Configure Environment Variables (For Private Repository)
Use the bundles in your module build.gradle.kts:
```kotlin
GPR_USER=your_github_username
GPR_KEY=your_github_personal_access_token
```

# 4. Add Dependencies
Use the bundles in your module build.gradle.kts:
```kotlin
dependencies {

    // Runtime utilities
    implementation(libs.bundles.bmaterialRuntime)

    // Core UI modules
    implementation(libs.bundles.bmaterialUiCore)

    // Compose UI components
    implementation(libs.bundles.bmaterialUiComponents)

}
```