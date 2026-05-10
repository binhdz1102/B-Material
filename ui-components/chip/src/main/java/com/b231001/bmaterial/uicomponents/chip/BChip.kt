package com.b231001.bmaterial.uicomponents.chip

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens

@Stable
sealed interface BChipStyle {
    data object Filled : BChipStyle // primary / onPrimary
    data object Tonal : BChipStyle // secondaryContainer / onSecondaryContainer
    data object Outlined : BChipStyle // transparent + outline
    data object Elevated : BChipStyle // surface1 + subtle shadow
    data object Destructive : BChipStyle // error / onError
    data object Success : BChipStyle // success / onSuccess
    data object Warning : BChipStyle // warning / onWarning
    data object Info : BChipStyle // info / onInfo
}

@Stable
enum class BChipSize { Sm, Md, Lg }

@Stable
data class BChipColors(
    val container: Color,
    val onContainer: Color,
    val selectedContainer: Color,
    val selectedOnContainer: Color,
    val disabledContainer: Color,
    val disabledOnContainer: Color,
    val border: Color? = null
)

@Stable
data class BChipMetrics(
    val height: Dp,
    val horizontalPadding: Dp,
    val shape: Shape,
    val iconSize: Dp,
    val avatarSize: Dp,
    val gap: Dp,
    val textStyle: TextStyle,
    val borderWidth: Dp,
    val elevationDefault: Dp,
    val elevationFocused: Dp,
    val elevationPressed: Dp
)

object BChipDefaults {

    @Composable
    fun colors(style: BChipStyle): BChipColors {
        val cs = BTokens.colorScheme

        fun disabledContainer() =
            cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContainer)

        fun disabledContent() =
            cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContent)

        fun map(
            container: Color,
            onContainer: Color,
            selectedContainer: Color = onContainer,
            selectedOnContainer: Color = container,
            border: Color? = null
        ) = BChipColors(
            container = container,
            onContainer = onContainer,
            selectedContainer = selectedContainer,
            selectedOnContainer = selectedOnContainer,
            disabledContainer = disabledContainer(),
            disabledOnContainer = disabledContent(),
            border = border
        )

        return when (style) {
            BChipStyle.Filled -> map(cs.primary, cs.onPrimary)
            BChipStyle.Tonal -> map(cs.secondaryContainer, cs.onSecondaryContainer)
            BChipStyle.Elevated -> map(cs.surface1, cs.primary)
            BChipStyle.Destructive -> map(cs.error, cs.onError)
            BChipStyle.Success -> map(cs.success, cs.onSuccess)
            BChipStyle.Warning -> map(cs.warning, cs.onWarning)
            BChipStyle.Info -> map(cs.info, cs.onInfo)
            BChipStyle.Outlined -> map(
                container = Color.Transparent,
                onContainer = cs.primary,
                selectedContainer = cs.primary,
                selectedOnContainer = cs.onPrimary,
                border = cs.outline
            )
        }
    }

    @Composable
    fun metrics(size: BChipSize): BChipMetrics {
        val sh = BTokens.shapes
        val ty = BTokens.typography

        return when (size) {
            BChipSize.Sm -> BChipMetrics(
                height = ComponentTokens.Chip.SmHeight,
                horizontalPadding = ComponentTokens.Chip.SmHorizontalPadding,
                shape = sh.large,
                iconSize = ComponentTokens.Chip.SmIconSize,
                avatarSize = ComponentTokens.Chip.SmAvatarSize,
                gap = ComponentTokens.Chip.SmGap,
                textStyle = ty.labelMedium,
                borderWidth = ComponentTokens.Border.Thin,
                elevationDefault = 0.dp,
                elevationFocused = 0.dp,
                elevationPressed = 0.dp
            )

            BChipSize.Md -> BChipMetrics(
                height = ComponentTokens.Chip.MdHeight,
                horizontalPadding = ComponentTokens.Chip.MdHorizontalPadding,
                shape = sh.large,
                iconSize = ComponentTokens.Chip.MdIconSize,
                avatarSize = ComponentTokens.Chip.MdAvatarSize,
                gap = ComponentTokens.Chip.MdGap,
                textStyle = ty.labelLarge,
                borderWidth = ComponentTokens.Border.Thin,
                elevationDefault = 0.dp,
                elevationFocused = 0.dp,
                elevationPressed = 0.dp
            )

            BChipSize.Lg -> BChipMetrics(
                height = ComponentTokens.Chip.LgHeight,
                horizontalPadding = ComponentTokens.Chip.LgHorizontalPadding,
                shape = sh.extraLarge,
                iconSize = ComponentTokens.Chip.LgIconSize,
                avatarSize = ComponentTokens.Chip.LgAvatarSize,
                gap = ComponentTokens.Chip.LgGap,
                textStyle = ty.titleSmall,
                borderWidth = ComponentTokens.Border.Thin,
                elevationDefault = ComponentTokens.Chip.LgElevation,
                elevationFocused = ComponentTokens.Chip.LgElevation,
                elevationPressed = 0.dp
            )
        }
    }
}

@Composable
fun BChip(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: BChipStyle = BChipStyle.Outlined,
    size: BChipSize = BChipSize.Md,
    colors: BChipColors = BChipDefaults.colors(style),
    metrics: BChipMetrics = BChipDefaults.metrics(size),
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    onTrailingClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val cs = BTokens.colorScheme

    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val dragged by interactionSource.collectIsDraggedAsState()

    val overlayColor = when {
        dragged || pressed -> cs.overlayPressed
        hovered -> cs.overlayHover
        focused -> cs.overlayFocus
        else -> Color.Transparent
    }
    val overlayAlpha by animateFloatAsState(overlayColor.alpha, label = "chip-overlay")

    val baseContainer = if (checked) colors.selectedContainer else colors.container
    val baseContent = if (checked) colors.selectedOnContainer else colors.onContainer

    val containerColorTarget = when {
        !enabled -> colors.disabledContainer
        else -> baseContainer
    }
    val contentColorTarget = when {
        !enabled -> colors.disabledOnContainer
        else -> baseContent
    }

    val containerColor by animateColorAsState(containerColorTarget, label = "chip-container")
    val contentColor by animateColorAsState(contentColorTarget, label = "chip-content")

    val borderStroke = when {
        !enabled && style is BChipStyle.Outlined -> BorderStroke(
            metrics.borderWidth,
            colors.disabledOnContainer.copy(alpha = ComponentTokens.Alpha.DisabledOutline)
        )

        style is BChipStyle.Outlined -> colors.border?.let { BorderStroke(metrics.borderWidth, it) }
        else -> null
    }

    val currentElevationTarget =
        if (style == BChipStyle.Elevated && enabled) {
            when {
                pressed -> metrics.elevationPressed
                focused -> metrics.elevationFocused
                else -> metrics.elevationDefault
            }
        } else {
            0.dp
        }
    val animatedElevation by animateDpAsState(currentElevationTarget, label = "chip-elevation")

    val indication = rememberRipple(bounded = true)
    val toggle = Modifier.toggleable(
        value = checked,
        onValueChange = onCheckedChange,
        enabled = enabled,
        role = Role.Checkbox,
        interactionSource = interactionSource,
        indication = indication
    )

    Surface(
        modifier = modifier
            .semantics { selected = checked }
            .clip(metrics.shape)
            .then(toggle)
            .heightIn(min = metrics.height)
            .drawBehind {
                if (focused) {
                    drawRoundRect(
                        color = contentColor.copy(alpha = ComponentTokens.Alpha.ChipFocusRing),
                        style = Stroke(width = ComponentTokens.Border.Regular.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(100f, 100f)
                    )
                }
            },
        shape = metrics.shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = animatedElevation,
        shadowElevation = animatedElevation,
        border = borderStroke
    ) {
        Box(
            Modifier
                .background(overlayColor.copy(alpha = overlayAlpha))
                .padding(horizontal = metrics.horizontalPadding)
                .heightIn(min = metrics.height),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Box(
                        Modifier
                            .size(metrics.iconSize)
                            .padding(
                                end = metrics.gap - ComponentTokens.Chip.LeadingGapAdjustment
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides contentColor
                        ) {
                            leadingIcon()
                        }
                    }
                }

                ProvideTextStyle(metrics.textStyle) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        content()
                    }
                }

                if (trailingIcon != null) {
                    Spacer(Modifier.width(metrics.gap))
                    val trailingModifier = if (onTrailingClick != null && enabled) {
                        Modifier
                            .size(metrics.iconSize)
                            .clickable(
                                role = Role.Button,
                                onClick = onTrailingClick,
                                indication = rememberRipple(bounded = false),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    } else {
                        Modifier.size(metrics.iconSize)
                    }

                    Box(trailingModifier, contentAlignment = Alignment.Center) {
                        CompositionLocalProvider(LocalContentColor provides contentColor) {
                            trailingIcon()
                        }
                    }
                }
            }
        }
    }
}
