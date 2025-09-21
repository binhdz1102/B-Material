package com.b231001.bmaterial.uicomponents.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens

data class BPopupStyle(
    val containerColor: Color,
    val contentColor: Color,
    val elevation: Dp,
    val shape: Shape,
    val border: BorderStroke? = null,
    val scrimColor: Color? = null,
    val padding: PaddingValues = PaddingValues(0.dp)
)

data class BPopupMetrics(
    val minWidth: Dp = Dp.Unspecified,
    val maxWidth: Dp = 360.dp,
    val minHeight: Dp = Dp.Unspecified,
    val maxHeight: Dp = Dp.Unspecified,
    val offset: DpOffset = DpOffset(0.dp, 0.dp),
    val focusTrap: Boolean = false
)

enum class BPopupMode { Anchored, Centered }
enum class BPopupPlacement {
    TopStart, Top, TopEnd, BottomStart, Bottom, BottomEnd, Start, End, Auto
}
enum class BPopupDismissPolicy { OnClickOutside, OnBackPress, OnLoseFocus, None }

object BPopupDefaults {
    @Composable
    fun style(kind: String = "surface", tonal: Boolean = false): BPopupStyle {
        val cs = BTokens.colorScheme
        val sh = BTokens.shapes
        val (bg, fg) = when (kind) {
            "menu" -> cs.surface1 to cs.onSurface
            "tooltip" -> cs.onSurface to cs.surface
            "danger" -> cs.error to cs.onError
            else -> cs.surface to cs.onSurface
        }
        return BPopupStyle(
            containerColor = bg,
            contentColor = fg,
            elevation = if (tonal) 2.dp else 4.dp,
            shape = sh.medium,
            border = BorderStroke(1.dp, cs.outlineVariant),
            scrimColor = null
        )
    }

    @Composable
    fun metrics(kind: String = "menu"): BPopupMetrics = when (kind) {
        "tooltip" -> BPopupMetrics(maxWidth = 280.dp)
        "sheet" -> BPopupMetrics(maxWidth = Dp.Unspecified, focusTrap = true)
        else -> BPopupMetrics()
    }
}
