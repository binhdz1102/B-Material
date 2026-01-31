plugins {
    alias(libs.plugins.bmaterial.android.library)
    alias(libs.plugins.fatAar)
}

android {
    namespace = "com.b231001.bmaterial.bundle"
}

dependencies {
    embed(projects.uiCore.tokens)
    embed(projects.uiCore.resources)

    embed(projects.uiComponents.button)
    embed(projects.uiComponents.slider)
    embed(projects.uiComponents.text)
    embed(projects.uiComponents.card)
    embed(projects.uiComponents.snackbar)
    embed(projects.uiComponents.listitem)
    embed(projects.uiComponents.dialog)
    embed(projects.uiComponents.chip)
    embed(projects.uiComponents.switch)
    embed(projects.uiComponents.checkbox)
    embed(projects.uiComponents.loading)
    embed(projects.uiComponents.layout)
    embed(projects.uiComponents.scrollbar)
}
