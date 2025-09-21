package com.b231001.bmaterial.uicomponents.dialog

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens

@Composable
fun BTooltip(
    state: BPopupState,
    anchorBounds: Rect?,
    text: String
) {
    BPopup(
        state = state,
        onDismissRequest = { state.hide() },
        mode = BPopupMode.Anchored,
        placement = BPopupPlacement.Auto,
        style = BPopupDefaults.style("tooltip").copy(border = null, elevation = 2.dp),
        metrics = BPopupDefaults.metrics("tooltip"),
        anchorBoundsInWindow = anchorBounds
    ) {
        Text(text, style = BTokens.typography.labelMedium, modifier = Modifier.padding(8.dp))
    }
}

