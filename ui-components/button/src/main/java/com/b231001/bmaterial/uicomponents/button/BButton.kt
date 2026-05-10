package com.b231001.bmaterial.uicomponents.button

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens

@Stable
sealed interface BButtonStyle {
    data object Filled : BButtonStyle // primary/onPrimary
    data object Tonal : BButtonStyle // secondaryContainer/onSecondaryContainer
    data object Outlined : BButtonStyle // transparent, outline border, primary content
    data object Text : BButtonStyle // transparent, primary content
    data object Elevated : BButtonStyle // surface1 container
    data object Destructive : BButtonStyle // error/onError
    data object Success : BButtonStyle // success/onSuccess
    data object Warning : BButtonStyle // warning/onWarning
    data object Info : BButtonStyle // info/onInfo
}

@Stable
enum class BButtonSize { Xs, Sm, Md, Lg, Xl }

@Stable
data class BButtonColors(
    val container: Color,
    val onContainer: Color,
    val disabledContainer: Color,
    val disabledOnContainer: Color,
    val border: Color? = null
)

@Stable
class BButtonMetrics(
    val height: Dp,
    val horizontalPadding: Dp,
    val shape: Shape,
    val iconSize: Dp,
    val gap: Dp,
    val textStyle: TextStyle
)

@Stable
class BButtonElevation(
    val default: Dp,
    val hovered: Dp,
    val focused: Dp,
    val pressed: Dp,
    val selected: Dp
)

object BButtonDefaults {
    /** Map style to colors from ColorScheme. */
    @Composable
    fun colors(
        style: BButtonStyle,
        enabled: Boolean = true,
        selected: Boolean = true
    ): BButtonColors {
        val cs = BTokens.colorScheme

        fun disabledContainer(): Color =
            cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContainer)

        fun disabledContent(): Color =
            cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContent)

        if (!enabled) {
            return when (style) {
                BButtonStyle.Text -> BButtonColors(
                    container = Color.Transparent,
                    onContainer = disabledContent(),
                    disabledContainer = Color.Transparent,
                    disabledOnContainer = disabledContent(),
                    border = null
                )

                BButtonStyle.Outlined -> BButtonColors(
                    container = Color.Transparent,
                    onContainer = disabledContent(),
                    disabledContainer = Color.Transparent,
                    disabledOnContainer = disabledContent(),
                    border = cs.outlineVariant
                )

                BButtonStyle.Filled,
                BButtonStyle.Tonal,
                BButtonStyle.Elevated,
                BButtonStyle.Destructive,
                BButtonStyle.Success,
                BButtonStyle.Warning,
                BButtonStyle.Info -> BButtonColors(
                    container = disabledContainer(),
                    onContainer = disabledContent(),
                    disabledContainer = disabledContainer(),
                    disabledOnContainer = disabledContent(),
                    border = null
                )
            }
        }

        val base = when (style) {
            BButtonStyle.Filled -> BButtonColors(
                container = cs.primary,
                onContainer = cs.onPrimary,
                disabledContainer = disabledContainer(),
                disabledOnContainer = disabledContent()
            )

            BButtonStyle.Tonal -> BButtonColors(
                container = cs.secondaryContainer,
                onContainer = cs.onSecondaryContainer,
                disabledContainer = disabledContainer(),
                disabledOnContainer = disabledContent()
            )

            BButtonStyle.Elevated -> BButtonColors(
                container = cs.surface1,
                onContainer = cs.primary,
                disabledContainer = disabledContainer(),
                disabledOnContainer = disabledContent()
            )

            BButtonStyle.Destructive -> BButtonColors(
                container = cs.error,
                onContainer = cs.onError,
                disabledContainer = disabledContainer(),
                disabledOnContainer = disabledContent()
            )

            BButtonStyle.Success -> BButtonColors(
                container = cs.success,
                onContainer = cs.onSuccess,
                disabledContainer = disabledContainer(),
                disabledOnContainer = disabledContent()
            )

            BButtonStyle.Warning -> BButtonColors(
                container = cs.warning,
                onContainer = cs.onWarning,
                disabledContainer = disabledContainer(),
                disabledOnContainer = disabledContent()
            )

            BButtonStyle.Info -> BButtonColors(
                container = cs.info,
                onContainer = cs.onInfo,
                disabledContainer = disabledContainer(),
                disabledOnContainer = disabledContent()
            )

            BButtonStyle.Outlined -> BButtonColors(
                container = Color.Transparent,
                onContainer = cs.primary,
                disabledContainer = Color.Transparent,
                disabledOnContainer = disabledContent(),
                border = cs.outline
            )

            BButtonStyle.Text -> BButtonColors(
                container = Color.Transparent,
                onContainer = cs.primary,
                disabledContainer = Color.Transparent,
                disabledOnContainer = disabledContent(),
                border = null
            )
        }

        if (selected) {
            fun swap(c: Color, on: Color) = base.copy(container = on, onContainer = c)
            return when (style) {
                BButtonStyle.Filled -> swap(cs.primary, cs.onPrimary)
                BButtonStyle.Tonal -> swap(cs.secondaryContainer, cs.onSecondaryContainer)
                BButtonStyle.Elevated -> swap(cs.surface1, cs.onSurface)
                BButtonStyle.Destructive -> swap(cs.error, cs.onError)
                BButtonStyle.Success -> swap(cs.success, cs.onSuccess)
                BButtonStyle.Warning -> swap(cs.warning, cs.onWarning)
                BButtonStyle.Info -> swap(cs.info, cs.onInfo)
                BButtonStyle.Outlined,
                BButtonStyle.Text -> base.copy(
                    container = cs.primary,
                    onContainer = cs.onPrimary,
                    border = null
                )
            }
        }

        return base
    }

    /** Size metrics derived from BSizes, Shapes, and Typography. */
    @Composable
    fun metrics(size: BButtonSize): BButtonMetrics {
        val sz = BTokens.sizes
        val sh = BTokens.shapes
        val ty = BTokens.typography
        return when (size) {
            BButtonSize.Xs -> BButtonMetrics(
                height = ComponentTokens.Button.XsHeight,
                horizontalPadding = ComponentTokens.Button.XsHorizontalPadding,
                shape = sh.small,
                iconSize = sz.iconSmall,
                gap = ComponentTokens.Button.DefaultGap,
                textStyle = ty.labelMedium
            )

            BButtonSize.Sm -> BButtonMetrics(
                height = ComponentTokens.Button.SmHeight,
                horizontalPadding = ComponentTokens.Button.SmHorizontalPadding,
                shape = sh.small,
                iconSize = sz.iconMedium,
                gap = ComponentTokens.Button.DefaultGap,
                textStyle = ty.labelLarge
            )

            BButtonSize.Md -> BButtonMetrics(
                height = ComponentTokens.Button.MdHeight,
                horizontalPadding = ComponentTokens.Button.MdHorizontalPadding,
                shape = sh.medium,
                iconSize = sz.iconMedium,
                gap = ComponentTokens.Button.DefaultGap,
                textStyle = ty.labelLarge
            )

            BButtonSize.Lg -> BButtonMetrics(
                height = ComponentTokens.Button.LgHeight,
                horizontalPadding = ComponentTokens.Button.LgHorizontalPadding,
                shape = sh.large,
                iconSize = sz.iconLarge,
                gap = ComponentTokens.Button.LgGap,
                textStyle = ty.titleSmall
            )

            BButtonSize.Xl -> BButtonMetrics(
                height = ComponentTokens.Button.XlHeight,
                horizontalPadding = ComponentTokens.Button.XlHorizontalPadding,
                shape = sh.extraLarge,
                iconSize = sz.iconLarge,
                gap = ComponentTokens.Button.XlGap,
                textStyle = ty.titleSmall
            )
        }
    }

    /** Elevation curve; use small, subtle steps. */
    @Composable
    fun elevation(style: BButtonStyle): BButtonElevation = when (style) {
        BButtonStyle.Elevated -> BButtonElevation(
            default = ComponentTokens.Button.ElevatedDefault,
            hovered = ComponentTokens.Button.ElevatedHovered,
            focused = ComponentTokens.Button.ElevatedHovered,
            pressed = ComponentTokens.Button.ElevatedDefault,
            selected = ComponentTokens.Button.ElevatedDefault
        )

        else -> BButtonElevation(
            default = 0.dp,
            hovered = 0.dp,
            focused = 0.dp,
            pressed = 0.dp,
            selected = 0.dp
        )
    }
}

@Composable
fun BButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    enabled: Boolean = true,
    style: BButtonStyle = BButtonStyle.Filled,
    size: BButtonSize = BButtonSize.Md,
    colors: BButtonColors = BButtonDefaults.colors(style, enabled, selected),
    metrics: BButtonMetrics = BButtonDefaults.metrics(size),
    elevations: BButtonElevation = BButtonDefaults.elevation(style),
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val cs = BTokens.colorScheme

    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val dragged by interactionSource.collectIsDraggedAsState()

    val stateOverlayColor: Color? = when {
        dragged -> cs.overlayPressed
        pressed -> cs.overlayPressed
        hovered -> cs.overlayHover
        focused -> cs.overlayFocus
        else -> null
    }
    val overlayAlphaTarget = stateOverlayColor?.alpha ?: 0f
    val overlayAlpha by animateFloatAsState(overlayAlphaTarget, label = "overlayAlpha")

    val containerColor = if (enabled) colors.container else colors.disabledContainer
    val contentColor = if (enabled) colors.onContainer else colors.disabledOnContainer

    val currentElevationTarget: Dp = when {
        pressed -> elevations.pressed
        hovered -> elevations.hovered
        focused -> elevations.focused
        selected -> elevations.selected
        else -> elevations.default
    }
    val animatedElevation by
    animateDpAsState(currentElevationTarget, label = "elevation")

    val borderStroke: BorderStroke? = when (style) {
        BButtonStyle.Outlined -> colors.border?.let {
            BorderStroke(ComponentTokens.Border.Thin, it)
        }
        else -> null
    }

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = metrics.height)
            .semantics {
                // Expose button role and selected state to accessibility services.
                role = Role.Button
                this.selected = selected
            }
            .clip(metrics.shape)
            .drawBehind {
                if (focused) {
                    val stroke = ComponentTokens.Border.Regular.toPx()
                    val canvasSize = this.size
                    val w = canvasSize.width + stroke * 2
                    val h = canvasSize.height + stroke * 2

                    drawRoundRect(
                        color = contentColor.copy(alpha = ComponentTokens.Alpha.FocusRing),
                        topLeft = Offset(-stroke, -stroke),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(
                            ComponentTokens.Button.FocusRingCorner.toPx()
                        )
                    )
                }
            },
        onClick = onClick,
        enabled = enabled && !loading,
        shape = metrics.shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = animatedElevation,
        shadowElevation = animatedElevation,
        border = borderStroke,
        interactionSource = interactionSource
    ) {
        val overlayBase = stateOverlayColor ?: Color.Transparent
        Box(Modifier.background(overlayBase.copy(alpha = overlayAlpha))) {
            Row(
                Modifier
                    .heightIn(min = metrics.height)
                    .padding(horizontal = metrics.horizontalPadding)
                    .then(
                        if (size == BButtonSize.Md ||
                            size == BButtonSize.Lg ||
                            size == BButtonSize.Xl
                        ) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val gap = metrics.gap
                val iconBox: @Composable (@Composable () -> Unit) -> Unit = { icon ->
                    Box(
                        Modifier.size(metrics.iconSize),
                        contentAlignment = Alignment.Center
                    ) { icon() }
                }

                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(metrics.iconSize),
                        strokeWidth = ComponentTokens.Border.Regular,
                        color = contentColor
                    )
                    Spacer(Modifier.width(gap))
                } else if (leadingIcon != null) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        iconBox(
                            leadingIcon
                        )
                    }
                    Spacer(Modifier.width(gap))
                }

                ProvideTextStyle(metrics.textStyle) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        content()
                    }
                }

                if (!loading && trailingIcon != null) {
                    Spacer(Modifier.width(gap))
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        iconBox(
                            trailingIcon
                        )
                    }
                }
            }
        }
    }
}
