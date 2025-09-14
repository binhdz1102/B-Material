package com.b231001.bmaterial.uicore.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class BPaddings(
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
)

val BPaddingsDefault = BPaddings(
    extraSmall = 2.dp,
    small = 4.dp,
    medium = 8.dp,
    large = 16.dp,
    extraLarge = 24.dp
)
