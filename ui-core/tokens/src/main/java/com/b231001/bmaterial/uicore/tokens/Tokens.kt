package com.b231001.bmaterial.uicore.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class Tokens(
    val colorPalette: ColorPalette,
    val colorScheme: ColorScheme,
    val typography: BTypography,
    val shapes: BShapes,
    val sizes: BSizes,
    val paddings: BPaddings
)

val LocalTokens = staticCompositionLocalOf<Tokens> {
    error("Tokens is not provided")
}

object BTokens {
    val typography: BTypography
        @Composable get() = LocalTokens.current.typography

    val colorScheme: ColorScheme
        @Composable get() = LocalTokens.current.colorScheme

    val colorPalette: ColorPalette
        @Composable get() = LocalTokens.current.colorPalette

    val shapes: BShapes
        @Composable get() = LocalTokens.current.shapes

    val sizes: BSizes
        @Composable get() = LocalTokens.current.sizes

    val paddings: BPaddings
        @Composable get() = LocalTokens.current.paddings
}
