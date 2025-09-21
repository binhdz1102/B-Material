package com.b231001.bmaterial.uicomponents.dialog

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens

@Composable
fun BDropdownMenu(
    state: BPopupState,
    anchorBounds: Rect?,
    items: List<String>,
    onClick: (Int) -> Unit
) {
    BPopup(
        state = state,
        onDismissRequest = { state.hide() },
        mode = BPopupMode.Anchored,
        placement = BPopupPlacement.Auto,
        style = BPopupDefaults.style("menu"),
        metrics = BPopupDefaults.metrics().copy(offset = DpOffset(0.dp, 4.dp)),
        anchorBoundsInWindow = anchorBounds
    ) {
        Column {
            items.forEachIndexed { i, label ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onClick(i); state.hide() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) { Text(label, style = BTokens.typography.bodyMedium) }
            }
        }
    }
}

