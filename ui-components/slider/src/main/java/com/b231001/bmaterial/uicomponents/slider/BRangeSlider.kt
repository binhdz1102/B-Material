package com.b231001.bmaterial.uicomponents.slider

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private enum class ThumbDrag { Lower, Upper }

/**
 * Note: tick rendering is still limited and should be treated as experimental.
 */
@Composable
fun BRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    limitMin: Float? = null,
    limitMax: Float? = null,
    showTickMarks: Boolean = (steps > 0),
    showValueLabel: Boolean = false,
    style: BSliderStyle = BSliderStyle.Primary,
    size: BSliderSize = BSliderSize.Md,
    colors: BSliderColors = BSliderDefaults.colors(style),
    metrics: BSliderMetrics = BSliderDefaults.metrics(size),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val cs = BTokens.colorScheme
    val density = LocalDensity.current

    val minValue = valueRange.start
    val maxValue = valueRange.endInclusive
    val minLimit = limitMin ?: minValue
    val maxLimit = limitMax ?: maxValue

    val lower0 = value.start.coerceIn(minValue, maxValue)
    val upper0 = value.endInclusive.coerceIn(minValue, maxValue)
    val coercedLower = lower0.coerceAtMost(upper0).coerceIn(minLimit, maxLimit)
    val coercedUpper = upper0.coerceAtLeast(lower0).coerceIn(minLimit, maxLimit)

    val animatedLower by animateFloatAsState(
        targetValue = coercedLower,
        animationSpec = tween(150),
        label = "BRangeSliderLower"
    )
    val animatedUpper by animateFloatAsState(
        targetValue = coercedUpper,
        animationSpec = tween(150),
        label = "BRangeSliderUpper"
    )

    var isDragging by remember { mutableStateOf(false) }
    var draggingThumb by remember { mutableStateOf<ThumbDrag?>(null) }
    var lastDragPosX by remember { mutableFloatStateOf(Float.NaN) }

    var lastSentLower by remember { mutableFloatStateOf(coercedLower) }
    var lastSentUpper by remember { mutableFloatStateOf(coercedUpper) }

    val focused by interactionSource.collectIsFocusedAsState()

    var sliderWidth by remember { mutableFloatStateOf(0f) }
    val thumbRadiusPx = with(density) { (metrics.thumbSize / 2).toPx() }
    val trackLeftPx = thumbRadiusPx
    val trackWidthPx = max(0f, sliderWidth - 2 * thumbRadiusPx)

    val fractionLower = if (maxValue > minValue) {
        (animatedLower - minValue) / (maxValue - minValue)
    } else {
        0f
    }
    val fractionUpper = if (maxValue > minValue) {
        (animatedUpper - minValue) / (maxValue - minValue)
    } else {
        1f
    }

    val lowerCenterX = trackLeftPx + fractionLower * trackWidthPx
    val upperCenterX = trackLeftPx + fractionUpper * trackWidthPx

    val activeTrackColor = if (enabled) colors.trackActive else colors.disabledTrack
    val inactiveTrackColor = if (enabled) colors.trackInactive else colors.disabledTrack

    val thumbColor = if (enabled) colors.thumb else colors.disabledThumb
    val thumbBorderColor = cs.outlineVariant.copy(
        alpha = if (enabled) {
            ComponentTokens.Alpha.ThumbBorderEnabled
        } else {
            ComponentTokens.Alpha.ThumbBorderDisabled
        }
    )

    val latestTrackLeftPx by rememberUpdatedState(trackLeftPx)
    val latestTrackWidthPx by rememberUpdatedState(trackWidthPx)
    val latestLowerCenterX by rememberUpdatedState(lowerCenterX)
    val latestUpperCenterX by rememberUpdatedState(upperCenterX)
    val latestLastLower by rememberUpdatedState(lastSentLower)
    val latestLastUpper by rememberUpdatedState(lastSentUpper)

    fun xToValue(xInTrack: Float, width: Float): Float {
        if (width <= 0f) return minValue
        val frac = (xInTrack / width).coerceIn(0f, 1f)
        val raw = minValue + frac * (maxValue - minValue)
        return raw.coerceIn(minLimit, maxLimit)
    }

    fun snapIfNeeded(v: Float): Float {
        if (steps <= 0) return v
        val stepCount = steps + 1
        val stepIndex = (((v - minValue) / (maxValue - minValue)) * stepCount).roundToInt()
        val snapped = stepIndex.toFloat() / stepCount
        return (minValue + snapped * (maxValue - minValue)).coerceIn(minLimit, maxLimit)
    }

    val trackHeightAnim by animateDpAsState(
        targetValue = if (isDragging) {
            metrics.trackHeight * ComponentTokens.Slider.TrackGrowFactor
        } else {
            metrics.trackHeight
        },
        animationSpec = tween(120),
        label = "TrackGrow"
    )

    val gestureModifier =
        if (enabled) {
            Modifier.pointerInput(enabled) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val pressX = offset.x
                        val dLower = abs(pressX - latestLowerCenterX)
                        val dUpper = abs(pressX - latestUpperCenterX)
                        draggingThumb = if (dLower <= dUpper) ThumbDrag.Lower else ThumbDrag.Upper
                        isDragging = true
                        lastDragPosX = Float.NaN

                        if (abs(dLower - dUpper) <=
                            thumbRadiusPx * ComponentTokens.Slider.OverlapSelectionThresholdFactor
                        ) {
                            draggingThumb =
                                if (pressX >= latestLowerCenterX) {
                                    ThumbDrag.Upper
                                } else {
                                    ThumbDrag.Lower
                                }
                        }

                        val dist = if (draggingThumb == ThumbDrag.Lower) dLower else dUpper
                        if (dist > thumbRadiusPx * ComponentTokens.Slider.ThumbGrabRangeFactor) {
                            val newRaw = xToValue(pressX - latestTrackLeftPx, latestTrackWidthPx)
                            val snapped = snapIfNeeded(newRaw)
                            if (draggingThumb == ThumbDrag.Lower) {
                                val clamped = min(snapped, latestLastUpper)
                                if (clamped != lastSentLower) {
                                    lastSentLower = clamped
                                    onValueChange(clamped..latestLastUpper)
                                }
                            } else {
                                val clamped = max(snapped, latestLastLower)
                                if (clamped != lastSentUpper) {
                                    lastSentUpper = clamped
                                    onValueChange(latestLastLower..clamped)
                                }
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        isDragging = true
                        val posX = change.position.x
                        val dx = if (lastDragPosX.isNaN()) 0f else posX - lastDragPosX
                        lastDragPosX = posX

                        if (latestLastLower == latestLastUpper && dx != 0f) {
                            draggingThumb = if (dx > 0f) ThumbDrag.Upper else ThumbDrag.Lower
                        }

                        val raw = xToValue(posX - latestTrackLeftPx, latestTrackWidthPx)
                        val snapped = snapIfNeeded(raw)

                        when (draggingThumb) {
                            ThumbDrag.Lower -> {
                                val clamped = min(snapped, latestLastUpper)
                                if (clamped != lastSentLower) {
                                    lastSentLower = clamped
                                    onValueChange(clamped..latestLastUpper)
                                }
                            }

                            ThumbDrag.Upper -> {
                                val clamped = max(snapped, latestLastLower)
                                if (clamped != lastSentUpper) {
                                    lastSentUpper = clamped
                                    onValueChange(latestLastLower..clamped)
                                }
                            }

                            null -> Unit
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        isDragging = false
                        draggingThumb = null
                        lastDragPosX = Float.NaN
                        onValueChangeFinished?.invoke()
                    },
                    onDragCancel = {
                        isDragging = false
                        draggingThumb = null
                        lastDragPosX = Float.NaN
                    }
                )
            }
        } else {
            Modifier
        }

    val containerHeight = maxOf(metrics.thumbSize, metrics.trackHeight)

    Box(
        modifier
            .fillMaxWidth()
            .height(containerHeight)
            .then(gestureModifier)
            .focusable(enabled, interactionSource = interactionSource)
            .onSizeChanged { sliderWidth = it.width.toFloat() }
            .semantics(mergeDescendants = true) {}
    ) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(trackHeightAnim)
                .align(Alignment.Center)
        ) {
            val canvasSize = this.size
            val w = canvasSize.width
            val h = canvasSize.height
            val trackH = canvasSize.height
            val trackR = trackH / 2
            val lowerX = fractionLower * w
            val upperX = fractionUpper * w

            if (lowerX > 0f) {
                drawRoundRect(
                    color = inactiveTrackColor,
                    topLeft = Offset(0f, (h - trackH) / 2f),
                    size = Size(lowerX, trackH),
                    cornerRadius = CornerRadius(trackR, trackR)
                )
            }
            val rangeW = (upperX - lowerX).coerceAtLeast(0f)
            if (rangeW > 0f) {
                drawRoundRect(
                    color = activeTrackColor,
                    topLeft = Offset(lowerX, (h - trackH) / 2f),
                    size = Size(rangeW, trackH),
                    cornerRadius = CornerRadius(trackR, trackR)
                )
            }
            if (upperX < w) {
                drawRoundRect(
                    color = inactiveTrackColor,
                    topLeft = Offset(upperX, (h - trackH) / 2f),
                    size = Size(w - upperX, trackH),
                    cornerRadius = CornerRadius(trackR, trackR)
                )
            }
        }

        if (showTickMarks) {
            val ticks: List<Float> = if (steps > 0) {
                val stepCount = steps + 1
                (0..stepCount).map { it.toFloat() / stepCount }
            } else {
                listOf(0f, 1f)
            }

            ticks.forEach { frac ->
                val xDp = with(density) { (frac * sliderWidth).toDp() }
                val inside = frac in fractionLower..fractionUpper
                Box(
                    Modifier
                        .offset(x = xDp - (metrics.tickSize / 2))
                        .align(Alignment.CenterStart)
                        .size(
                            if (inside) {
                                metrics.tickSize * ComponentTokens.Slider.ActiveTickScale
                            } else {
                                metrics.tickSize
                            }
                        )
                        .background(
                            if (inside) activeTrackColor else colors.tickInactive,
                            CircleShape
                        )
                        .zIndex(1f)
                )
            }
        }

        if (showValueLabel && isDragging && draggingThumb == ThumbDrag.Lower) {
            SliderValueTooltip(
                xPx = lowerCenterX,
                text = "${lastSentLower.roundToInt()}",
                parentWidthPx = sliderWidth
            )
        }
        if (showValueLabel && isDragging && draggingThumb == ThumbDrag.Upper) {
            SliderValueTooltip(
                xPx = upperCenterX,
                text = "${lastSentUpper.roundToInt()}",
                parentWidthPx = sliderWidth
            )
        }

        Thumb(
            centerXPx = lowerCenterX,
            thumbRadiusPx = thumbRadiusPx,
            thumbSize = metrics.thumbSize,
            enabled = enabled,
            focused = focused,
            focusHaloRadius = metrics.focusHaloRadius,
            thumbColor = thumbColor,
            thumbBorderColor = thumbBorderColor,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Thumb(
            centerXPx = upperCenterX,
            thumbRadiusPx = thumbRadiusPx,
            thumbSize = metrics.thumbSize,
            enabled = enabled,
            focused = focused,
            focusHaloRadius = metrics.focusHaloRadius,
            thumbColor = thumbColor,
            thumbBorderColor = thumbBorderColor,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun Thumb(
    centerXPx: Float,
    thumbRadiusPx: Float,
    thumbSize: Dp,
    enabled: Boolean,
    focused: Boolean,
    focusHaloRadius: Dp,
    thumbColor: Color,
    thumbBorderColor: Color,
    modifier: Modifier = Modifier
) {
    val cs = BTokens.colorScheme

    Box(
        modifier
            .offset { IntOffset((centerXPx - thumbRadiusPx).roundToInt(), 0) }
            .size(thumbSize)
            .zIndex(1.5f)
    ) {
        if (enabled && focused) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    color = cs.onSurface.copy(alpha = ComponentTokens.Alpha.FocusRing),
                    radius = focusHaloRadius.toPx()
                )
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .shadow(
                    if (enabled) ComponentTokens.Slider.ThumbShadow else 0.dp,
                    CircleShape,
                    clip = false
                )
                .background(thumbColor, CircleShape)
                .border(ComponentTokens.Border.Thin, thumbBorderColor, CircleShape)
        )
    }
}

@Composable
private fun BoxScope.SliderValueTooltip(
    xPx: Float,
    text: String,
    parentWidthPx: Float,
    topPadding: Dp = 0.dp
) {
    val cs = BTokens.colorScheme
    val density = LocalDensity.current
    val labelWidth = ComponentTokens.Slider.TooltipWidth
    val labelHeight = ComponentTokens.Slider.TooltipHeight
    val arrowHeight = ComponentTokens.Slider.TooltipArrowHeight

    val halfLabelPx = with(density) { (labelWidth / 2).toPx() }
    val clampedLeftPx =
        (xPx - halfLabelPx).coerceIn(0f, parentWidthPx - with(density) { labelWidth.toPx() })
    val xDp = with(density) { clampedLeftPx.toDp() }

    Box(
        Modifier
            .zIndex(2f)
            .align(Alignment.TopStart)
            .offset(
                x = xDp,
                y = -(labelHeight + arrowHeight + ComponentTokens.Slider.TooltipGap) - topPadding
            )
            .size(labelWidth, labelHeight)
            .background(
                cs.surface3,
                shape = RoundedCornerShape(ComponentTokens.Slider.TooltipCorner)
            )
            .border(
                ComponentTokens.Border.Thin,
                cs.outlineVariant,
                RoundedCornerShape(ComponentTokens.Slider.TooltipCorner)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BTokens.typography.labelMedium,
            color = cs.onSurface
        )
    }
}
