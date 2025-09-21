package com.b231001.bmaterial.uicomponents.dialog


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.button.BButton
import com.b231001.bmaterial.uicomponents.button.BButtonSize
import com.b231001.bmaterial.uicomponents.button.BButtonStyle
import com.b231001.bmaterial.uicore.tokens.BTokens

@Composable
fun OverlayRoot(onClose: (() -> Unit)? = null) {
    Surface(
        shape = BTokens.shapes.large,
        color = BTokens.colorScheme.surface1,
        border = BorderStroke(1.dp, BTokens.colorScheme.outlineVariant),
        tonalElevation = 4.dp, shadowElevation = 4.dp
    ) {
        Row(Modifier.padding(BTokens.paddings.small)) {
            Text("Quick Tools", style = BTokens.typography.labelLarge)
            Spacer(Modifier.width(8.dp))
            BButton(onClick = { /* do something */ }, style = BButtonStyle.Filled, size = BButtonSize.Sm) {
                Text("Open")
            }
            Spacer(Modifier.width(8.dp))
            if (onClose != null) {
                BButton(onClick = { onClose() }, style = BButtonStyle.Outlined, size = BButtonSize.Sm) {
                    Text("Close")
                }
            }
        }
    }
}

