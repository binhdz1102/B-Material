package com.b231001.bmaterial.uicomponents.layout.column

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.launch

@Stable
class BColumnState internal constructor(
    val scrollState: ScrollState
)

@Composable
fun rememberBColumnState(
    scrollState: ScrollState = rememberScrollState()
): BColumnState = remember { BColumnState(scrollState) }

enum class OverscrollEdge { Start, End }

private data class ItemBox(
    val topRel: Float,
    val bottomRel: Float,
    val fullHeight: Float,
    val visibleTopRel: Float,
    val visibleBottomRel: Float
)

private class VisibleTrackContext(
    val enabled: Boolean,
    val viewportCoords: MutableState<LayoutCoordinates?>,
    val itemBoxes: SnapshotStateMap<Int, ItemBox>
)

private val LocalVisibleTrack = staticCompositionLocalOf<VisibleTrackContext?> { null }

fun Modifier.bColumnItem(index: Int): Modifier = composed {
    val ctx = LocalVisibleTrack.current
    if (ctx?.enabled != true) return@composed this

    val vp by ctx.viewportCoords
    this.then(
        Modifier.onGloballyPositioned { child ->
            val anc = vp ?: return@onGloballyPositioned

            val childR = child.boundsInRoot()
            val vpR = anc.boundsInRoot()

            val topRel = childR.top - vpR.top
            val bottomRel = childR.bottom - vpR.top

            val visibleTop = max(childR.top, vpR.top)
            val visibleBottom = min(childR.bottom, vpR.bottom)
            val visibleTopRel = (visibleTop - vpR.top).coerceAtLeast(0f)
            val visibleBottomRel = (visibleBottom - vpR.top).coerceAtMost(vpR.height)

            ctx.itemBoxes[index] = ItemBox(
                topRel = topRel,
                bottomRel = bottomRel,
                fullHeight = child.size.height.toFloat(),
                visibleTopRel = visibleTopRel,
                visibleBottomRel = visibleBottomRel
            )
        }
    )
}

/**
 * A high-level composable that extends [Column] with scroll, overscroll,
 * visible-range tracking, and optional auto-divider insertion between items.
 *
 * This is part of the **B-Material** design system and serves as a flexible,
 * reusable container for vertically stacked items with extra behavioral options.
 *
 * ---
 *
 * ## Core behavior
 * `BColumn` behaves like a standard `Column` but can:
 * - Become scrollable via `scrollEnabled`.
 * - Animate overscroll bounce when reaching start or end, if enabled.
 * - Detect visible item range (first/last visible) and scroll progress (0f..1f).
 * - Automatically insert a divider composable between items.
 *
 * ---
 *
 * ## Example
 * ```kotlin
 * BColumn(
 *     scrollEnabled = true,
 *     onVisibleRangeChanged = { first, last, progress ->
 *         Log.d("BColumn", "Visible: $first..$last, scroll = ${(progress * 100).toInt()}%")
 *     },
 *     autoDividerEnabled = true
 * ) {
 *     repeat(20) { i ->
 *         Text(
 *             "Item #$i",
 *             Modifier
 *                 .fillMaxWidth()
 *                 .padding(12.dp)
 *                 .bColumnItem(i)
 *         )
 *     }
 * }
 * ```
 *
 * ---
 *
 * @param modifier Modifier applied to the entire column container.
 *
 * @param state Scroll state holder used to control or observe scroll offset.
 *               Obtain via [rememberBColumnState].
 *
 * @param verticalArrangement Defines the vertical layout of children inside the column.
 *                            Defaults to [Arrangement.Top].
 *
 * @param horizontalAlignment Defines the horizontal alignment of children.
 *                            Defaults to [Alignment.Start].
 *
 * @param scrollEnabled Enables or disables vertical scrolling.
 *                      If `false`, the column acts as a static layout container.
 *
 * @param flingOverscrollEnabled Enables bounce/stretch animation when the user
 *                               drags beyond scroll limits. Automatically enabled
 *                               if [onOverscrollActivated] is provided.
 *
 * @param onOverscrollActivated Optional callback triggered once per gesture when
 *                              the user overscrolls the top or bottom edge.
 *                              Receives an [OverscrollEdge] (`Start` or `End`).
 *
 * @param onVisibleRangeChanged Optional callback that reports:
 *                              - `firstVisible`: index of first item meeting the
 *                                [visibleThreshold] condition (closest to top).
 *                              - `lastVisible`: index of last visible item (closest to bottom).
 *                              - `progress01`: scroll progress in the range `[0f..1f]`.
 *                              This logic is disabled when `null` to avoid recompositions.
 *
 * @param visibleThreshold A fractional threshold `[0f..1f]` determining the
 *                         minimum visible ratio for an item to be considered "visible".
 *                         For example:
 *                         - `1f` → fully visible only.
 *                         - `0.5f` → at least 50% visible.
 *                         - `0f` → any intersection counts.
 *
 * @param autoDividerEnabled If `true`, automatically inserts a divider composable
 *                           between items when using [BColumnItems].
 *
 * @param divider Composable defining how dividers are drawn when
 *                [autoDividerEnabled] is enabled. Defaults to a 1 dp line using
 *                `BTokens.colorScheme.outlineVariant`.
 *
 * @param content Column content scope, same as [ColumnScope]. Use
 *                [bColumnItem] to mark measurable child elements for visibility
 *                tracking or use [BColumnItems] to simplify divider insertion.
 *
 * ---
 *
 * ## Notes
 * - Visibility tracking and overscroll are opt-in; they remain inactive when
 *   their corresponding callbacks are `null` for optimal performance.
 * - The internal system uses `LayoutCoordinates.boundsInRoot()` to compute
 *   visible ratios accurately, even under nested scroll or graphicsLayer transforms.
 */
@Composable
fun BColumn(
    modifier: Modifier = Modifier,
    state: BColumnState = rememberBColumnState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollEnabled: Boolean = true,
    flingOverscrollEnabled: Boolean = false,
    onOverscrollActivated: ((edge: OverscrollEdge) -> Unit)? = null,
    onVisibleRangeChanged:
        ((firstVisible: Int?, lastVisible: Int?, progress01: Float) -> Unit)? = null,
    visibleThreshold: Float = 0.5f,
    autoDividerEnabled: Boolean = false,
    divider: @Composable () -> Unit = {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = BTokens.colorScheme.outlineVariant,
            thickness = 1.dp
        )
    },
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()

    var viewportHeightPx by remember { mutableStateOf(0f) }
    val viewportCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }

    val overscrollAnim = remember { Animatable(0f) }
    val onEdgeLatest by rememberUpdatedState(onOverscrollActivated)

    var overscrollActive by remember { mutableStateOf(false) }
    var overscrollEdge by remember { mutableStateOf<OverscrollEdge?>(null) }

    val overscrollEnabled =
        scrollEnabled && (flingOverscrollEnabled || onOverscrollActivated != null)

    val connection = remember(overscrollEnabled, state, viewportHeightPx) {
        if (!overscrollEnabled) {
            object : NestedScrollConnection {}
        } else {
            object : NestedScrollConnection {
                private fun maxStretch() = max(1f, viewportHeightPx * 0.25f)

                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (source == NestedScrollSource.Drag && available.y != 0f) {
                        val atTop = state.scrollState.value == 0
                        val atEnd = state.scrollState.value == state.scrollState.maxValue
                        val dy = available.y
                        val pullTop = dy > 0 && atTop
                        val pullEnd = dy < 0 && atEnd
                        if (pullTop || pullEnd) {
                            scope.launch {
                                overscrollAnim.snapTo(
                                    (overscrollAnim.value + dy * 0.5f)
                                        .coerceIn(-maxStretch(), maxStretch())
                                )
                            }
                            if (!overscrollActive) {
                                overscrollActive = true
                                overscrollEdge =
                                    if (dy > 0) OverscrollEdge.Start else OverscrollEdge.End
                                onEdgeLatest?.invoke(overscrollEdge!!)
                            }
                            return Offset(0f, dy)
                        }
                    }
                    return Offset.Zero
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    if (source == NestedScrollSource.Drag && available.y != 0f) {
                        scope.launch {
                            overscrollAnim.snapTo(
                                (overscrollAnim.value + available.y * 0.5f)
                                    .coerceIn(-maxStretch(), maxStretch())
                            )
                        }
                    }
                    return Offset.Zero
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    overscrollActive = false
                    overscrollEdge = null
                    overscrollAnim.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                    return Velocity.Zero
                }
            }
        }
    }

    val trackEnabled = onVisibleRangeChanged != null
    val itemBoxes = remember(trackEnabled) {
        if (trackEnabled) mutableStateMapOf<Int, ItemBox>() else mutableStateMapOf()
    }

    val snapshot by remember(trackEnabled, state.scrollState, viewportHeightPx) {
        derivedStateOf {
            if (!trackEnabled || viewportHeightPx <= 0f) {
                Triple<Int?, Int?, Float>(null, null, 0f)
            } else {
                val qualified = itemBoxes.asSequence()
                    .map { (idx, b) ->
                        val visibleH = (b.visibleBottomRel - b.visibleTopRel).coerceAtLeast(0f)
                        val frac = if (b.fullHeight > 0f) visibleH / b.fullHeight else 0f
                        Triple(idx, b, frac)
                    }
                    .filter { it.third + 1e-4f >= visibleThreshold }
                    .toList()

                val first = qualified.minByOrNull { it.second.visibleTopRel }?.first
                val last = qualified.maxByOrNull { it.second.visibleBottomRel }?.first

                val maxScroll = state.scrollState.maxValue.takeIf { it > 0 } ?: 1
                val progress = (state.scrollState.value.toFloat() / maxScroll).coerceIn(0f, 1f)

                Triple(first, last, progress)
            }
        }
    }

    val onRangeLatest by rememberUpdatedState(onVisibleRangeChanged)
    LaunchedEffect(snapshot) {
        onRangeLatest?.invoke(
            snapshot.first,
            snapshot.second,
            snapshot.third
        )
    }

    val visibleCtx = remember(trackEnabled) {
        if (trackEnabled) VisibleTrackContext(true, viewportCoords, itemBoxes) else null
    }

    CompositionLocalProvider(LocalVisibleTrack provides visibleCtx) {
        Column(
            modifier = modifier
                .onGloballyPositioned { coords ->
                    viewportCoords.value = coords
                    viewportHeightPx = coords.size.height.toFloat()
                }
                .then(if (overscrollEnabled) Modifier.nestedScroll(connection) else Modifier)
                .clipToBounds()
                .graphicsLayer { translationY = overscrollAnim.value }
                .then(if (scrollEnabled) Modifier.verticalScroll(state.scrollState) else Modifier),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            val localAutoDivider = autoDividerEnabled
            val localDivider = divider

            @Composable
            fun ColumnScope.BColumnItems(
                count: Int,
                item: @Composable (Int) -> Unit
            ) {
                if (!localAutoDivider) {
                    repeat(count) { i -> item(i) }
                } else {
                    repeat(count) { i ->
                        item(i)
                        if (i != count - 1) localDivider()
                    }
                }
            }

            val BColumnItemsRef: @Composable ColumnScope.(Int, @Composable (Int) -> Unit) -> Unit =
                { count, item -> BColumnItems(count, item) }

            with(object {
                @Composable
                fun ColumnScope.BColumnItems(count: Int, item: @Composable (Int) -> Unit) =
                    BColumnItemsRef(this, count, item)
            }) {
                content()
            }
        }
    }
}
