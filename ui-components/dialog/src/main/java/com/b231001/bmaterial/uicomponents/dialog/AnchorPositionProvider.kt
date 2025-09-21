package com.b231001.bmaterial.uicomponents.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider


@Composable
fun rememberAnchorBounds(): Pair<Modifier, State<Rect?>> {
    val density = LocalDensity.current
    val rect = remember { mutableStateOf<Rect?>(null) }
    val mod = Modifier.onGloballyPositioned { c: LayoutCoordinates ->
        val pos = c.localToWindow(Offset.Zero)
        val size = c.size
        with(density) {
            rect.value = Rect(
                pos.x, pos.y, pos.x + size.width, pos.y + size.height
            )
        }
    }
    return mod to rect
}

class AnchorPositionProvider(
    private val placement: BPopupPlacement,
    private val density: Density,
    private val offset: DpOffset
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        fun base(p: BPopupPlacement): IntOffset {
            val x = when (p) {
                BPopupPlacement.Top, BPopupPlacement.Bottom -> anchorBounds.left
                BPopupPlacement.TopStart, BPopupPlacement.BottomStart, BPopupPlacement.Start -> anchorBounds.left
                BPopupPlacement.TopEnd, BPopupPlacement.BottomEnd, BPopupPlacement.End -> anchorBounds.right - popupContentSize.width
                else -> anchorBounds.left
            }
            val y = when (p) {
                BPopupPlacement.Top, BPopupPlacement.TopStart, BPopupPlacement.TopEnd -> anchorBounds.top - popupContentSize.height
                BPopupPlacement.Bottom, BPopupPlacement.BottomStart, BPopupPlacement.BottomEnd -> anchorBounds.bottom
                BPopupPlacement.Start, BPopupPlacement.End -> anchorBounds.top
                else -> anchorBounds.bottom
            }
            return IntOffset(x, y)
        }
        var pos = base(if (placement == BPopupPlacement.Auto) BPopupPlacement.BottomStart else placement)
        fun fits(i: IntOffset) =
            i.x >= 0 && i.y >= 0 &&
                i.x + popupContentSize.width <= windowSize.width &&
                i.y + popupContentSize.height <= windowSize.height

        if (!fits(pos)) {
            pos = base(
                when (placement) {
                    BPopupPlacement.Bottom, BPopupPlacement.BottomStart, BPopupPlacement.BottomEnd -> BPopupPlacement.Top
                    BPopupPlacement.Top, BPopupPlacement.TopStart, BPopupPlacement.TopEnd -> BPopupPlacement.Bottom
                    BPopupPlacement.Start -> BPopupPlacement.End
                    BPopupPlacement.End -> BPopupPlacement.Start
                    else -> BPopupPlacement.Top
                }
            )
        }
        with(density) {
            pos = pos.copy(
                x = pos.x + offset.x.roundToPx(),
                y = pos.y + offset.y.roundToPx()
            )
        }
        return pos
    }
}

