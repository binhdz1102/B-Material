package com.b231001.bmaterial.uicore.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BSizes(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,

    val iconSmall: Dp,
    val iconMedium: Dp,
    val iconLarge: Dp
)

val BSizesDefault = BSizes(
    extraSmall = 2.dp,
    small = 4.dp,
    medium = 8.dp,
    large = 16.dp,
    extraLarge = 24.dp,

    iconSmall = 12.dp,
    iconMedium = 24.dp,
    iconLarge = 32.dp
)

val LocalSizes = staticCompositionLocalOf<BSizes> {
    error("BSizes is not provided!")
}
