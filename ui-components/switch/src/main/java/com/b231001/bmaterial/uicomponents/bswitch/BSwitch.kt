package com.b231001.bmaterial.uicomponents.bswitch

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens

@Stable
sealed interface BSwitchStyle {
    data object Primary : BSwitchStyle

    data object Destructive : BSwitchStyle

    data object Success : BSwitchStyle

    data object Warning : BSwitchStyle

    data object Info : BSwitchStyle
}

@Stable
enum class BSwitchSize { Sm, Md, Lg }

@Stable
data class BSwitchColors(
    val trackOn: Color,
    val trackOff: Color,
    val thumbOn: Color,
    val thumbOff: Color,
    val borderOn: Color?,
    val borderOff: Color?,
    val disabledTrackOn: Color,
    val disabledTrackOff: Color,
    val disabledThumbOn: Color,
    val disabledThumbOff: Color
)

@Stable
data class BSwitchMetrics(
    val width: Dp,
    val height: Dp,
    val thumbDiameter: Dp,
    val thumbPadding: Dp,
    val trackShape: Shape,
    val focusRingRadius: Dp,
    val thumbElevation: Dp
)

object BSwitchDefaults {

    @Composable
    fun colors(style: BSwitchStyle): BSwitchColors {
        val cs = BTokens.colorScheme

        val disabledTrack = cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContainer)
        val disabledThumb = cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContent)

        val onTrack: Color
        val onThumb: Color

        when (style) {
            BSwitchStyle.Primary -> {
                onTrack = cs.primary
                onThumb = cs.onPrimary
            }

            BSwitchStyle.Destructive -> {
                onTrack = cs.error
                onThumb = cs.onError
            }

            BSwitchStyle.Success -> {
                onTrack = cs.success
                onThumb = cs.onSuccess
            }

            BSwitchStyle.Warning -> {
                onTrack = cs.warning
                onThumb = cs.onWarning
            }

            BSwitchStyle.Info -> {
                onTrack = cs.info
                onThumb = cs.onInfo
            }
        }

        val offTrack = cs.surfaceVariant
        val offThumb = cs.onSurface
        val borderOff = cs.outlineVariant
        val borderOn: Color? = null

        return BSwitchColors(
            trackOn = onTrack,
            trackOff = offTrack,
            thumbOn = cs.surface.takeIf { onThumb == cs.onPrimary || onThumb == cs.onError }
                ?: cs.surface1,
            thumbOff = offThumb,
            borderOn = borderOn,
            borderOff = borderOff,
            disabledTrackOn = disabledTrack,
            disabledTrackOff = disabledTrack,
            disabledThumbOn = disabledThumb,
            disabledThumbOff = disabledThumb
        )
    }

    @Composable
    fun metrics(size: BSwitchSize): BSwitchMetrics {
        return when (size) {
            BSwitchSize.Sm -> BSwitchMetrics(
                width = ComponentTokens.Switch.SmWidth,
                height = ComponentTokens.Switch.SmHeight,
                thumbDiameter = ComponentTokens.Switch.SmThumbDiameter,
                thumbPadding = ComponentTokens.Switch.SmThumbPadding,
                trackShape = RoundedCornerShape(ComponentTokens.Switch.SmTrackCorner),
                focusRingRadius = ComponentTokens.Switch.SmFocusRingRadius,
                thumbElevation = ComponentTokens.Switch.SmThumbElevation
            )

            BSwitchSize.Md -> BSwitchMetrics(
                width = ComponentTokens.Switch.MdWidth,
                height = ComponentTokens.Switch.MdHeight,
                thumbDiameter = ComponentTokens.Switch.MdThumbDiameter,
                thumbPadding = ComponentTokens.Switch.DefaultThumbPadding,
                trackShape = RoundedCornerShape(ComponentTokens.Switch.MdTrackCorner),
                focusRingRadius = ComponentTokens.Switch.MdFocusRingRadius,
                thumbElevation = ComponentTokens.Switch.MdThumbElevation
            )

            BSwitchSize.Lg -> BSwitchMetrics(
                width = ComponentTokens.Switch.LgWidth,
                height = ComponentTokens.Switch.LgHeight,
                thumbDiameter = ComponentTokens.Switch.LgThumbDiameter,
                thumbPadding = ComponentTokens.Switch.DefaultThumbPadding,
                trackShape = RoundedCornerShape(ComponentTokens.Switch.LgTrackCorner),
                focusRingRadius = ComponentTokens.Switch.LgFocusRingRadius,
                thumbElevation = ComponentTokens.Switch.LgThumbElevation
            )
        }
    }
}

@Composable
fun BSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: BSwitchStyle = BSwitchStyle.Primary,
    size: BSwitchSize = BSwitchSize.Md,
    colors: BSwitchColors = BSwitchDefaults.colors(style),
    metrics: BSwitchMetrics = BSwitchDefaults.metrics(size),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val cs = BTokens.colorScheme

    // Interaction states
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val dragged by interactionSource.collectIsDraggedAsState()

    val overlayBase: Color? = when {
        dragged || pressed -> cs.overlayPressed
        hovered -> cs.overlayHover
        focused -> cs.overlayFocus
        else -> null
    }
    val overlayAlphaTarget = (overlayBase?.alpha ?: 0f)
    val overlayAlpha by animateFloatAsState(overlayAlphaTarget, label = "sw-overlay-alpha")

    // Track/thumb colors
    val targetTrack = when {
        !enabled && checked -> colors.disabledTrackOn
        !enabled && !checked -> colors.disabledTrackOff
        enabled && checked -> colors.trackOn
        else -> colors.trackOff
    }
    val targetThumb = when {
        !enabled && checked -> colors.disabledThumbOn
        !enabled && !checked -> colors.disabledThumbOff
        enabled && checked -> colors.thumbOn
        else -> colors.thumbOff
    }

    val trackColor by animateColorAsState(targetTrack, label = "sw-track")
    val thumbColor by animateColorAsState(targetThumb, label = "sw-thumb")

    // Border
    val borderColor = if (!checked) colors.borderOff else colors.borderOn
    val borderStroke = borderColor?.let { BorderStroke(ComponentTokens.Border.Thin, it) }

    // Thumb position
    val travel = metrics.width - (metrics.thumbPadding * 2) - metrics.thumbDiameter
    val targetX = if (checked) travel else 0.dp
    val thumbOffsetX by animateDpAsState(targetX, label = "sw-thumb-x")

    val indication = rememberRipple(bounded = false)

    Box(
        modifier = modifier
            .semantics {
                role = Role.Switch
                selected = checked
            }
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
                interactionSource = interactionSource,
                indication = indication
            )
            .padding(ComponentTokens.Switch.FocusPadding) // Reserve outer space for the focus ring.
            .wrapContentSize()
    ) {
        val focusStroke = ComponentTokens.Border.Regular
        if (focused) {
            Box(
                Modifier
                    .matchParentSize()
                    .drawBehind {
                        val stroke = focusStroke.toPx()
                        val w = metrics.width.toPx() + stroke * 2
                        val h = metrics.height.toPx() + stroke * 2
                        val canvasSize = this.size

                        drawRoundRect(
                            color = cs.onSurface.copy(alpha = ComponentTokens.Alpha.FocusRing),
                            topLeft = Offset(
                                (canvasSize.width - w) / 2f,
                                (canvasSize.height - h) / 2f
                            ),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(metrics.focusRingRadius.toPx())
                        )
                    }
            )
        }

        // Track
        Surface(
            modifier = Modifier
                .size(metrics.width, metrics.height),
            shape = metrics.trackShape,
            color = trackColor,
            border = borderStroke,
            contentColor = Color.Unspecified
        ) {
            val overlay = overlayBase ?: Color.Transparent
            Box(
                Modifier
                    .fillMaxSize()
                    .background(overlay.copy(alpha = overlayAlpha))
            )
        }

        // Thumb
        Box(
            Modifier
                .padding(metrics.thumbPadding)
                .offset(thumbOffsetX, 0.dp)
                .size(metrics.thumbDiameter)
                .shadow(metrics.thumbElevation, CircleShape, clip = false)
                .background(thumbColor, CircleShape)
        )
    }
}
