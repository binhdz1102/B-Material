package com.b231001.bmaterial.uicomponents.card

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
sealed interface BCardStyle {
    data object Filled : BCardStyle
    data object Elevated : BCardStyle
    data object Outlined : BCardStyle

    /** Semantic styles */
    data object Success : BCardStyle
    data object Warning : BCardStyle
    data object Info : BCardStyle
    data object Destructive : BCardStyle
}

@Stable
enum class BCardSize { Sm, Md, Lg }

@Stable
data class BCardColors(
    val container: Color,
    val onContainer: Color,
    val outline: Color?,
    val disabledContainer: Color,
    val disabledOnContainer: Color
)

@Stable
data class BCardMetrics(
    val shape: Shape,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
    val mediaCornerRadius: Dp,
    val headerTextStyle: TextStyle,
    val bodyTextStyle: TextStyle,
    val actionGap: Dp
)

@Stable
data class BCardElevation(
    val default: Dp,
    val hovered: Dp,
    val focused: Dp,
    val pressed: Dp,
    val selected: Dp
)

object BCardDefaults {

    @Composable
    fun colors(
        style: BCardStyle,
        enabled: Boolean = true
    ): BCardColors {
        val cs = BTokens.colorScheme
        val disabledContainer =
            cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContainerSubtle)
        val disabledContent =
            cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContent)

        val base = when (style) {
            BCardStyle.Filled -> BCardColors(
                container = cs.surface1,
                onContainer = cs.onSurface,
                outline = null,
                disabledContainer = disabledContainer,
                disabledOnContainer = disabledContent
            )
            BCardStyle.Elevated -> BCardColors(
                container = cs.surface2,
                onContainer = cs.onSurface,
                outline = null,
                disabledContainer = disabledContainer,
                disabledOnContainer = disabledContent
            )
            BCardStyle.Outlined -> BCardColors(
                container = Color.Transparent,
                onContainer = cs.onSurface,
                outline = cs.outline,
                disabledContainer = Color.Transparent,
                disabledOnContainer = disabledContent
            )
            BCardStyle.Success -> BCardColors(
                container = cs.success,
                onContainer = cs.onSuccess,
                outline = null,
                disabledContainer = disabledContainer,
                disabledOnContainer = disabledContent
            )
            BCardStyle.Warning -> BCardColors(
                container = cs.warning,
                onContainer = cs.onWarning,
                outline = null,
                disabledContainer = disabledContainer,
                disabledOnContainer = disabledContent
            )
            BCardStyle.Info -> BCardColors(
                container = cs.info,
                onContainer = cs.onInfo,
                outline = null,
                disabledContainer = disabledContainer,
                disabledOnContainer = disabledContent
            )
            BCardStyle.Destructive -> BCardColors(
                container = cs.error,
                onContainer = cs.onError,
                outline = null,
                disabledContainer = disabledContainer,
                disabledOnContainer = disabledContent
            )
        }

        return if (enabled) {
            base.copy(
                container = base.container,
                onContainer = base.onContainer
            )
        } else {
            BCardColors(
                container = base.disabledContainer,
                onContainer = base.disabledOnContainer,
                outline = base.outline,
                disabledContainer = base.disabledContainer,
                disabledOnContainer = base.disabledOnContainer
            )
        }
    }

    @Composable
    fun metrics(size: BCardSize): BCardMetrics {
        val sh = BTokens.shapes
        val pad = BTokens.paddings
        val ty = BTokens.typography

        return when (size) {
            BCardSize.Sm -> BCardMetrics(
                shape = sh.small,
                verticalPadding = pad.small,
                horizontalPadding = pad.medium,
                mediaCornerRadius = ComponentTokens.Card.SmallMediaCorner,
                headerTextStyle = ty.titleSmall,
                bodyTextStyle = ty.bodySmall,
                actionGap = ComponentTokens.Card.SmallActionGap
            )
            BCardSize.Md -> BCardMetrics(
                shape = sh.medium,
                verticalPadding = pad.medium,
                horizontalPadding = pad.large,
                mediaCornerRadius = ComponentTokens.Card.MediumMediaCorner,
                headerTextStyle = ty.titleMedium,
                bodyTextStyle = ty.bodyMedium,
                actionGap = ComponentTokens.Card.MediumActionGap
            )
            BCardSize.Lg -> BCardMetrics(
                shape = sh.large,
                verticalPadding = pad.large,
                horizontalPadding = pad.extraLarge,
                mediaCornerRadius = ComponentTokens.Card.LargeMediaCorner,
                headerTextStyle = ty.titleLarge,
                bodyTextStyle = ty.bodyLarge,
                actionGap = ComponentTokens.Card.LargeActionGap
            )
        }
    }

    @Composable
    fun elevation(style: BCardStyle): BCardElevation = when (style) {
        BCardStyle.Elevated -> BCardElevation(
            default = ComponentTokens.Card.ElevatedDefault,
            hovered = ComponentTokens.Card.ElevatedHovered,
            focused = ComponentTokens.Card.ElevatedHovered,
            pressed = ComponentTokens.Card.ElevatedDefault,
            selected = ComponentTokens.Card.ElevatedDefault
        )
        BCardStyle.Outlined -> BCardElevation(
            default = 0.dp,
            hovered = ComponentTokens.Card.ElevatedDefault,
            focused = ComponentTokens.Card.ElevatedDefault,
            pressed = 0.dp,
            selected = 0.dp
        )
        else -> BCardElevation(
            default = 0.dp,
            hovered = ComponentTokens.Card.ElevatedDefault,
            focused = ComponentTokens.Card.ElevatedDefault,
            pressed = 0.dp,
            selected = 0.dp
        )
    }
}

@Composable
fun BCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    style: BCardStyle = BCardStyle.Filled,
    size: BCardSize = BCardSize.Md,
    colors: BCardColors = BCardDefaults.colors(style, enabled),
    metrics: BCardMetrics = BCardDefaults.metrics(size),
    elevations: BCardElevation = BCardDefaults.elevation(style),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    // Slots
    header: (@Composable RowScope.() -> Unit)? = null,
    media: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val cs = BTokens.colorScheme

    // Interaction states
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val dragged by interactionSource.collectIsDraggedAsState()

    // State layer color.
    val overlayBase: Color? = when {
        dragged || pressed -> cs.overlayPressed
        hovered -> cs.overlayHover
        focused -> cs.overlayFocus
        else -> null
    }
    val overlayAlphaTarget = overlayBase?.alpha ?: 0f
    val overlayAlpha by animateFloatAsState(overlayAlphaTarget, label = "card-overlay")

    // Elevation
    val targetElevation = when {
        pressed -> elevations.pressed
        hovered -> elevations.hovered
        focused -> elevations.focused
        selected -> elevations.selected
        else -> elevations.default
    }
    val animatedElevation by animateDpAsState(targetElevation, label = "card-elevation")

    val containerColor by animateColorAsState(
        if (enabled) colors.container else colors.disabledContainer,
        label = "card-container"
    )
    val contentColor by animateColorAsState(
        if (enabled) colors.onContainer else colors.disabledOnContainer,
        label = "card-on"
    )

    val clickableModifier = if (onClick != null) {
        Modifier
            .semantics {
                role = Role.Button
                this.selected = selected
            }
            .then(
                Modifier.clickable(
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = true),
                    onClick = onClick
                )
            )
    } else {
        Modifier
    }

    val outlineStroke = colors.outline?.let { BorderStroke(ComponentTokens.Border.Thin, it) }

    Surface(
        modifier = modifier
            .clip(metrics.shape)
            .then(clickableModifier),
        shape = metrics.shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = animatedElevation,
        shadowElevation = animatedElevation,
        border = outlineStroke
    ) {
        val overlay = (overlayBase ?: Color.Transparent).copy(alpha = overlayAlpha)
        Column(
            Modifier
                .background(overlay)
                .padding(
                    vertical = metrics.verticalPadding,
                    horizontal = metrics.horizontalPadding
                )
        ) {
            if (header != null) {
                ProvideTextStyle(metrics.headerTextStyle) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = header
                    )
                }
                Spacer(Modifier.height(ComponentTokens.Card.SectionSpacing))
            }

            if (media != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(metrics.mediaCornerRadius))
                ) {
                    media()
                }
                Spacer(Modifier.height(ComponentTokens.Card.SectionSpacing))
            }

            ProvideTextStyle(metrics.bodyTextStyle) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    Column(content = content)
                }
            }

            if (actions != null) {
                Spacer(Modifier.height(ComponentTokens.Card.ActionsSpacing))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(metrics.actionGap),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
}
