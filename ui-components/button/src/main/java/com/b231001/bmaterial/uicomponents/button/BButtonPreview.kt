package com.b231001.bmaterial.uicomponents.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(
    name = "FHD Portrait",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BButtonGallery() {
    var state1 by remember { mutableStateOf(true) }
    var state2 by remember { mutableStateOf(true) }
    var state3 by remember { mutableStateOf(true) }
    var state4 by remember { mutableStateOf(true) }
    var state5 by remember { mutableStateOf(true) }
    var state6 by remember { mutableStateOf(true) }
    var state7 by remember { mutableStateOf(true) }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state1 = !state1 },
//                selected = state1,
                style = BButtonStyle.Filled
            ) { Text("Filled") }
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state2 = !state2 },
//                selected = state2,
                style = BButtonStyle.Tonal
            ) { Text("Tonal") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state3 = !state3 },
//                selected = state3,
                style = BButtonStyle.Outlined
            ) { Text("Outlined") }
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state3 = !state3 },
//                selected = state3,
                style = BButtonStyle.Text
            ) { Text("Text") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state4 = !state4 },
//                selected = state4,
                style = BButtonStyle.Elevated
            ) { Text("Elevated") }
            BButton(
                modifier = Modifier.width(200.dp),
                onClick = { state5 = !state5 },
//                selected = state5,
                style = BButtonStyle.Destructive,
                leadingIcon = {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                }
            ) { Text("Destructive") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state6 = !state6 },
//                selected = state6,
                style = BButtonStyle.Success
            ) { Text("Success") }
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state7 = !state7 },
//                selected = state7,
                style = BButtonStyle.Warning
            ) { Text("Warning") }
            BButton(
                modifier = Modifier.width(100.dp),
                onClick = { state7 = !state7 },
//                selected = state7,
                style = BButtonStyle.Info,
                loading = true
            ) { Text("Info") }
        }
    }
}
