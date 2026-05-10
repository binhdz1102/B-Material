package com.b231001.bmaterial.uicomponents.slider

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.runtime.LaunchedEffect
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
import com.b231001.bmaterial.uicore.tokens.ComponentTokens
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Note: tick rendering is still limited and should be treated as experimental.
 */
@Composable
fun BSlider(
    value: Float,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    allowedValues: List<Float>? = null,
    limitMin: Float? = null,
    limitMax: Float? = null,
    showTickMarks: Boolean = (steps > 0 || allowedValues != null),
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

    val coercedValue = value.coerceIn(minValue, maxValue).coerceIn(minLimit, maxLimit)
    var lastSentValue by remember { mutableFloatStateOf(coercedValue) }

    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(coercedValue) {
        if (!isDragging) lastSentValue = coercedValue
    }

    val uiValue = lastSentValue
    var dragOffsetFromThumbCenter by remember { mutableFloatStateOf(0f) }

    fun positionToValue(xInTrackPx: Float, trackWidthPx: Float): Float {
        if (trackWidthPx <= 0f) return minValue
        val fraction = (xInTrackPx / trackWidthPx).coerceIn(0f, 1f)
        var newValue = minValue + fraction * (maxValue - minValue)
        if (!allowedValues.isNullOrEmpty()) {
            val sorted = allowedValues.sorted()
            newValue = sorted.minByOrNull { kotlin.math.abs(it - newValue) } ?: newValue
        } else if (steps > 0) {
            val stepCount = steps + 1
            val stepIndex =
                (((newValue - minValue) / (maxValue - minValue)) * stepCount).roundToInt()
            val snapped = stepIndex.toFloat() / stepCount
            newValue = minValue + snapped * (maxValue - minValue)
        }
        return newValue.coerceIn(minLimit, maxLimit)
    }

    // Track geometry in pixels.
    var sliderWidth by remember { mutableFloatStateOf(0f) }
    val thumbRadiusPx = with(density) { (metrics.thumbSize / 2).toPx() }
    val trackLeftPx = thumbRadiusPx
    val trackWidthPx = max(0f, sliderWidth - 2 * thumbRadiusPx)

    val fractionFilled = if (maxValue > minValue) {
        (uiValue - minValue) / (maxValue - minValue)
    } else {
        0f
    }

    val fractionStartLimit = if (maxLimit > minLimit) {
        (minLimit - minValue) / (maxValue - minValue)
    } else {
        0f
    }
    val fractionEndLimit = if (maxLimit > minLimit) {
        (maxLimit - minValue) / (maxValue - minValue)
    } else {
        1f
    }

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

    val thumbCenterX: Float = trackLeftPx + fractionFilled * trackWidthPx

    val latestTrackLeftPx by rememberUpdatedState(trackLeftPx)
    val latestTrackWidthPx by rememberUpdatedState(trackWidthPx)
    val latestThumbCenterX by rememberUpdatedState(thumbCenterX)
    val latestLastSent by rememberUpdatedState(lastSentValue)

    val focused by interactionSource.collectIsFocusedAsState()

    val trackHeightAnim by animateDpAsState(
        targetValue = if (isDragging) {
            metrics.trackHeight * ComponentTokens.Slider.TrackGrowFactor
        } else {
            metrics.trackHeight
        },
        animationSpec = tween(120),
        label = "TrackGrow"
    )

    val gestureModifier = if (enabled) {
        Modifier.pointerInput(true) {
            detectDragGestures(
                onDragStart = { offset ->
                    isDragging = true

                    val distToThumb = kotlin.math.abs(offset.x - latestThumbCenterX)
                    val isPressOnThumb =
                        distToThumb <= thumbRadiusPx * ComponentTokens.Slider.ThumbGrabRangeFactor

                    dragOffsetFromThumbCenter = if (isPressOnThumb) {
                        offset.x - latestThumbCenterX
                    } else {
                        0f
                    }

                    if (!isPressOnThumb) {
                        val newValue = positionToValue(
                            offset.x - latestTrackLeftPx,
                            latestTrackWidthPx
                        )
                        if (newValue != latestLastSent) {
                            lastSentValue = newValue
                            onValueChange(newValue)
                        }
                    }
                },
                onDrag = { change, _ ->
                    val desiredThumbCenterX = change.position.x - dragOffsetFromThumbCenter
                    val newValue = positionToValue(
                        desiredThumbCenterX - latestTrackLeftPx,
                        latestTrackWidthPx
                    )
                    if (newValue != lastSentValue) {
                        lastSentValue = newValue
                        onValueChange(newValue)
                    }
                    change.consume()
                },
                onDragEnd = {
                    isDragging = false
                    dragOffsetFromThumbCenter = 0f
                    onValueChangeFinished?.invoke()
                },
                onDragCancel = {
                    isDragging = false
                    dragOffsetFromThumbCenter = 0f
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
        // Track
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

            val startLimitX = fractionStartLimit * w
            if (startLimitX > 0f) {
                drawRoundRect(
                    color = colors.disabledTrack,
                    topLeft = Offset(0f, (h - trackH) / 2f),
                    size = Size(startLimitX, trackH),
                    cornerRadius = CornerRadius(trackR, trackR)
                )
            }

            val endLimitX = fractionEndLimit * w
            if (endLimitX < w) {
                drawRoundRect(
                    color = colors.disabledTrack,
                    topLeft = Offset(endLimitX, (h - trackH) / 2f),
                    size = Size(w - endLimitX, trackH),
                    cornerRadius = CornerRadius(trackR, trackR)
                )
            }

            val centralStart = max(0f, startLimitX)
            val centralEnd = min(w, endLimitX)
            val centralW = max(0f, centralEnd - centralStart)
            if (centralW > 0f) {
                drawRoundRect(
                    color = inactiveTrackColor,
                    topLeft = Offset(centralStart, (h - trackH) / 2f),
                    size = Size(centralW, trackH),
                    cornerRadius = CornerRadius(trackR, trackR)
                )
                val filledX =
                    if (uiValue <= minLimit) {
                        0f
                    } else {
                        (
                            (uiValue.coerceAtMost(maxLimit) - minLimit) /
                                (maxLimit - minLimit)
                            ) * centralW
                    }

                if (filledX > 0f) {
                    drawRoundRect(
                        color = activeTrackColor,
                        topLeft = Offset(centralStart, (h - trackH) / 2f),
                        size = Size(filledX, trackH),
                        cornerRadius = CornerRadius(trackR, trackR)
                    )
                }
            }
        }

        // Tick marks
        if (showTickMarks) {
            val ticks: List<Float> = when {
                !allowedValues.isNullOrEmpty() -> {
                    allowedValues.sorted().map {
                        (it.coerceIn(minValue, maxValue) - minValue) / (maxValue - minValue)
                    }
                }

                steps > 0 -> {
                    val stepCount = steps + 1
                    (0..stepCount).map { it.toFloat() / stepCount }
                }

                else -> listOf(0f, 1f)
            }

            ticks.forEach { frac ->
                if (frac < fractionStartLimit || frac > fractionEndLimit) return@forEach
                val xOffsetDp = with(density) { (frac * sliderWidth).toDp() }
                val isActive = frac <= fractionFilled
                Box(
                    Modifier
                        .offset(x = xOffsetDp - (metrics.tickSize / 2))
                        .align(Alignment.CenterStart)
                        .size(
                            if (isActive) {
                                metrics.tickSize * ComponentTokens.Slider.ActiveTickScale
                            } else {
                                metrics.tickSize
                            }
                        )
                        .background(
                            if (isActive) colors.tickActive else colors.tickInactive,
                            CircleShape
                        )
                        .zIndex(1f)
                )
            }
        }

        // Floating value label shown while dragging.
        if (showValueLabel && isDragging) {
            SliderValueTooltip(
                xPx = thumbCenterX,
                text = "${lastSentValue.roundToInt()}",
                parentWidthPx = sliderWidth
            )
        }

        // Thumb
        Box(
            Modifier
                .offset { IntOffset((thumbCenterX - thumbRadiusPx).roundToInt(), 0) }
                .size(metrics.thumbSize)
                .align(Alignment.CenterStart)
                .zIndex(1.5f)
        ) {
            if (enabled && focused) {
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        color = cs.onSurface.copy(alpha = ComponentTokens.Alpha.FocusRing),
                        radius = metrics.focusHaloRadius.toPx()
                    )
                }
            }
            Box(
                Modifier
                    .matchParentSize()
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
}

/** Floating value label positioned from the thumb center in pixels. */
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
