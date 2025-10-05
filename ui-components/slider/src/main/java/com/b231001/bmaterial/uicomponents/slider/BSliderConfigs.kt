package com.b231001.bmaterial.uicomponents.slider

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens

@Stable
sealed interface BSliderStyle {
    data object Primary : BSliderStyle
    data object Destructive : BSliderStyle
    data object Success : BSliderStyle
    data object Warning : BSliderStyle
    data object Info : BSliderStyle
}

@Stable
enum class BSliderSize { Sm, Md, Lg }

@Stable
data class BSliderColors(
    val trackActive: Color,
    val trackInactive: Color,
    val thumb: Color,
    val tickActive: Color,
    val tickInactive: Color,
    val disabledTrack: Color,
    val disabledThumb: Color
)

@Stable
data class BSliderMetrics(
    val trackHeight: Dp,
    val thumbSize: Dp,
    val trackShape: Shape,
    val focusHaloRadius: Dp,
    val tickSize: Dp
)

object BSliderDefaults {
    @Composable
    fun colors(style: BSliderStyle): BSliderColors {
        val cs = BTokens.colorScheme
        val (trackOnColor, thumbOnColor) = when (style) {
            BSliderStyle.Primary -> cs.primary to cs.onPrimary
            BSliderStyle.Destructive -> cs.error to cs.onError
            BSliderStyle.Success -> cs.success to cs.onSuccess
            BSliderStyle.Warning -> cs.warning to cs.onWarning
            BSliderStyle.Info -> cs.info to cs.onInfo
        }

        val thumbColor = if (thumbOnColor == cs.onPrimary || thumbOnColor == cs.onError) {
            Color.White
        } else {
            cs.surface1
        }

        val trackOffColor = cs.surfaceVariant
        return BSliderColors(
            trackActive = trackOnColor,
            trackInactive = trackOffColor,
            thumb = thumbColor,
            tickActive = trackOnColor,
            tickInactive = trackOffColor,
            disabledTrack = cs.onSurface.copy(alpha = 0.12f),
            disabledThumb = cs.onSurface.copy(alpha = 0.38f)
        )
    }

    @Composable
    fun metrics(size: BSliderSize): BSliderMetrics =
        when (size) {
            BSliderSize.Sm -> BSliderMetrics(
                trackHeight = 2.dp,
                thumbSize = 16.dp,
                trackShape = RoundedCornerShape(percent = 50),
                focusHaloRadius = 12.dp,
                tickSize = 2.dp
            )

            BSliderSize.Md -> BSliderMetrics(
                trackHeight = 4.dp,
                thumbSize = 24.dp,
                trackShape = RoundedCornerShape(percent = 50),
                focusHaloRadius = 16.dp,
                tickSize = 3.dp
            )

            BSliderSize.Lg -> BSliderMetrics(
                trackHeight = 6.dp,
                thumbSize = 32.dp,
                trackShape = RoundedCornerShape(percent = 50),
                focusHaloRadius = 20.dp,
                tickSize = 4.dp
            )
        }
}
