package com.b231001.bmaterial.uicomponents.loading

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens

@Stable
sealed interface BLoadingState {
    data object Indeterminate : BLoadingState

    /** Percent based loading (0f..100f). */
    data class Percent(val value: Float) : BLoadingState

    /** Range based loading (current/max). */
    data class Range(val current: Float, val max: Float) : BLoadingState
}

private fun BLoadingState.progress(): Float? = when (this) {
    BLoadingState.Indeterminate -> null
    is BLoadingState.Percent -> (value / 100f).coerceIn(0f, 1f)
    is BLoadingState.Range -> {
        if (max <= 0f) 0f else (current / max).coerceIn(0f, 1f)
    }
}

@Composable
fun BCircularLoading(
    state: BLoadingState = BLoadingState.Indeterminate,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = BTokens.colorScheme.primary,
    trackColor: Color = BTokens.colorScheme.surfaceVariant
) {
    val progress = state.progress()
    val animatedProgress by animateFloatAsState(
        targetValue = progress ?: 0f,
        animationSpec = tween(durationMillis = 600),
        label = "b_circular_loading_progress"
    )

    if (progress == null) {
        CircularProgressIndicator(
            modifier = modifier.size(size),
            color = color,
            strokeWidth = strokeWidth
        )
    } else {
        CircularProgressIndicator(
            progress = animatedProgress,
            modifier = modifier.size(size),
            color = color,
            trackColor = trackColor,
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun BLinearLoading(
    state: BLoadingState = BLoadingState.Indeterminate,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    color: Color = BTokens.colorScheme.primary,
    trackColor: Color = BTokens.colorScheme.surfaceVariant
) {
    val progress = state.progress()
    val animatedProgress by animateFloatAsState(
        targetValue = progress ?: 0f,
        animationSpec = tween(durationMillis = 600),
        label = "b_linear_loading_progress"
    )

    if (progress == null) {
        LinearProgressIndicator(
            modifier = modifier.height(height),
            color = color,
            trackColor = trackColor
        )
    } else {
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = modifier.height(height),
            color = color,
            trackColor = trackColor
        )
    }
}

@Composable
fun BDotsLoading(
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
    color: Color = BTokens.colorScheme.primary,
    spacing: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "b_dots_loading")
    val delays = listOf(0, 150, 300)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        delays.forEachIndexed { index, delayMillis ->
            val scale by transition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 900
                        0.6f at delayMillis
                        1f at (delayMillis + 300)
                        0.6f at (delayMillis + 600)
                    }
                ),
                label = "b_dots_loading_scale_$index"
            )
            val alpha by transition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 900
                        0.4f at delayMillis
                        1f at (delayMillis + 300)
                        0.4f at (delayMillis + 600)
                    }
                ),
                label = "b_dots_loading_alpha_$index"
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(scale)
                    .alpha(alpha)
                    .background(color, CircleShape)
            )
        }
    }
}
