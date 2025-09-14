package com.b231001.bmaterial.uicore.tokens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,

    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,

    // Neutrals / surfaces
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,

    // Error
    val error: Color,
    val onError: Color,

    // Extra semantics
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color,

    // State overlays
    val overlayPressed: Color,
    val overlayHover: Color,
    val overlayFocus: Color,

    // Tonal surfaces
    val surface1: Color,
    val surface2: Color,
    val surface3: Color
)

fun lightColorScheme(
    primary: Color = colorPaletteDefault.blue.c40,
    onPrimary: Color = Color.White,
    primaryContainer: Color = colorPaletteDefault.blue.c90,
    onPrimaryContainer: Color = colorPaletteDefault.blue.c10,
    secondary: Color = colorPaletteDefault.teal.c40,
    onSecondary: Color = Color.White,
    secondaryContainer: Color = colorPaletteDefault.teal.c90,
    onSecondaryContainer: Color = colorPaletteDefault.teal.c10,
    tertiary: Color = colorPaletteDefault.purple.c40,
    onTertiary: Color = Color.White,
    tertiaryContainer: Color = colorPaletteDefault.purple.c90,
    onTertiaryContainer: Color = colorPaletteDefault.purple.c10,
    background: Color = colorPaletteDefault.neutral.c99 ?: Color(0xFFFFFBFE),
    onBackground: Color = colorPaletteDefault.neutral.c10,
    surface: Color = colorPaletteDefault.neutral.c99 ?: Color(0xFFFFFBFE),
    onSurface: Color = colorPaletteDefault.neutral.c10,
    surfaceVariant: Color = colorPaletteDefault.neutralVariant.c90,
    onSurfaceVariant: Color = colorPaletteDefault.neutralVariant.c30,
    outline: Color = colorPaletteDefault.neutralVariant.c40,
    outlineVariant: Color = colorPaletteDefault.neutralVariant.c80,
    error: Color = colorPaletteDefault.red.c40,
    onError: Color = Color.White,
    success: Color = colorPaletteDefault.green.c40,
    onSuccess: Color = Color.White,
    warning: Color = colorPaletteDefault.orange.c40,
    onWarning: Color = Color.White,
    info: Color = colorPaletteDefault.teal.c40,
    onInfo: Color = Color.White,
    overlayPressed: Color = Color(0xFF000000).copy(alpha = 0.12f),
    overlayHover: Color = Color(0xFF000000).copy(alpha = 0.08f),
    overlayFocus: Color = Color(0xFF000000).copy(alpha = 0.12f),
    surface1: Color = colorPaletteDefault.neutral.c99 ?: Color.White,
    surface2: Color = colorPaletteDefault.neutral.c95 ?: Color(0xFFF5F5F7),
    surface3: Color = colorPaletteDefault.neutral.c90
): ColorScheme = ColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    error = error,
    onError = onError,
    success = success,
    onSuccess = onSuccess,
    warning = warning,
    onWarning = onWarning,
    info = info,
    onInfo = onInfo,
    overlayPressed = overlayPressed,
    overlayHover = overlayHover,
    overlayFocus = overlayFocus,
    surface1 = surface1,
    surface2 = surface2,
    surface3 = surface3
)

fun darkColorScheme(
    primary: Color = colorPaletteDefault.blue.c80,
    onPrimary: Color = colorPaletteDefault.blue.c20,
    primaryContainer: Color = colorPaletteDefault.blue.c30,
    onPrimaryContainer: Color = colorPaletteDefault.blue.c90,
    secondary: Color = colorPaletteDefault.teal.c80,
    onSecondary: Color = colorPaletteDefault.teal.c20,
    secondaryContainer: Color = colorPaletteDefault.teal.c30,
    onSecondaryContainer: Color = colorPaletteDefault.teal.c90,
    tertiary: Color = colorPaletteDefault.purple.c80,
    onTertiary: Color = colorPaletteDefault.purple.c20,
    tertiaryContainer: Color = colorPaletteDefault.purple.c30,
    onTertiaryContainer: Color = colorPaletteDefault.purple.c90,
    background: Color = colorPaletteDefault.neutral.c10,
    onBackground: Color = colorPaletteDefault.neutral.c90,
    surface: Color = colorPaletteDefault.neutral.c10,
    onSurface: Color = colorPaletteDefault.neutral.c90,
    surfaceVariant: Color = colorPaletteDefault.neutralVariant.c30,
    onSurfaceVariant: Color = colorPaletteDefault.neutralVariant.c80,
    outline: Color = colorPaletteDefault.neutralVariant.c80,
    outlineVariant: Color = colorPaletteDefault.neutralVariant.c30,
    error: Color = colorPaletteDefault.red.c80,
    onError: Color = colorPaletteDefault.red.c20,
    success: Color = colorPaletteDefault.green.c80,
    onSuccess: Color = colorPaletteDefault.green.c20,
    warning: Color = colorPaletteDefault.orange.c80,
    onWarning: Color = colorPaletteDefault.orange.c20,
    info: Color = colorPaletteDefault.teal.c80,
    onInfo: Color = colorPaletteDefault.teal.c20,
    overlayPressed: Color = Color(0xFFFFFFFF).copy(alpha = 0.16f),
    overlayHover: Color = Color(0xFFFFFFFF).copy(alpha = 0.10f),
    overlayFocus: Color = Color(0xFFFFFFFF).copy(alpha = 0.16f),
    surface1: Color = colorPaletteDefault.neutral.c20,
    surface2: Color = colorPaletteDefault.neutral.c30,
    surface3: Color = colorPaletteDefault.neutral.c40
): ColorScheme = ColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    error = error,
    onError = onError,
    success = success,
    onSuccess = onSuccess,
    warning = warning,
    onWarning = onWarning,
    info = info,
    onInfo = onInfo,
    overlayPressed = overlayPressed,
    overlayHover = overlayHover,
    overlayFocus = overlayFocus,
    surface1 = surface1,
    surface2 = surface2,
    surface3 = surface3
)

/**
 * Provides access to the current ColorScheme (light/dark).
 */
val LocalColorScheme = staticCompositionLocalOf<ColorScheme> {
    error("ColorScheme is not provided!")
}

@Composable
fun BTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    palette: ColorPalette = colorPaletteDefault,
    typography: BTypography = BTypographyDefault,
    shapes: BShapes = BShapesDefault,
    sizes: BSizes = BSizesDefault,
    paddings: BPaddings = BPaddingsDefault,
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    val tokens = Tokens(
        colorPalette = palette,
        colorScheme = scheme,
        typography = typography,
        shapes = shapes,
        sizes = sizes,
        paddings = paddings
    )

    CompositionLocalProvider(
        LocalTokens provides tokens
    ) {
        content()
    }
}
