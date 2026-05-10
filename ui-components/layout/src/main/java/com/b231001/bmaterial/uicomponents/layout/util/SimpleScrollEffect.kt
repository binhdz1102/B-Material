package com.b231001.bmaterial.uicomponents.layout.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
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
import com.b231001.bmaterial.uicore.tokens.ComponentTokens
import kotlin.math.sign
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun Modifier.simpleBouncingVerticalScroll(): Modifier = composed {
    var offset by remember { mutableFloatStateOf(0f) }

    simpleBounceOverscroll(
        orientation = Orientation.Vertical,
        onNewOverscrollAmount = { offset = it }
    )
        .verticalScroll(rememberScrollState())
        .graphicsLayer { translationY = offset }
}

@Composable
fun Modifier.simpleBounceOverscroll(
    orientation: Orientation,
    onNewOverscrollAmount: (Float) -> Unit,
    animationSpec: SpringSpec<Float> = spring(stiffness = Spring.StiffnessLow)
): Modifier = composed {
    val overscroll = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var length by remember { mutableFloatStateOf(1f) }

    val axis: AxisOps = remember(orientation) { AxisOps(orientation) }

    fun calculateOverscroll(available: Offset): Float {
        val previous = overscroll.value
        val newValue = previous + axis.offsetValue(available)

        return when {
            previous > 0f -> newValue.coerceAtLeast(0f)
            previous < 0f -> newValue.coerceAtMost(0f)
            else -> newValue
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { overscroll.value }.collect { value ->
            onNewOverscrollAmount(transformOverscroll(value, length))
        }
    }

    val connection: NestedScrollConnection = remember(orientation, animationSpec) {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (overscroll.value != 0f && source != NestedScrollSource.Fling) {
                    scope.launch { overscroll.snapTo(calculateOverscroll(available)) }
                    return available
                }
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                scope.launch { overscroll.snapTo(calculateOverscroll(available)) }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val availableVelocity = axis.velocityValue(available)

                if (overscroll.value != 0f && availableVelocity != 0f) {
                    var consumedVelocity = availableVelocity
                    val previousSign = overscroll.value.sign

                    val predictedEndValue = exponentialDecay<Float>().calculateTargetValue(
                        initialValue = overscroll.value,
                        initialVelocity = availableVelocity
                    )

                    if (predictedEndValue.sign == previousSign) {
                        overscroll.animateTo(
                            targetValue = 0f,
                            initialVelocity = availableVelocity,
                            animationSpec = animationSpec
                        )
                    } else {
                        try {
                            overscroll.animateDecay(
                                initialVelocity = availableVelocity,
                                animationSpec = exponentialDecay()
                            ) {
                                if (value.sign != previousSign) {
                                    consumedVelocity -= velocity
                                    scope.launch { overscroll.snapTo(0f) }
                                }
                            }
                        } catch (_: CancellationException) {
                            // Cancellation is expected when a new gesture interrupts the decay animation.
                        }
                    }

                    return axis.buildConsumedVelocity(consumedVelocity)
                }

                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val availableVelocity = axis.velocityValue(available)

                overscroll.animateTo(
                    targetValue = 0f,
                    initialVelocity = availableVelocity,
                    animationSpec = animationSpec
                )
                return available
            }
        }
    }

    this
        .onSizeChanged { size ->
            length = axis.lengthFromSize(size.width, size.height)
        }
        .nestedScroll(connection)
}

private fun transformOverscroll(raw: Float, length: Float): Float {
    val positive = raw > 0f
    val toTransform = if (positive) raw else -raw
    val transformed = CBEasing.transform(
        toTransform / (length * ComponentTokens.Overscroll.TransformLengthFactor)
    ) * length
    return if (positive) transformed else -transformed
}

private class AxisOps(private val orientation: Orientation) {
    fun offsetValue(offset: Offset): Float = when (orientation) {
        Orientation.Vertical -> offset.y
        Orientation.Horizontal -> offset.x
    }

    fun velocityValue(velocity: Velocity): Float = when (orientation) {
        Orientation.Vertical -> velocity.y
        Orientation.Horizontal -> velocity.x
    }

    fun buildConsumedVelocity(axisConsumed: Float): Velocity = when (orientation) {
        Orientation.Vertical -> Velocity(0f, axisConsumed)
        Orientation.Horizontal -> Velocity(axisConsumed, 0f)
    }

    fun lengthFromSize(width: Int, height: Int): Float = when (orientation) {
        Orientation.Vertical -> height.toFloat()
        Orientation.Horizontal -> width.toFloat()
    }
}

private val CBEasing: Easing = CubicBezierEasing(0.55f, 0.55f, 1.0f, 0.25f)
