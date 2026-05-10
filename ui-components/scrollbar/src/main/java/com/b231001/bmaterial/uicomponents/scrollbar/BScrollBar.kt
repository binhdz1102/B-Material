package com.b231001.bmaterial.uicomponents.scrollbar

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Immutable
data class BScrollbarStyle(
    val thickness: Dp = ComponentTokens.Scrollbar.Thickness,
    val padding: Dp = ComponentTokens.Scrollbar.Padding,
    val minThumbLength: Dp = ComponentTokens.Scrollbar.MinThumbLength,
    val cornerRadius: Dp = ComponentTokens.Scrollbar.CornerRadius,
    val trackColor: Color = Color.Unspecified,
    val thumbColor: Color = Color.Unspecified,
    val tooltipBg: Color = Color.Unspecified,
    val tooltipText: Color = Color.Unspecified,
    val tooltipTextSize: TextUnit = ComponentTokens.Scrollbar.TooltipTextSize,
    val tooltipPaddingH: Dp = ComponentTokens.Scrollbar.TooltipPaddingH,
    val tooltipPaddingV: Dp = ComponentTokens.Scrollbar.TooltipPaddingV,
    val tooltipGapFromThumb: Dp = ComponentTokens.Scrollbar.TooltipGapFromThumb
)

fun Modifier.bScrollbar(
    scrollState: ScrollState,
    orientation: Orientation = Orientation.Vertical,
    style: BScrollbarStyle = BScrollbarStyle(),
    touchToSeekEnabled: Boolean = true,
    showTooltip: Boolean = true,
    autoHideEnabled: Boolean = true,
    autoHideDelayMillis: Long = ComponentTokens.Scrollbar.AutoHideDelayMillis,
    fadeInMillis: Int = ComponentTokens.Scrollbar.FadeInMillis,
    fadeOutMillis: Int = ComponentTokens.Scrollbar.FadeOutMillis,

    tooltipFormatter: (progress01: Float) -> String = { p ->
        "${(p.coerceIn(0f, 1f) * 100f).roundToInt()}%"
    }
): Modifier = bScrollbarImpl(
    adapter = ScrollStateAdapter(scrollState),
    orientation = orientation,
    style = style,
    touchToSeekEnabled = touchToSeekEnabled,
    showTooltip = showTooltip,
    autoHideEnabled = autoHideEnabled,
    autoHideDelayMillis = autoHideDelayMillis,
    fadeInMillis = fadeInMillis,
    fadeOutMillis = fadeOutMillis,
    tooltipFormatter = tooltipFormatter
)

fun Modifier.bLazyScrollbar(
    state: LazyListState,
    orientation: Orientation = Orientation.Vertical,
    style: BScrollbarStyle = BScrollbarStyle(),
    touchToSeekEnabled: Boolean = true,
    showTooltip: Boolean = true,
    autoHideEnabled: Boolean = true,
    autoHideDelayMillis: Long = ComponentTokens.Scrollbar.AutoHideDelayMillis,
    fadeInMillis: Int = ComponentTokens.Scrollbar.FadeInMillis,
    fadeOutMillis: Int = ComponentTokens.Scrollbar.FadeOutMillis,
    tooltipFormatter: (progress01: Float) -> String = { p ->
        "${(p.coerceIn(0f, 1f) * 100f).roundToInt()}%"
    }
): Modifier = bScrollbarImpl(
    adapter = LazyListStateAdapter(state),
    orientation = orientation,
    style = style,
    touchToSeekEnabled = touchToSeekEnabled,
    showTooltip = showTooltip,
    autoHideEnabled = autoHideEnabled,
    autoHideDelayMillis = autoHideDelayMillis,
    fadeInMillis = fadeInMillis,
    fadeOutMillis = fadeOutMillis,
    tooltipFormatter = tooltipFormatter
)

private interface ScrollbarAdapter {
    val canScroll: Boolean
    val progress01: Float // 0..1
    val isScrollInProgress: Boolean
    fun contentLengthPx(viewportLengthPx: Int): Int
    suspend fun scrollToProgress01(p01: Float)
}

private class ScrollStateAdapter(
    private val state: ScrollState
) : ScrollbarAdapter {

    override val canScroll: Boolean get() = state.maxValue > 0
    override val isScrollInProgress: Boolean get() = state.isScrollInProgress

    override val progress01: Float
        get() {
            val maxV = state.maxValue
            return if (maxV <= 0) {
                0f
            } else {
                (state.value.toFloat() / maxV.toFloat()).coerceIn(0f, 1f)
            }
        }

    override fun contentLengthPx(viewportLengthPx: Int): Int =
        viewportLengthPx + state.maxValue

    override suspend fun scrollToProgress01(p01: Float) {
        val maxV = state.maxValue
        val target = (p01.coerceIn(0f, 1f) * maxV.toFloat()).roundToInt()
        state.scrollTo(target)
    }
}

private class LazyListStateAdapter(
    private val state: LazyListState
) : ScrollbarAdapter {

    private fun totalItems(): Int = state.layoutInfo.totalItemsCount

    override val canScroll: Boolean
        get() = totalItems() > 0 && (state.canScrollForward || state.canScrollBackward)

    override val isScrollInProgress: Boolean get() = state.isScrollInProgress

    override val progress01: Float
        get() {
            val total = totalItems().coerceAtLeast(1)
            val avg = averageItemSizePx().coerceAtLeast(1)
            val pos = state.firstVisibleItemIndex.toFloat() +
                (state.firstVisibleItemScrollOffset.toFloat() / avg.toFloat())
            return (pos / (total - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
        }

    override fun contentLengthPx(viewportLengthPx: Int): Int {
        val total = totalItems().coerceAtLeast(1)
        val avg = averageItemSizePx().coerceAtLeast(1)
        return max(viewportLengthPx, total * avg)
    }

    private fun averageItemSizePx(): Int {
        val visible = state.layoutInfo.visibleItemsInfo
        if (visible.isEmpty()) return 1
        val sum = visible.sumOf { it.size }
        return (sum / visible.size).coerceAtLeast(1)
    }

    override suspend fun scrollToProgress01(p01: Float) {
        val total = totalItems().coerceAtLeast(1)
        val targetIndex = (p01.coerceIn(0f, 1f) * (total - 1)).roundToInt()
        state.scrollToItem(index = targetIndex, scrollOffset = 0)
    }
}

private fun Modifier.bScrollbarImpl(
    adapter: ScrollbarAdapter,
    orientation: Orientation,
    style: BScrollbarStyle,
    touchToSeekEnabled: Boolean,
    showTooltip: Boolean,
    autoHideEnabled: Boolean,
    autoHideDelayMillis: Long,
    fadeInMillis: Int,
    fadeOutMillis: Int,
    tooltipFormatter: (Float) -> String
): Modifier = composed {
    val density = LocalDensity.current
    val cs = BTokens.colorScheme
    val scope = rememberCoroutineScope()
    val resolvedStyle = style.resolveDefaults(cs)

    val thicknessPx = with(density) { resolvedStyle.thickness.toPx() }
    val paddingPx = with(density) { resolvedStyle.padding.toPx() }
    val minThumbPx = with(density) { resolvedStyle.minThumbLength.toPx() }
    val radiusPx = with(density) { resolvedStyle.cornerRadius.toPx() }
    val hitSlopPx = with(density) { ComponentTokens.Scrollbar.HitSlop.toPx() }

    val tipPadHPx = with(density) { resolvedStyle.tooltipPaddingH.toPx() }
    val tipPadVPx = with(density) { resolvedStyle.tooltipPaddingV.toPx() }
    val tipGapPx = with(density) { resolvedStyle.tooltipGapFromThumb.toPx() }
    val tipTextSizePx = with(density) { resolvedStyle.tooltipTextSize.toPx() }

    var hostSize by remember { mutableStateOf(Size.Zero) }

    var interacting by remember { mutableStateOf(false) }
    var lastTooltipText by remember { mutableStateOf("") }

    var seekJob by remember { mutableStateOf<Job?>(null) }
    var hideJob by remember { mutableStateOf<Job?>(null) }

    val alphaAnim = remember(autoHideEnabled) {
        Animatable(if (autoHideEnabled) 1f else 1f)
    }

    fun showBar() {
        if (!autoHideEnabled) return
        hideJob?.cancel()
        scope.launch {
            if (alphaAnim.value < 1f) {
                alphaAnim
                    .animateTo(1f, animationSpec = tween(durationMillis = fadeInMillis))
            }
        }
    }

    fun scheduleHide() {
        if (!autoHideEnabled) return
        hideJob?.cancel()
        hideJob = scope.launch {
            delay(autoHideDelayMillis)
            if (!interacting && !adapter.isScrollInProgress) {
                alphaAnim.animateTo(
                    0f,
                    animationSpec = tween(durationMillis = fadeOutMillis)
                )
            }
        }
    }

    LaunchedEffect(autoHideEnabled, adapter) {
        if (!autoHideEnabled) {
            hideJob?.cancel()
            alphaAnim.snapTo(1f)
            return@LaunchedEffect
        }

        snapshotFlow { adapter.canScroll }
            .distinctUntilChanged()
            .collect { can ->
                if (!can) {
                    hideJob?.cancel()
                    alphaAnim.snapTo(0f)
                } else {
                    alphaAnim.snapTo(1f)
                    scheduleHide()
                }
            }
    }

    LaunchedEffect(autoHideEnabled, adapter) {
        if (!autoHideEnabled) return@LaunchedEffect
        snapshotFlow { adapter.isScrollInProgress }
            .distinctUntilChanged()
            .collect { inProgress ->
                if (!adapter.canScroll) return@collect
                if (inProgress) showBar() else scheduleHide()
            }
    }

    val textPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.LEFT
        }
    }
    SideEffect {
        textPaint.textSize = tipTextSizePx
        textPaint.color = resolvedStyle.tooltipText.toArgb()
    }

    fun isHitScrollbar(pos: Offset): Boolean {
        if (!adapter.canScroll) return false
        val w = hostSize.width
        val h = hostSize.height
        if (w <= 0f || h <= 0f) return false

        return if (orientation == Orientation.Vertical) {
            val left = w - thicknessPx - paddingPx
            pos.x >= (left - hitSlopPx) && pos.x <= w
        } else {
            val top = h - thicknessPx - paddingPx
            pos.y >= (top - hitSlopPx) && pos.y <= h
        }
    }

    data class ThumbTrack(
        val track: RoundRect,
        val thumb: RoundRect,
        val thumbLen: Float
    )

    fun computeThumbTrack(host: Size): ThumbTrack {
        val viewportLen = if (orientation == Orientation.Vertical) host.height else host.width
        val viewportPx = viewportLen.roundToInt().coerceAtLeast(1)
        val contentPx = adapter.contentLengthPx(viewportPx).coerceAtLeast(1)

        val trackLen = max(0f, viewportLen - paddingPx * 2f)
        if (!adapter.canScroll || trackLen <= 1f || contentPx <= viewportPx) {
            val empty = RoundRect(0f, 0f, 0f, 0f, CornerRadius(radiusPx))
            return ThumbTrack(empty, empty, 0f)
        }

        val rawThumb = trackLen * (viewportPx.toFloat() / contentPx.toFloat())
        val thumbLen = rawThumb.coerceIn(minThumbPx, trackLen)

        val p = adapter.progress01
        val travel = (trackLen - thumbLen).coerceAtLeast(0f)
        val thumbOffset = paddingPx + travel * p

        val trackRect = if (orientation == Orientation.Vertical) {
            val left = host.width - thicknessPx - paddingPx
            RoundRect(
                left = left,
                top = paddingPx,
                right = left + thicknessPx,
                bottom = host.height - paddingPx,
                cornerRadius = CornerRadius(radiusPx)
            )
        } else {
            val top = host.height - thicknessPx - paddingPx
            RoundRect(
                left = paddingPx,
                top = top,
                right = host.width - paddingPx,
                bottom = top + thicknessPx,
                cornerRadius = CornerRadius(radiusPx)
            )
        }

        val thumbRect = if (orientation == Orientation.Vertical) {
            val left = host.width - thicknessPx - paddingPx
            RoundRect(
                left = left,
                top = thumbOffset,
                right = left + thicknessPx,
                bottom = thumbOffset + thumbLen,
                cornerRadius = CornerRadius(radiusPx)
            )
        } else {
            val top = host.height - thicknessPx - paddingPx
            RoundRect(
                left = thumbOffset,
                top = top,
                right = thumbOffset + thumbLen,
                bottom = top + thicknessPx,
                cornerRadius = CornerRadius(radiusPx)
            )
        }

        return ThumbTrack(trackRect, thumbRect, thumbLen)
    }

    fun seekAsync(p01: Float) {
        val p = p01.coerceIn(0f, 1f)
        lastTooltipText = tooltipFormatter(p)

        if (autoHideEnabled) showBar()

        seekJob?.cancel()
        seekJob = scope.launch { adapter.scrollToProgress01(p) }
    }

    val seekModifier: Modifier =
        if (!touchToSeekEnabled) {
            Modifier
        } else {
            Modifier.pointerInput(adapter, orientation, hostSize) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (!isHitScrollbar(down.position)) return@awaitEachGesture

                    down.consume()
                    interacting = true
                    if (autoHideEnabled) showBar()

                    val tt = computeThumbTrack(hostSize)
                    if (tt.thumbLen <= 0f) {
                        interacting = false
                        if (autoHideEnabled) scheduleHide()
                        return@awaitEachGesture
                    }

                    val grab = if (orientation == Orientation.Vertical) {
                        (down.position.y - tt.thumb.top).coerceIn(0f, tt.thumbLen)
                    } else {
                        (down.position.x - tt.thumb.left).coerceIn(0f, tt.thumbLen)
                    }

                    fun posToProgress(pos: Offset): Float {
                        val viewportLen = if (orientation == Orientation.Vertical) {
                            hostSize.height
                        } else {
                            hostSize.width
                        }
                        val trackLen = max(0f, viewportLen - paddingPx * 2f)
                        val travel = (trackLen - tt.thumbLen).coerceAtLeast(1f)

                        val raw = if (orientation == Orientation.Vertical) {
                            (pos.y - paddingPx - grab) / travel
                        } else {
                            (pos.x - paddingPx - grab) / travel
                        }
                        return raw.coerceIn(0f, 1f)
                    }

                    // Seek immediately on pointer down so taps jump to the requested position.
                    seekAsync(posToProgress(down.position))

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break

                        if (change.changedToUpIgnoreConsumed()) {
                            change.consume()
                            interacting = false
                            if (autoHideEnabled) scheduleHide()
                            break
                        }

                        seekAsync(posToProgress(change.position))
                        change.consume()
                    }
                }
            }
        }

    this
        .onSizeChanged { hostSize = Size(it.width.toFloat(), it.height.toFloat()) }
        .then(seekModifier)
        .drawWithContent {
            drawContent()

            val host = Size(size.width, size.height)
            val tt = computeThumbTrack(host)
            if (tt.thumbLen <= 0f) return@drawWithContent

            val alpha = if (autoHideEnabled) alphaAnim.value else 1f
            if (alpha <= 0.01f) return@drawWithContent

            val trackC =
                resolvedStyle.trackColor.copy(alpha = resolvedStyle.trackColor.alpha * alpha)
            val thumbC =
                resolvedStyle.thumbColor.copy(alpha = resolvedStyle.thumbColor.alpha * alpha)

            // Track
            drawRoundRect(
                color = trackC,
                topLeft = Offset(tt.track.left, tt.track.top),
                size = Size(tt.track.width, tt.track.height),
                cornerRadius = CornerRadius(radiusPx)
            )

            // Thumb
            drawRoundRect(
                color = thumbC,
                topLeft = Offset(tt.thumb.left, tt.thumb.top),
                size = Size(tt.thumb.width, tt.thumb.height),
                cornerRadius = CornerRadius(radiusPx)
            )

            // Tooltip
            if (showTooltip && interacting) {
                val text = lastTooltipText
                if (text.isNotBlank()) {
                    val textW = textPaint.measureText(text)
                    val textH = textPaint.fontMetrics.run { bottom - top }

                    val bubbleW = textW + tipPadHPx * 2f
                    val bubbleH = textH + tipPadVPx * 2f

                    val thumbCenter = if (orientation == Orientation.Vertical) {
                        (tt.thumb.top + tt.thumb.bottom) / 2f
                    } else {
                        (tt.thumb.left + tt.thumb.right) / 2f
                    }

                    val bubbleTopLeft = if (orientation == Orientation.Vertical) {
                        val left = (tt.thumb.left - tipGapPx - bubbleW)
                            .coerceAtLeast(0f)
                        val top = (thumbCenter - bubbleH / 2f)
                            .coerceIn(0f, host.height - bubbleH)
                        Offset(left, top)
                    } else {
                        val left = (thumbCenter - bubbleW / 2f)
                            .coerceIn(0f, host.width - bubbleW)
                        val top = (tt.thumb.top - tipGapPx - bubbleH)
                            .coerceAtLeast(0f)
                        Offset(left, top)
                    }

                    val tipBg =
                        resolvedStyle.tooltipBg.copy(alpha = resolvedStyle.tooltipBg.alpha * alpha)

                    drawRoundRect(
                        color = tipBg,
                        topLeft = bubbleTopLeft,
                        size = Size(bubbleW, bubbleH),
                        cornerRadius = CornerRadius(radiusPx)
                    )

                    drawIntoCanvas { canvas ->
                        textPaint.color = resolvedStyle.tooltipText.toArgb()
                        val x = bubbleTopLeft.x + tipPadHPx
                        val baseline = bubbleTopLeft.y + tipPadVPx - textPaint.fontMetrics.top
                        canvas.nativeCanvas.drawText(text, x, baseline, textPaint)
                    }
                }
            }
        }
}

private fun BScrollbarStyle.resolveDefaults(cs: com.b231001.bmaterial.uicore.tokens.ColorScheme):
    BScrollbarStyle = copy(
    trackColor = if (trackColor == Color.Unspecified) {
        cs.onSurface.copy(alpha = ComponentTokens.Scrollbar.TrackAlpha)
    } else {
        trackColor
    },
    thumbColor = if (thumbColor == Color.Unspecified) {
        cs.onSurface.copy(alpha = ComponentTokens.Scrollbar.ThumbAlpha)
    } else {
        thumbColor
    },
    tooltipBg = if (tooltipBg == Color.Unspecified) {
        cs.onSurface.copy(alpha = ComponentTokens.Scrollbar.TooltipBackgroundAlpha)
    } else {
        tooltipBg
    },
    tooltipText = if (tooltipText == Color.Unspecified) cs.surface else tooltipText
)
