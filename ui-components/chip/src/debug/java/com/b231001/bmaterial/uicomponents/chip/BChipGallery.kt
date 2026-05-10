package com.b231001.bmaterial.uicomponents.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tag
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
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

@Preview(
    name = "FHD Portrait",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Preview
@Composable
fun BChipGallery() {
    BTheme {
        var s1 by remember { mutableStateOf(false) }
        var s2 by remember { mutableStateOf(true) }
        var s3 by remember { mutableStateOf(false) }
        var s4 by remember { mutableStateOf(true) }
        var s5 by remember { mutableStateOf(false) }
        var s6 by remember { mutableStateOf(false) }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BChip(
                    checked = s1,
                    onCheckedChange = { s1 = it },
                    style = BChipStyle.Outlined
                ) {
                    Text("Outlined", style = BTokens.typography.labelLarge)
                }
                BChip(
                    checked = s2,
                    onCheckedChange = { s2 = it },
                    style = BChipStyle.Outlined
                ) {
                    Text("Outlined - on", style = BTokens.typography.labelLarge)
                }
                BChip(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false,
                    style = BChipStyle.Outlined
                ) {
                    Text("Outlined - disabled", style = BTokens.typography.labelLarge)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BChip(
                    checked = s3,
                    onCheckedChange = { s3 = it },
                    style = BChipStyle.Filled,
                    size = BChipSize.Sm
                ) {
                    Text("Filled", style = BTokens.typography.labelLarge)
                }
                BChip(
                    checked = s4,
                    onCheckedChange = { s4 = it },
                    style = BChipStyle.Tonal
                ) {
                    Text("Tonal - on", style = BTokens.typography.labelLarge)
                }
                BChip(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false,
                    style = BChipStyle.Tonal
                ) {
                    Text("Tonal - disabled", style = BTokens.typography.labelLarge)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BChip(
                    checked = s5,
                    onCheckedChange = { s5 = it },
                    style = BChipStyle.Elevated,
                    leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = null) },
                    onTrailingClick = { s5 = false }
                ) {
                    Text("Elevated + close", style = BTokens.typography.labelLarge)
                }

                BChip(
                    checked = s6,
                    onCheckedChange = { s6 = it },
                    style = BChipStyle.Destructive
                ) {
                    Text(
                        text = if (s6) "Danger - on" else "Danger",
                        style = BTokens.typography.labelLarge
                    )
                }
            }
        }
    }
}
