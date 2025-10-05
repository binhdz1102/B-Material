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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.b231001.bmaterial.uicore.tokens.BTokens
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private enum class ThumbDrag { Lower, Upper }

/**
 * Note: the showTickMarks option is not fully developed and cannot work yet!
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

    // Normalize
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

    // Geometry
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
    val thumbBorderColor =
        BTokens.colorScheme.outlineVariant.copy(alpha = if (enabled) 0.6f else 0.3f)

    // latest geometry/state for pointerInput
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
        targetValue = if (isDragging) metrics.trackHeight * 1.6f else metrics.trackHeight,
        animationSpec = tween(120),
        label = "TrackGrow"
    )

    val gestureModifier =
        if (enabled) {
            Modifier.pointerInput(enabled) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val pressX = offset.x
                        val dLower = kotlin.math.abs(pressX - latestLowerCenterX)
                        val dUpper = kotlin.math.abs(pressX - latestUpperCenterX)
                        draggingThumb = if (dLower <= dUpper) ThumbDrag.Lower else ThumbDrag.Upper
                        isDragging = true
                        lastDragPosX = Float.NaN

                        // if 2 thumbs are overlapping (dLower≈dUpper)
                        // → select according to the touching side
                        if (kotlin.math.abs(dLower - dUpper) <= thumbRadiusPx * 0.25f) {
                            draggingThumb =
                                if (pressX >= latestLowerCenterX) {
                                    ThumbDrag.Upper
                                } else {
                                    ThumbDrag.Lower
                                }
                        }

                        // If you press far thumb → jump the nearest thumb to the pressed position
                        val dist = if (draggingThumb == ThumbDrag.Lower) dLower else dUpper
                        if (dist > thumbRadiusPx * 1.5f) {
                            val newRaw =
                                xToValue(pressX - latestTrackLeftPx, latestTrackWidthPx)
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

                        // If the 2 ends overlap → select the thumb in the direction of the drag
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
        // TRACK
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

        // TICKS
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
                        .size(if (inside) metrics.tickSize * 1.25f else metrics.tickSize)
                        .background(
                            if (inside) activeTrackColor else colors.tickInactive,
                            CircleShape
                        )
                        .zIndex(1f)
                )
            }
        }

        // TOOLTIP
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

        // LOWER THUMB
        Box(
            Modifier
                .offset { IntOffset((lowerCenterX - thumbRadiusPx).roundToInt(), 0) }
                .size(metrics.thumbSize)
                .align(Alignment.CenterStart)
                .zIndex(1.5f)
        ) {
            if (enabled && focused) {
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        color = cs.onSurface.copy(alpha = 0.32f),
                        radius = metrics.focusHaloRadius.toPx()
                    )
                }
            }
            Box(
                Modifier
                    .matchParentSize()
                    .shadow(if (enabled) 2.dp else 0.dp, CircleShape, clip = false)
                    .background(thumbColor, CircleShape)
                    .border(1.dp, thumbBorderColor, CircleShape)
            )
        }

        // UPPER THUMB
        Box(
            Modifier
                .offset { IntOffset((upperCenterX - thumbRadiusPx).roundToInt(), 0) }
                .size(metrics.thumbSize)
                .align(Alignment.CenterStart)
                .zIndex(1.5f)
        ) {
            if (enabled && focused) {
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        color = cs.onSurface.copy(alpha = 0.32f),
                        radius = metrics.focusHaloRadius.toPx()
                    )
                }
            }
            Box(
                Modifier
                    .matchParentSize()
                    .shadow(if (enabled) 2.dp else 0.dp, CircleShape, clip = false)
                    .background(thumbColor, CircleShape)
                    .border(1.dp, thumbBorderColor, CircleShape)
            )
        }
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
    val labelWidth = 44.dp
    val labelHeight = 26.dp
    val arrowHeight = 6.dp

    val halfLabelPx = with(density) { (labelWidth / 2).toPx() }
    val clampedLeftPx =
        (xPx - halfLabelPx).coerceIn(0f, parentWidthPx - with(density) { labelWidth.toPx() })
    val xDp = with(density) { clampedLeftPx.toDp() }

    Box(
        Modifier
            .zIndex(2f)
            .align(Alignment.TopStart)
            .offset(x = xDp, y = -(labelHeight + arrowHeight + 4.dp) - topPadding)
            .size(labelWidth, labelHeight)
            .background(cs.surface3, shape = RoundedCornerShape(6.dp))
            .border(1.dp, cs.outlineVariant, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BTokens.typography.labelMedium,
            color = cs.onSurface
        )
    }
}
