package com.b231001.bmaterial.uicomponents.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

@Preview(
    name = "FHD Portrait",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BButtonGallery() {
    BTheme {
        var state1 by remember { mutableStateOf(false) }
        var state2 by remember { mutableStateOf(false) }
        var state3 by remember { mutableStateOf(false) }
        var state4 by remember { mutableStateOf(false) }
        var state5 by remember { mutableStateOf(false) }
        var state6 by remember { mutableStateOf(false) }
        var state7 by remember { mutableStateOf(false) }
        var state8 by remember { mutableStateOf(false) }
        var state9 by remember { mutableStateOf(false) }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Style variants", style = BTokens.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state1 = !state1 },
                        selected = state1,
                        style = BButtonStyle.Filled
                    ) { Text("Filled") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Filled", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state2 = !state2 },
                        selected = state2,
                        style = BButtonStyle.Tonal
                    ) { Text("Tonal") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tonal", style = BTokens.typography.bodySmall)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state3 = !state3 },
                        selected = state3,
                        style = BButtonStyle.Outlined
                    ) { Text("Outlined") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Outlined", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state4 = !state4 },
                        selected = state4,
                        style = BButtonStyle.Text
                    ) { Text("Text") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Text", style = BTokens.typography.bodySmall)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state5 = !state5 },
                        selected = state5,
                        style = BButtonStyle.Elevated
                    ) { Text("Elevated") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Elevated", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(200.dp),
                        onClick = { state6 = !state6 },
                        selected = state6,
                        style = BButtonStyle.Destructive,
                        leadingIcon = {
                            Icon(Icons.Default.Favorite, contentDescription = null)
                        }
                    ) { Text("Destructive") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Destructive", style = BTokens.typography.bodySmall)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state7 = !state7 },
                        selected = state7,
                        style = BButtonStyle.Success
                    ) { Text("Success") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Success", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state8 = !state8 },
                        selected = state8,
                        style = BButtonStyle.Warning
                    ) { Text("Warning") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Warning", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BButton(
                        modifier = Modifier.width(100.dp),
                        onClick = { state9 = !state9 },
                        selected = state9,
                        style = BButtonStyle.Info,
                        loading = true
                    ) { Text("Info") }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Info (Loading)", style = BTokens.typography.bodySmall)
                }
            }
        }
    }
}
