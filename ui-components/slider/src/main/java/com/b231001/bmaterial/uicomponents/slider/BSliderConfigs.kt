package com.b231001.bmaterial.uicomponents.slider

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens

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
            cs.surface
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
            disabledTrack = cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContainer),
            disabledThumb = cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContent)
        )
    }

    @Composable
    fun metrics(size: BSliderSize): BSliderMetrics =
        when (size) {
            BSliderSize.Sm -> BSliderMetrics(
                trackHeight = ComponentTokens.Slider.SmTrackHeight,
                thumbSize = ComponentTokens.Slider.SmThumbSize,
                trackShape = RoundedCornerShape(
                    percent = ComponentTokens.Slider.TrackShapePercent
                ),
                focusHaloRadius = ComponentTokens.Slider.SmFocusHaloRadius,
                tickSize = ComponentTokens.Slider.SmTickSize
            )

            BSliderSize.Md -> BSliderMetrics(
                trackHeight = ComponentTokens.Slider.MdTrackHeight,
                thumbSize = ComponentTokens.Slider.MdThumbSize,
                trackShape = RoundedCornerShape(
                    percent = ComponentTokens.Slider.TrackShapePercent
                ),
                focusHaloRadius = ComponentTokens.Slider.MdFocusHaloRadius,
                tickSize = ComponentTokens.Slider.MdTickSize
            )

            BSliderSize.Lg -> BSliderMetrics(
                trackHeight = ComponentTokens.Slider.LgTrackHeight,
                thumbSize = ComponentTokens.Slider.LgThumbSize,
                trackShape = RoundedCornerShape(
                    percent = ComponentTokens.Slider.TrackShapePercent
                ),
                focusHaloRadius = ComponentTokens.Slider.LgFocusHaloRadius,
                tickSize = ComponentTokens.Slider.LgTickSize
            )
        }
}
