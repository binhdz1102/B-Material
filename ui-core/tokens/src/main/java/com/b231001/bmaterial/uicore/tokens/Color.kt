package com.b231001.bmaterial.uicore.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorModel(
    val c10: Color,
    val c20: Color,
    val c30: Color,
    val c40: Color,
    val c80: Color,
    val c90: Color,
    val c95: Color? = null,
    val c99: Color? = null
)

private val BlueTones = ColorModel(
    c10 = Color(0xFF001F28),
    c20 = Color(0xFF003544),
    c30 = Color(0xFF004D61),
    c40 = Color(0xFF006780),
    c80 = Color(0xFF5DD5FC),
    c90 = Color(0xFFB8EAFF)
)

private val GreenTones = ColorModel(
    c10 = Color(0xFF00210B),
    c20 = Color(0xFF003919),
    c30 = Color(0xFF005227),
    c40 = Color(0xFF006D36),
    c80 = Color(0xFF0EE37C),
    c90 = Color(0xFF5AFF9D)
)

private val OrangeTones = ColorModel(
    c10 = Color(0xFF380D00),
    c20 = Color(0xFF5B1A00),
    c30 = Color(0xFF812800),
    c40 = Color(0xFFA23F16),
    c80 = Color(0xFFFFB59B),
    c90 = Color(0xFFFFDBCF)
)

private val PurpleTones = ColorModel(
    c10 = Color(0xFF36003C),
    c20 = Color(0xFF560A5D),
    c30 = Color(0xFF702776),
    c40 = Color(0xFF8B418F),
    c80 = Color(0xFFFFA9FE),
    c90 = Color(0xFFFFD6FA)
)

private val TealTones = ColorModel(
    c10 = Color(0xFF001F26),
    c20 = Color(0xFF02363F),
    c30 = Color(0xFF214D56),
    c40 = Color(0xFF3A656F),
    c80 = Color(0xFFA2CED9),
    c90 = Color(0xFFBEEAF6)
)

private val RedTones = ColorModel(
    c10 = Color(0xFF410002),
    c20 = Color(0xFF690005),
    c30 = Color(0xFF93000A),
    c40 = Color(0xFFBA1A1A),
    c80 = Color(0xFFFFB4AB),
    c90 = Color(0xFFFFDAD6)
)

private val NeutralTones = ColorModel(
    c10 = Color(0xFF1A1C1A),
    c20 = Color(0xFF2F312E),
    c30 = Color(0xFF414941),
    c40 = Color(0xFF4D444C),
    c80 = Color(0xFFC1C9BF),
    c90 = Color(0xFFE2E3DE),
    c95 = Color(0xFFF0F1EC),
    c99 = Color(0xFFFBFDF7)
)

private val NeutralVariantTones = ColorModel(
    c10 = Color(0xFF201A1B),
    c20 = Color(0xFF362F30),
    c30 = Color(0xFF4D444C),
    c40 = Color(0xFF727971),
    c80 = Color(0xFFD0C3CC),
    c90 = Color(0xFFEDDEE8),
    c95 = Color(0xFFFAEEEF),
    c99 = Color(0xFFFCFCFC)
)

/**
 * The list of colors to be used in the app via the color palette
 */
@Immutable
data class ColorPalette(
    val blue: ColorModel,
    val green: ColorModel,
    val orange: ColorModel,
    val purple: ColorModel,
    val teal: ColorModel,
    val red: ColorModel,
    val neutral: ColorModel,
    val neutralVariant: ColorModel
)

internal val colorPaletteDefault = ColorPalette(
    blue = BlueTones,
    green = GreenTones,
    orange = OrangeTones,
    purple = PurpleTones,
    teal = TealTones,
    red = RedTones,
    neutral = NeutralTones,
    neutralVariant = NeutralVariantTones
)
