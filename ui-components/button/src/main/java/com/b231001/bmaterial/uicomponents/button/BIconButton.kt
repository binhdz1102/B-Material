package com.b231001.bmaterial.uicomponents.button

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import androidx.compose.ui.geometry.Size as GSize

@Stable
sealed interface BIconButtonStyle {
    data object Filled : BIconButtonStyle // primary/onPrimary
    data object Tonal : BIconButtonStyle // secondaryContainer/onSecondaryContainer
    data object Outlined : BIconButtonStyle // transparent, outline border, primary icon
    data object Text : BIconButtonStyle // transparent, primary icon
    data object Elevated : BIconButtonStyle // surface1 container, primary icon
    data object Destructive : BIconButtonStyle // error/onError
    data object Success : BIconButtonStyle // success/onSuccess
    data object Warning : BIconButtonStyle // warning/onWarning
    data object Info : BIconButtonStyle // info/onInfo
}

@Stable
enum class BIconButtonSize { Sm, Md, Lg }

@Stable
data class BIconButtonColors(
    val containerOn: Color,
    val contentOn: Color,
    val containerOff: Color,
    val contentOff: Color,
    val disabledContainerOn: Color,
    val disabledContentOn: Color,
    val disabledContainerOff: Color,
    val disabledContentOff: Color,
    val borderOn: Color? = null,
    val borderOff: Color? = null
)

@Stable
data class BIconButtonMetrics(
    val size: Dp,
    val iconSize: Dp,
    val shapeCorner: Dp,
    val focusRingRadius: Dp,
    val elevation: Dp
)

object BIconButtonDefaults {

    @Composable
    fun colors(style: BIconButtonStyle): BIconButtonColors {
        val cs = BTokens.colorScheme

        fun disabledContainer() = cs.onSurface.copy(alpha = 0.12f)
        fun disabledContent() = cs.onSurface.copy(alpha = 0.38f)

        val onCont: Color
        val onIcon: Color
        val offCont: Color
        val offIcon: Color
        val borderOn: Color?
        val borderOff: Color?

        when (style) {
            BIconButtonStyle.Filled -> {
                onCont = cs.primary
                onIcon = cs.onPrimary
                offCont = cs.surfaceVariant
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }

            BIconButtonStyle.Tonal -> {
                onCont = cs.secondaryContainer
                onIcon = cs.onSecondaryContainer
                offCont = cs.surfaceVariant
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }

            BIconButtonStyle.Elevated -> {
                onCont = cs.surface1
                onIcon = cs.primary
                offCont = cs.surface1
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }

            BIconButtonStyle.Outlined -> {
                onCont = Color.Transparent
                onIcon = cs.primary
                offCont = Color.Transparent
                offIcon = cs.onSurfaceVariant
                borderOn = cs.outline
                borderOff = cs.outlineVariant
            }

            BIconButtonStyle.Text -> {
                onCont = Color.Transparent
                onIcon = cs.primary
                offCont = Color.Transparent
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }

            BIconButtonStyle.Destructive -> {
                onCont = cs.error
                onIcon = cs.onError
                offCont = cs.surfaceVariant
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }

            BIconButtonStyle.Success -> {
                onCont = cs.success
                onIcon = cs.onSuccess
                offCont = cs.surfaceVariant
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }

            BIconButtonStyle.Warning -> {
                onCont = cs.warning
                onIcon = cs.onWarning
                offCont = cs.surfaceVariant
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }

            BIconButtonStyle.Info -> {
                onCont = cs.info
                onIcon = cs.onInfo
                offCont = cs.surfaceVariant
                offIcon = cs.onSurfaceVariant
                borderOn = null
                borderOff = null
            }
        }

        return BIconButtonColors(
            containerOn = onCont,
            contentOn = onIcon,
            containerOff = offCont,
            contentOff = offIcon,
            disabledContainerOn = disabledContainer(),
            disabledContentOn = disabledContent(),
            disabledContainerOff = disabledContainer(),
            disabledContentOff = disabledContent(),
            borderOn = borderOn,
            borderOff = borderOff
        )
    }

    @Composable
    fun metrics(size: BIconButtonSize): BIconButtonMetrics = when (size) {
        BIconButtonSize.Sm -> BIconButtonMetrics(
            size = 40.dp,
            iconSize = 20.dp,
            shapeCorner = 20.dp,
            focusRingRadius = 14.dp,
            elevation = 0.dp
        )

        BIconButtonSize.Md -> BIconButtonMetrics(
            size = 48.dp,
            iconSize = 24.dp,
            shapeCorner = 24.dp,
            focusRingRadius = 18.dp,
            elevation = 0.dp
        )

        BIconButtonSize.Lg -> BIconButtonMetrics(
            size = 56.dp,
            iconSize = 28.dp,
            shapeCorner = 28.dp,
            focusRingRadius = 20.dp,
            elevation = 1.dp
        )
    }
}

@Composable
fun BIconButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: BIconButtonStyle = BIconButtonStyle.Filled,
    size: BIconButtonSize = BIconButtonSize.Md,
    colors: BIconButtonColors = BIconButtonDefaults.colors(style),
    metrics: BIconButtonMetrics = BIconButtonDefaults.metrics(size),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
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
    val overlayAlpha by animateFloatAsState(
        targetValue = overlayBase?.alpha ?: 0f,
        label = "ib-overlay"
    )

    val targetContainer = when {
        !enabled && checked -> colors.disabledContainerOn
        !enabled && !checked -> colors.disabledContainerOff
        enabled && checked -> colors.containerOn
        else -> colors.containerOff
    }
    val targetContent = when {
        !enabled && checked -> colors.disabledContentOn
        !enabled && !checked -> colors.disabledContentOff
        enabled && checked -> colors.contentOn
        else -> colors.contentOff
    }
    val container by animateColorAsState(targetContainer, label = "ib-container")
    val contentColor by animateColorAsState(targetContent, label = "ib-content")

    val borderStroke: BorderStroke? = when {
        checked -> colors.borderOn?.let { BorderStroke(1.dp, it) }
        else -> colors.borderOff?.let { BorderStroke(1.dp, it) }
    }

    val baseElevation = if (style == BIconButtonStyle.Elevated) metrics.elevation else 0.dp
    val elev by animateDpAsState(
        targetValue = when {
            pressed -> baseElevation
            else -> baseElevation
        },
        label = "ib-elev"
    )

    val indication = rememberRipple(bounded = true)

    Surface(
        modifier = modifier
            .size(metrics.size)
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
            .clip(if (metrics.shapeCorner <= 0.dp) CircleShape else CircleShape),
        shape = CircleShape,
        color = container,
        contentColor = contentColor,
        shadowElevation = elev,
        tonalElevation = elev,
        border = borderStroke
    ) {
        // Focus
        if (focused) {
            Box(
                Modifier.drawBehind {
                    val stroke = 2.dp.toPx()
                    val canvasSize = this.size

                    val w = canvasSize.width + stroke * 2
                    val h = canvasSize.height + stroke * 2
                    drawRoundRect(
                        color = contentColor.copy(alpha = 0.32f),
                        topLeft = Offset(-stroke, -stroke),
                        size = GSize(w, h),
                        cornerRadius = CornerRadius(metrics.focusRingRadius.toPx())
                    )
                }
            )
        }

        // Overlay for states
        Box(
            Modifier
                .background((overlayBase ?: Color.Transparent).copy(alpha = overlayAlpha))
                .size(metrics.size),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Box(Modifier.size(metrics.iconSize)) {
                    content()
                }
            }
        }
    }
}
