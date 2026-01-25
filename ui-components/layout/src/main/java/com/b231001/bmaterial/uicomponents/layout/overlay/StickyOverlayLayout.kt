package com.b231001.bmaterial.uicomponents.layout.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class StickySide { Top, Bottom, Left, Right }

/** Cross-axis alignment relative to anchor on the perpendicular axis */
enum class StickyCrossAlign { Start, Center, End }

enum class OutsideTapBehavior {
    /** Tap outside => dismiss (modal-ish) */
    Dismiss,

    /** Tap outside => pass-through to underlying UI */
    PassThrough
}

data class StickyPlacement(
    val chosenSide: StickySide,
    val anchorBoundsInWindow: IntRect,
    val popupBoundsInWindow: IntRect,
    val windowSize: IntSize
)

@Stable
class StickyOverlayState internal constructor() {
    // anchor bounds in window coordinates
    var anchorBoundsInWindow: IntRect? by mutableStateOf(null)
        private set

    // Expose placement info for debugging / analytics / UI hints
    private val _placement = MutableStateFlow<StickyPlacement?>(null)
    val placementFlow = _placement.asStateFlow()

    internal fun updateAnchor(coords: LayoutCoordinates) {
        if (!coords.isAttached) {
            anchorBoundsInWindow = null
            return
        }
        val r = coords.boundsInWindow()
        anchorBoundsInWindow = r.toIntRect()
    }

    internal fun reportPlacement(p: StickyPlacement) {
        // avoid noisy emissions
        val old = _placement.value
        if (old != p) _placement.tryEmit(p)
    }
}

@Composable
fun rememberStickyOverlayState(): StickyOverlayState = remember { StickyOverlayState() }

/**
 * Mark any composable as an anchor for StickyOverlayLayout.
 */
fun Modifier.stickyOverlayAnchor(state: StickyOverlayState): Modifier = this.then(
    Modifier.onGloballyPositioned { coords -> state.updateAnchor(coords) }
)

/**
 * StickyOverlayLayout: a Popup that chooses the best side around anchor to stay within screen.
 */
@Composable
fun StickyOverlayLayout(
    shown: Boolean,
    anchorState: StickyOverlayState,
    modifier: Modifier = Modifier,
    preferredSides: List<StickySide> = listOf(
        StickySide.Bottom,
        StickySide.Top,
        StickySide.Right,
        StickySide.Left
    ),
    crossAxisAlign: StickyCrossAlign = StickyCrossAlign.Center,
    margin: Dp = 8.dp,
    outsideTapBehavior: OutsideTapBehavior = OutsideTapBehavior.Dismiss,
    onDismissRequest: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val anchorRect = anchorState.anchorBoundsInWindow
    if (!shown || anchorRect == null) return

    val density = LocalDensity.current
    val marginPx = with(density) { margin.roundToPx() }

    // For older Compose versions, recreating the provider when anchorRect
    // changes forces reposition.
    // Newer Compose also auto-updates position when state read during calculatePosition changes.
    val provider = remember(anchorRect, preferredSides, crossAxisAlign, marginPx) {
        StickyOverlayPositionProvider(
            anchorBoundsInWindow = anchorRect,
            preferredSides = preferredSides,
            crossAxisAlign = crossAxisAlign,
            marginPx = marginPx,
            reportPlacement = { anchorState.reportPlacement(it) }
        )
    }

    val props = when (outsideTapBehavior) {
        OutsideTapBehavior.Dismiss -> PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
        OutsideTapBehavior.PassThrough -> PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    }

    Popup(
        popupPositionProvider = provider,
        properties = props,
        onDismissRequest = {
            // Only meaningful when focusable=true (Dismiss mode).
            if (outsideTapBehavior == OutsideTapBehavior.Dismiss) onDismissRequest()
        }
    ) {
        Box(modifier = modifier, content = content)
    }
}

// Position Provider
private class StickyOverlayPositionProvider(
    private val anchorBoundsInWindow: IntRect,
    private val preferredSides: List<StickySide>,
    private val crossAxisAlign: StickyCrossAlign,
    private val marginPx: Int,
    private val reportPlacement: (StickyPlacement) -> Unit
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // IMPORTANT: we ignore anchorBounds from Popup and use the real
        // anchorBoundsInWindow we captured.
        // PopupPositionProvider API provides windowSize + popupContentSize to compute position.
        val a = anchorBoundsInWindow
        val pw = popupContentSize.width
        val ph = popupContentSize.height

        val usableLeft = marginPx
        val usableTop = marginPx
        val usableRight = windowSize.width - marginPx
        val usableBottom = windowSize.height - marginPx

        fun fitsFully(x: Int, y: Int): Boolean {
            return x >= usableLeft &&
                y >= usableTop &&
                x + pw <= usableRight &&
                y + ph <= usableBottom
        }

        fun clampToWindow(x: Int, y: Int): Pair<Int, Int> {
            val maxX = max(usableLeft, usableRight - pw)
            val maxY = max(usableTop, usableBottom - ph)
            return x.coerceIn(usableLeft, maxX) to y.coerceIn(usableTop, maxY)
        }

        fun intersectionArea(x: Int, y: Int): Long {
            val left = max(usableLeft, x)
            val top = max(usableTop, y)
            val right = min(usableRight, x + pw)
            val bottom = min(usableBottom, y + ph)
            val w = max(0, right - left)
            val h = max(0, bottom - top)
            return (w.toLong() * h.toLong())
        }

        fun crossAlignedXForTopBottom(): Int = when (crossAxisAlign) {
            StickyCrossAlign.Start -> a.left
            StickyCrossAlign.Center -> (a.left + a.right) / 2 - pw / 2
            StickyCrossAlign.End -> a.right - pw
        }

        fun crossAlignedYForLeftRight(): Int = when (crossAxisAlign) {
            StickyCrossAlign.Start -> a.top
            StickyCrossAlign.Center -> (a.top + a.bottom) / 2 - ph / 2
            StickyCrossAlign.End -> a.bottom - ph
        }

        data class Candidate(val side: StickySide, val x: Int, val y: Int)

        val candidates = preferredSides.map { side ->
            when (side) {
                StickySide.Top -> Candidate(
                    side,
                    x = crossAlignedXForTopBottom(),
                    y = a.top - ph
                )
                StickySide.Bottom -> Candidate(
                    side,
                    x = crossAlignedXForTopBottom(),
                    y = a.bottom
                )
                StickySide.Left -> Candidate(
                    side,
                    x = a.left - pw,
                    y = crossAlignedYForLeftRight()
                )
                StickySide.Right -> Candidate(
                    side,
                    x = a.right,
                    y = crossAlignedYForLeftRight()
                )
            }
        }

        // 1) Pick the first that fully fits
        val firstFit = candidates.firstOrNull { fitsFully(it.x, it.y) }
        val chosen = if (firstFit != null) {
            val (cx, cy) = clampToWindow(firstFit.x, firstFit.y)
            firstFit.copy(x = cx, y = cy)
        } else {
            // 2) Otherwise pick the one with max visible intersection area, then clamp.
            val best = candidates.maxByOrNull { intersectionArea(it.x, it.y) } ?: candidates.first()
            val (cx, cy) = clampToWindow(best.x, best.y)
            best.copy(x = cx, y = cy)
        }

        val popupRect = IntRect(chosen.x, chosen.y, chosen.x + pw, chosen.y + ph)
        reportPlacement(
            StickyPlacement(
                chosenSide = chosen.side,
                anchorBoundsInWindow = a,
                popupBoundsInWindow = popupRect,
                windowSize = windowSize
            )
        )
        return IntOffset(chosen.x, chosen.y)
    }
}

private fun Rect.toIntRect(): IntRect = IntRect(
    left = left.roundToInt(),
    top = top.roundToInt(),
    right = right.roundToInt(),
    bottom = bottom.roundToInt()
)
