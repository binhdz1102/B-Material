package com.b231001.bmaterial.uicomponents.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens

@Stable
sealed interface BCheckboxStyle {
    data object Primary : BCheckboxStyle
    data object Destructive : BCheckboxStyle
    data object Success : BCheckboxStyle
    data object Warning : BCheckboxStyle
    data object Info : BCheckboxStyle
}

@Stable
enum class BCheckboxSize { Sm, Md, Lg }

@Stable
data class BCheckboxColors(
    val boxOn: Color,
    val boxOff: Color,
    val checkmarkOn: Color,
    val checkmarkOff: Color,
    val borderOn: Color?,
    val borderOff: Color?,
    val disabledBoxOn: Color,
    val disabledBoxOff: Color,
    val disabledCheckmarkOn: Color,
    val disabledCheckmarkOff: Color
)

@Stable
data class BCheckboxMetrics(
    val side: Dp,
    val corner: Dp,
    val borderWidth: Dp,
    val focusRingStroke: Dp,
    val checkStroke: Dp
)

object BCheckboxDefaults {

    @Composable
    fun colors(style: BCheckboxStyle): BCheckboxColors {
        val cs = BTokens.colorScheme

        val onColor: Pair<Color, Color> = when (style) {
            BCheckboxStyle.Primary -> cs.primary to cs.onPrimary
            BCheckboxStyle.Destructive -> cs.error to cs.onError
            BCheckboxStyle.Success -> cs.success to cs.onSuccess
            BCheckboxStyle.Warning -> cs.warning to cs.onWarning
            BCheckboxStyle.Info -> cs.info to cs.onInfo
        }

        val (boxOn, onText) = onColor
        val boxOff = cs.surface
        val checkOn = onText
        val checkOff = cs.onSurface
        val borderOff = cs.outlineVariant
        val borderOn: Color? = null

        val disabledBox = cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContainer)
        val disabledCheck = cs.onSurface.copy(alpha = ComponentTokens.Alpha.DisabledContent)

        return BCheckboxColors(
            boxOn = boxOn,
            boxOff = boxOff,
            checkmarkOn = checkOn,
            checkmarkOff = checkOff,
            borderOn = borderOn,
            borderOff = borderOff,
            disabledBoxOn = disabledBox,
            disabledBoxOff = disabledBox,
            disabledCheckmarkOn = disabledCheck,
            disabledCheckmarkOff = disabledCheck
        )
    }

    @Composable
    fun metrics(size: BCheckboxSize): BCheckboxMetrics = when (size) {
        BCheckboxSize.Sm -> BCheckboxMetrics(
            side = ComponentTokens.Checkbox.SmSide,
            corner = ComponentTokens.Checkbox.SmCorner,
            borderWidth = ComponentTokens.Checkbox.SmBorderWidth,
            focusRingStroke = ComponentTokens.Border.Regular,
            checkStroke = ComponentTokens.Checkbox.SmCheckStroke
        )

        BCheckboxSize.Md -> BCheckboxMetrics(
            side = ComponentTokens.Checkbox.MdSide,
            corner = ComponentTokens.Checkbox.MdCorner,
            borderWidth = ComponentTokens.Checkbox.DefaultBorderWidth,
            focusRingStroke = ComponentTokens.Border.Regular,
            checkStroke = ComponentTokens.Checkbox.MdCheckStroke
        )

        BCheckboxSize.Lg -> BCheckboxMetrics(
            side = ComponentTokens.Checkbox.LgSide,
            corner = ComponentTokens.Checkbox.LgCorner,
            borderWidth = ComponentTokens.Checkbox.DefaultBorderWidth,
            focusRingStroke = ComponentTokens.Border.Regular,
            checkStroke = ComponentTokens.Checkbox.LgCheckStroke
        )
    }
}

@Composable
fun BCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: BCheckboxStyle = BCheckboxStyle.Primary,
    size: BCheckboxSize = BCheckboxSize.Md,
    colors: BCheckboxColors = BCheckboxDefaults.colors(style),
    metrics: BCheckboxMetrics = BCheckboxDefaults.metrics(size),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val cs = BTokens.colorScheme

    // Interactions -> overlay
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
    val overlayAlpha by animateFloatAsState((overlayBase?.alpha ?: 0f), label = "cbx-overlay")

    // Colors per state
    val boxTarget = when {
        !enabled && checked -> colors.disabledBoxOn
        !enabled && !checked -> colors.disabledBoxOff
        enabled && checked -> colors.boxOn
        else -> colors.boxOff
    }
    val checkTarget = when {
        !enabled && checked -> colors.disabledCheckmarkOn
        !enabled && !checked -> colors.disabledCheckmarkOff
        enabled && checked -> colors.checkmarkOn
        else -> colors.checkmarkOff
    }
    val borderColor = when {
        !checked -> colors.borderOff
        else -> colors.borderOn
    }

    val boxColor by animateColorAsState(boxTarget, label = "cbx-box")
    val markColor by animateColorAsState(checkTarget, label = "cbx-mark")

    // Check animation progress (0f=unchecked, 1f=checked)
    val progress by animateFloatAsState(if (checked) 1f else 0f, label = "cbx-progress")

    // Slight lift on hover/focus
    val shadow by animateDpAsState(
        if (hovered || focused) ComponentTokens.Checkbox.HoveredElevation else 0.dp,
        label = "cbx-shadow"
    )

    val indication = rememberRipple(bounded = true)

    Box(
        modifier = modifier
            .semantics {
                role = Role.Checkbox
                selected = checked
            }
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
                interactionSource = interactionSource,
                indication = indication
            )
            .padding(ComponentTokens.Checkbox.FocusPadding) // Reserve outer space for the focus ring.
            .wrapContentSize()
    ) {
        // Focus
        if (focused) {
            Box(
                Modifier
                    .matchParentSize()
                    .drawBehind {
                        val stroke = metrics.focusRingStroke.toPx()
                        val w = metrics.side.toPx() + stroke * 2
                        val h = metrics.side.toPx() + stroke * 2
                        val canvasSize = this.size

                        val topLeftX = (canvasSize.width - w) / 2f
                        val topLeftY = (canvasSize.height - h) / 2f
                        drawRoundRect(
                            color = cs.onSurface.copy(alpha = ComponentTokens.Alpha.FocusRing),
                            topLeft = Offset(topLeftX, topLeftY),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(metrics.corner.toPx())
                        )
                    }
            )
        }

        // Box + border + overlay + checkmark
        Box(
            modifier = Modifier
                .size(metrics.side)
                .shadow(shadow, RoundedCornerShape(metrics.corner), clip = false)
                .background(boxColor, RoundedCornerShape(metrics.corner))
                .border(
                    width = (if (borderColor != null && !checked) metrics.borderWidth else 0.dp),
                    brush = borderColor?.let { SolidColor(it) } ?: SolidColor(Color.Transparent),
                    shape = RoundedCornerShape(metrics.corner)
                )
        ) {
            // Interaction overlay
            Box(
                Modifier
                    .matchParentSize()
                    .background((overlayBase ?: Color.Transparent).copy(alpha = overlayAlpha))
            )

            // Checkmark path (animated draw)
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = this.size.width
                val h = this.size.height

                val startX = w * 0.28f
                val startY = h * 0.52f
                val midX = w * 0.45f
                val midY = h * 0.70f
                val endX = w * 0.74f
                val endY = h * 0.32f

                val path = Path().apply {
                    moveTo(startX, startY)
                    lineTo(midX, midY)
                    lineTo(endX, endY)
                }

                drawPath(
                    path = path,
                    color = markColor.copy(alpha = progress),
                    style = Stroke(
                        width = metrics.checkStroke.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
