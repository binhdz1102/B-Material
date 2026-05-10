package com.b231001.bmaterial.uicomponents.layout.row

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.layout.util.bounceOverscroll
import com.b231001.bmaterial.uicore.tokens.BTokens
import com.b231001.bmaterial.uicore.tokens.ComponentTokens
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Stable
class BRowState internal constructor(
    val scrollState: ScrollState
)

@Composable
fun rememberBRowState(
    scrollState: ScrollState = rememberScrollState()
): BRowState = remember { BRowState(scrollState) }

enum class OverscrollEdge { Start, End }

private data class HItemBox(
    val leftRel: Float,
    val rightRel: Float,
    val fullWidth: Float,
    val visibleLeftRel: Float,
    val visibleRightRel: Float
)

private class HVisibleTrackContext(
    val enabled: Boolean,
    val viewportCoords: MutableState<LayoutCoordinates?>,
    val itemBoxes: SnapshotStateMap<Int, HItemBox>
)

private val LocalHVisibleTrack = staticCompositionLocalOf<HVisibleTrackContext?> { null }

fun Modifier.bRowItem(index: Int): Modifier = composed {
    val ctx = LocalHVisibleTrack.current
    if (ctx?.enabled != true) return@composed this

    val vp by ctx.viewportCoords
    this.then(
        Modifier.onGloballyPositioned { child ->
            val anc = vp ?: return@onGloballyPositioned

            val childR = child.boundsInRoot()
            val vpR = anc.boundsInRoot()

            val leftRel = childR.left - vpR.left
            val rightRel = childR.right - vpR.left

            val visibleLeft = max(childR.left, vpR.left)
            val visibleRight = min(childR.right, vpR.right)
            val visibleLeftRel = (visibleLeft - vpR.left).coerceAtLeast(0f)
            val visibleRightRel = (visibleRight - vpR.left).coerceAtMost(vpR.width)

            ctx.itemBoxes[index] = HItemBox(
                leftRel = leftRel,
                rightRel = rightRel,
                fullWidth = child.size.width.toFloat(),
                visibleLeftRel = visibleLeftRel,
                visibleRightRel = visibleRightRel
            )
        }
    )
}

@Composable
fun BRow(
    modifier: Modifier = Modifier,
    state: BRowState = rememberBRowState(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    scrollEnabled: Boolean = true,
    flingOverscrollEnabled: Boolean = false,
    onOverscrollActivated: ((edge: OverscrollEdge) -> Unit)? = null,
    onVisibleRangeChanged:
        ((firstVisible: Int?, lastVisible: Int?, progress01: Float) -> Unit)? = null,
    visibleThreshold: Float = 0.5f,
    autoDividerEnabled: Boolean = false,
    divider: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxHeight()
                .width(ComponentTokens.Layout.DividerThickness)
                .background(BTokens.colorScheme.outlineVariant)
        )
    },
    content: @Composable RowScope.() -> Unit
) {
    var viewportWidthPx by remember { mutableFloatStateOf(0f) }
    val viewportCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }

    var overscrollTranslationX by remember { mutableFloatStateOf(0f) }

    val onEdgeLatest by rememberUpdatedState(onOverscrollActivated)
    var overscrollActive by remember { mutableStateOf(false) }
    var overscrollEdge by remember { mutableStateOf<OverscrollEdge?>(null) }

    val overscrollEnabled =
        scrollEnabled && (flingOverscrollEnabled || onOverscrollActivated != null)

    LaunchedEffect(overscrollEnabled) {
        if (!overscrollEnabled) {
            overscrollTranslationX = 0f
            overscrollActive = false
            overscrollEdge = null
            return@LaunchedEffect
        }

        val activatePx = 1f
        val settlePx = 0.5f
        val settleDelayMs = 80L

        snapshotFlow { overscrollTranslationX }
            .collectLatest { t ->
                val atRest = abs(t) < settlePx
                val shouldActivate = abs(t) > activatePx

                if (!overscrollActive) {
                    if (shouldActivate) {
                        overscrollActive = true
                        overscrollEdge = if (t > 0f) OverscrollEdge.Start else OverscrollEdge.End
                        onEdgeLatest?.invoke(overscrollEdge!!)
                    }
                } else {
                    if (atRest) {
                        delay(settleDelayMs)
                        overscrollActive = false
                        overscrollEdge = null
                    }
                }
            }
    }

    val trackEnabled = onVisibleRangeChanged != null
    val itemBoxes = remember(trackEnabled) {
        if (trackEnabled) mutableStateMapOf<Int, HItemBox>() else mutableStateMapOf()
    }

    val snapshot by remember(trackEnabled, state.scrollState, viewportWidthPx) {
        derivedStateOf {
            if (!trackEnabled || viewportWidthPx <= 0f) {
                Triple<Int?, Int?, Float>(null, null, 0f)
            } else {
                val qualified = itemBoxes.asSequence()
                    .map { (idx, b) ->
                        val visibleW = (b.visibleRightRel - b.visibleLeftRel)
                            .coerceAtLeast(0f)
                        val frac = if (b.fullWidth > 0f) visibleW / b.fullWidth else 0f
                        Triple(idx, b, frac)
                    }
                    .filter { it.third + 1e-4f >= visibleThreshold }
                    .toList()

                val first = qualified.minByOrNull { it.second.visibleLeftRel }?.first
                val last = qualified.maxByOrNull { it.second.visibleRightRel }?.first

                val maxScroll = state.scrollState.maxValue.takeIf { it > 0 } ?: 1
                val progress = (state.scrollState.value.toFloat() / maxScroll).coerceIn(0f, 1f)

                Triple(first, last, progress)
            }
        }
    }

    val onRangeLatest by rememberUpdatedState(onVisibleRangeChanged)
    LaunchedEffect(snapshot) {
        onRangeLatest?.invoke(snapshot.first, snapshot.second, snapshot.third)
    }

    val visibleCtx = remember(trackEnabled) {
        if (trackEnabled) HVisibleTrackContext(true, viewportCoords, itemBoxes) else null
    }

    CompositionLocalProvider(LocalHVisibleTrack provides visibleCtx) {
        Row(
            modifier = modifier
                .onGloballyPositioned { coords ->
                    viewportCoords.value = coords
                    viewportWidthPx = coords.size.width.toFloat()
                }
                .then(
                    if (overscrollEnabled) {
                        Modifier.bounceOverscroll(
                            orientation = Orientation.Horizontal,
                            springSpec = spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            ),
                            rubberBandConstant = ComponentTokens.Overscroll.RubberBandConstant,
                            allowFlingOverscroll = flingOverscrollEnabled,
                            onNewOverscrollTranslation = { overscrollTranslationX = it }
                        )
                    } else {
                        Modifier
                    }
                )
                .clipToBounds()
                .graphicsLayer { translationX = overscrollTranslationX }
                .then(
                    if (scrollEnabled) {
                        Modifier.horizontalScroll(state.scrollState)
                    } else {
                        Modifier
                    }
                ),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment
        ) {
            val localAutoDivider = autoDividerEnabled
            val localDivider = divider

            @Composable
            fun RowScope.BRowItems(
                count: Int,
                item: @Composable (Int) -> Unit
            ) {
                if (!localAutoDivider) {
                    repeat(count) { i -> item(i) }
                } else {
                    repeat(count) { i ->
                        item(i)
                        if (i != count - 1) {
                            Box(
                                Modifier
                                    .padding(vertical = 0.dp)
                                    .fillMaxHeight()
                                    .width(ComponentTokens.Layout.AutoDividerGap)
                            )
                            localDivider()
                        }
                    }
                }
            }

            val BRowItemsRef: @Composable RowScope.(Int, @Composable (Int) -> Unit) -> Unit =
                { count, item -> BRowItems(count, item) }

            with(object {
                @Composable
                fun RowScope.BRowItems(count: Int, item: @Composable (Int) -> Unit) =
                    BRowItemsRef(this, count, item)
            }) {
                content()
            }
        }
    }
}
