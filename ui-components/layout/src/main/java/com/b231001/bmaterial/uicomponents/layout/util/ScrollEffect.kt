package com.b231001.bmaterial.uicomponents.layout.util

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Velocity
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.sign
import kotlinx.coroutines.launch

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.verticalScrollWithBounce(
    scrollState: ScrollState = rememberScrollState(),
    springSpec: SpringSpec<Float> = spring(
        stiffness = Spring.StiffnessLow,
        dampingRatio = Spring.DampingRatioMediumBouncy
    ),
    rubberBandConstant: Float = 0.55f
): Modifier = composed {
    var translation by remember { mutableFloatStateOf(0f) }

    bounceOverscroll(
        orientation = Orientation.Vertical,
        springSpec = springSpec,
        rubberBandConstant = rubberBandConstant,
        onNewOverscrollTranslation = { translation = it }
    )
        .verticalScroll(scrollState)
        .graphicsLayer { translationY = translation }
}

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.horizontalScrollWithBounce(
    scrollState: ScrollState = rememberScrollState(),
    springSpec: SpringSpec<Float> = spring(
        stiffness = Spring.StiffnessLow,
        dampingRatio = Spring.DampingRatioMediumBouncy
    ),
    rubberBandConstant: Float = 0.55f
): Modifier = composed {
    var translation by remember { mutableFloatStateOf(0f) }

    bounceOverscroll(
        orientation = Orientation.Horizontal,
        springSpec = springSpec,
        rubberBandConstant = rubberBandConstant,
        onNewOverscrollTranslation = { translation = it }
    )
        .horizontalScroll(scrollState)
        .graphicsLayer { translationX = translation }
}

fun Modifier.bounceOverscroll(
    orientation: Orientation,
    onNewOverscrollTranslation: (Float) -> Unit,
    springSpec: SpringSpec<Float> = spring(
        stiffness = Spring.StiffnessLow,
        dampingRatio = Spring.DampingRatioMediumBouncy
    ),
    rubberBandConstant: Float = 0.55f,
    allowFlingOverscroll: Boolean = true,
    maxRawOverscrollFactor: Float = 1.5f
): Modifier = composed {
    val overscrollAnim = remember { Animatable(0f) }
    var rawOverscroll by remember { mutableFloatStateOf(0f) }
    var viewportSizePx by remember { mutableFloatStateOf(1f) }
    val scope = rememberCoroutineScope()

    fun dispatchTranslation(raw: Float) {
        val t = rubberBand(distance = raw, size = viewportSizePx, constant = rubberBandConstant)
        onNewOverscrollTranslation(t)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { overscrollAnim.value }.collect { v ->
            rawOverscroll = v
            dispatchTranslation(v)
        }
    }

    fun axis(offset: Offset): Float = when (orientation) {
        Orientation.Vertical -> offset.y
        Orientation.Horizontal -> offset.x
    }

    fun axis(velocity: Velocity): Float = when (orientation) {
        Orientation.Vertical -> velocity.y
        Orientation.Horizontal -> velocity.x
    }

    fun offsetOf(delta: Float): Offset = when (orientation) {
        Orientation.Vertical -> Offset(0f, delta)
        Orientation.Horizontal -> Offset(delta, 0f)
    }

    fun velocityOf(v: Float): Velocity = when (orientation) {
        Orientation.Vertical -> Velocity(0f, v)
        Orientation.Horizontal -> Velocity(v, 0f)
    }

    fun clampSameSign(current: Float, candidate: Float): Float = when {
        current > 0f -> candidate.coerceAtLeast(0f)
        current < 0f -> candidate.coerceAtMost(0f)
        else -> candidate
    }

    fun clampRaw(candidate: Float): Float {
        val maxRaw = (viewportSizePx * maxRawOverscrollFactor).coerceAtLeast(1f)
        return candidate.coerceIn(-maxRaw, maxRaw)
    }

    fun stopAnimIfRunning() {
        if (overscrollAnim.isRunning) {
            scope.launch { overscrollAnim.stop() }
        }
    }

    val connection = remember(orientation, allowFlingOverscroll, maxRawOverscrollFactor) {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.Fling) stopAnimIfRunning()

                if (source == NestedScrollSource.Fling) return Offset.Zero

                val current = rawOverscroll
                if (current == 0f) return Offset.Zero

                val delta = axis(available)
                if (delta == 0f) return Offset.Zero

                val candidate = clampRaw(current + delta)
                val next = clampSameSign(current, candidate)
                val consumedDelta = next - current

                if (consumedDelta != 0f) {
                    rawOverscroll = next
                    dispatchTranslation(next)
                }
                return offsetOf(consumedDelta)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!allowFlingOverscroll && source == NestedScrollSource.Fling) return Offset.Zero

                if (source != NestedScrollSource.Fling) stopAnimIfRunning()

                val delta = axis(available)
                if (delta == 0f) return Offset.Zero

                val current = rawOverscroll
                val candidate = clampRaw(current + delta)
                val next = clampSameSign(current, candidate)
                val consumedDelta = next - current

                if (consumedDelta != 0f) {
                    rawOverscroll = next
                    dispatchTranslation(next)
                }
                return offsetOf(consumedDelta)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val v0 = axis(available)
                val current = rawOverscroll
                if (current == 0f || v0 == 0f) return Velocity.Zero

                // sync anim from raw and animate
                if (overscrollAnim.isRunning) overscrollAnim.stop()
                overscrollAnim.snapTo(current)

                val decay = exponentialDecay<Float>()
                val predicted = decay.calculateTargetValue(current, v0)

                return if (predicted.sign == current.sign) {
                    overscrollAnim.animateTo(
                        targetValue = 0f,
                        initialVelocity = v0,
                        animationSpec = springSpec
                    )
                    available
                } else {
                    var consumed = v0
                    try {
                        overscrollAnim.animateDecay(
                            initialVelocity = v0,
                            animationSpec = decay
                        ) {
                            if (value.sign != current.sign && value != 0f) {
                                val remainingVelocity = velocity
                                consumed = v0 - remainingVelocity
                                scope.launch {
                                    overscrollAnim.snapTo(0f)
                                }
                            }
                        }
                    } catch (_: CancellationException) {
                    }
                    velocityOf(consumed)
                }
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val v = axis(available)
                val current = rawOverscroll
                if (current == 0f && v == 0f) return Velocity.Zero

                if (overscrollAnim.isRunning) overscrollAnim.stop()
                overscrollAnim.snapTo(current)

                overscrollAnim.animateTo(
                    targetValue = 0f,
                    initialVelocity = v,
                    animationSpec = springSpec
                )
                return available
            }
        }
    }

    this
        .onSizeChanged { size ->
            viewportSizePx = when (orientation) {
                Orientation.Vertical -> size.height.toFloat()
                Orientation.Horizontal -> size.width.toFloat()
            }.coerceAtLeast(1f)
        }
        .nestedScroll(connection)
}

private fun rubberBand(distance: Float, size: Float, constant: Float): Float {
    val s = size.coerceAtLeast(1f)
    val d = abs(distance)
    val result = (constant * d * s) / (d + constant * s)
    return result * distance.sign
}
