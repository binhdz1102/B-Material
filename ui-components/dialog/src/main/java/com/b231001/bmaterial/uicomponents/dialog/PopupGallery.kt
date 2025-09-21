package com.b231001.bmaterial.uicomponents.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.DpOffset
import com.b231001.bmaterial.uicomponents.button.BButton
import com.b231001.bmaterial.uicomponents.button.BButtonSize
import com.b231001.bmaterial.uicomponents.button.BButtonStyle
import com.b231001.bmaterial.uicore.tokens.BTokens

@Composable
fun PopupGallery() {
    // In preview show logs only when not in inspection to avoid spam
    if (!LocalInspectionMode.current) println("PopupGallery()")

    BTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionTitle("1) Anchored Tooltip + Dropdown")
            AnchoredTooltipAndMenuDemo()

            SectionTitle("2) Context Menu (icon + groups)")
            ContextMenuDemo()

            SectionTitle("3) Placement matrix (Top/Bottom/Start/End)")
            PlacementMatrixDemo()

            SectionTitle("4) Centered / Modal Action Sheet")
            CenteredModalDemo()
        }
    }
}

/* ------------------------------ Sections ------------------------------ */

@Composable
private fun AnchoredTooltipAndMenuDemo() {
    val (anchorMod, anchorRect) = rememberAnchorBounds()
    val tip = remember { BPopupState(false) }
    val menu = remember { BPopupState(false) }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        BButton(
            modifier = anchorMod,
            onClick = { menu.toggle() },
            style = BButtonStyle.Filled
        ) { Text("Open menu") }

        // Gợi ý: bạn tự bật tooltip theo hover/focus/long-press tuỳ app
        BButton(onClick = { tip.toggle() }, style = BButtonStyle.Outlined) {
            Text("Show tooltip")
        }

        // Tooltip (anchored)
        BTooltip(
            state = tip,
            anchorBounds = anchorRect.value,
            text = "Nhấn để mở menu"
        )

        // Dropdown (anchored)
        BDropdownMenu(
            state = menu,
            anchorBounds = anchorRect.value,
            items = listOf("Edit", "Duplicate", "Delete")
        ) { /* handle click */ }
    }
}

@Composable
private fun ContextMenuDemo() {
    val (anchorMod, anchorRect) = rememberAnchorBounds()
    val ctx = remember { BPopupState(false) }

    Surface(
        shape = BTokens.shapes.large,
        border = BorderStroke(1.dp, BTokens.colorScheme.outlineVariant),
        color = BTokens.colorScheme.surface1
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .then(anchorMod),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("File.kt", style = BTokens.typography.titleSmall, modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.MoreVert, contentDescription = "More",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { ctx.toggle() }
            )
        }
    }

    // Context menu = dropdown có icon + nhóm + divider (demo tối giản)
    BPopup(
        state = ctx,
        onDismissRequest = { ctx.hide() },
        mode = BPopupMode.Anchored,
        placement = BPopupPlacement.BottomEnd,
        style = BPopupDefaults.style("menu"),
        metrics = BPopupDefaults.metrics().copy(offset = DpOffset(0.dp, 6.dp)),
        anchorBoundsInWindow = anchorRect.value
    ) {
        Column(Modifier.widthIn(max = 280.dp)) {
            MenuRow("Open")
            MenuRow("Rename")
            DividerRow()
            MenuRow("Copy path")
            MenuRow("Delete", danger = true)
        }
    }
}

@Composable
private fun PlacementMatrixDemo() {
    val (anchorMod, anchorRect) = rememberAnchorBounds()
    val popup = remember { BPopupState(false) }
    var placement by remember { mutableStateOf(BPopupPlacement.Bottom) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PlacementChip("TopStart") { placement = BPopupPlacement.TopStart; popup.show() }
            PlacementChip("Top") { placement = BPopupPlacement.Top; popup.show() }
            PlacementChip("TopEnd") { placement = BPopupPlacement.TopEnd; popup.show() }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PlacementChip("Start") { placement = BPopupPlacement.Start; popup.show() }
            BButton(
                modifier = anchorMod,
                onClick = { placement = BPopupPlacement.Auto; popup.show() },
                style = BButtonStyle.Tonal,
                size = BButtonSize.Sm
            ) { Text("Anchor") }
            PlacementChip("End") { placement = BPopupPlacement.End; popup.show() }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PlacementChip("BottomStart") { placement = BPopupPlacement.BottomStart; popup.show() }
            PlacementChip("Bottom") { placement = BPopupPlacement.Bottom; popup.show() }
            PlacementChip("BottomEnd") { placement = BPopupPlacement.BottomEnd; popup.show() }
        }
    }

    BPopup(
        state = popup,
        onDismissRequest = { popup.hide() },
        mode = BPopupMode.Anchored,
        placement = placement,
        style = BPopupDefaults.style("menu"),
        metrics = BPopupDefaults.metrics().copy(offset = DpOffset(0.dp, 4.dp)),
        anchorBoundsInWindow = anchorRect.value
    ) {
        Text(
            "Placement: $placement",
            style = BTokens.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun CenteredModalDemo() {
    val modal = remember { BPopupState(false) }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BButton(onClick = { modal.show() }, style = BButtonStyle.Filled) { Text("Open sheet") }
        BButton(onClick = { /* no-op */ }, style = BButtonStyle.Outlined) { Text("Another action") }
    }

    // Centered / Modal-like action sheet
    BPopup(
        state = modal,
        onDismissRequest = { modal.hide() },
        mode = BPopupMode.Centered,
        style = BPopupDefaults.style().copy(
            elevation = 6.dp,
            shape = BTokens.shapes.large,
            scrimColor = BTokens.colorScheme.onSurface.copy(alpha = 0.32f)
        ),
        metrics = BPopupMetrics(
            maxWidth = 560.dp,
            focusTrap = true
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Xác nhận hành động", style = BTokens.typography.titleLarge)
            Text("Bạn có chắc muốn tiếp tục?", style = BTokens.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BButton(onClick = { modal.hide() }, style = BButtonStyle.Outlined) { Text("Huỷ") }
                BButton(onClick = { modal.hide() }, style = BButtonStyle.Filled) { Text("Đồng ý") }
            }
        }
    }
}

/* ------------------------------ Little atoms ------------------------------ */

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = BTokens.typography.headlineSmall)
}

@Composable
private fun MenuRow(label: String, danger: Boolean = false) {
    val fg = if (danger) BTokens.colorScheme.onError else BTokens.colorScheme.onSurface
    val bg = if (danger) BTokens.colorScheme.error.copy(alpha = 0.08f) else BTokens.colorScheme.surface
    Row(
        Modifier
            .fillMaxWidth()
            .background(bg, RectangleShape)
            .clickable { /* handle */ }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = BTokens.typography.bodyMedium, color = fg)
    }
}

@Composable
private fun DividerRow() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BTokens.colorScheme.outlineVariant)
    )
}

@Composable
private fun PlacementChip(text: String, onClick: () -> Unit) {
    BButton(onClick = onClick, style = BButtonStyle.Outlined, size = BButtonSize.Sm) { Text(text) }
}
