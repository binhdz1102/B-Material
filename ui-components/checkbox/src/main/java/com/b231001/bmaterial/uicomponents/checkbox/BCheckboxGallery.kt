package com.b231001.bmaterial.uicomponents.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@Preview(
    name = "FHD Portrait",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BCheckboxGallery() {
    BTheme {
        var c1 by remember { mutableStateOf(false) }
        var c2 by remember { mutableStateOf(true) }
        var c3 by remember { mutableStateOf(false) }
        var c4 by remember { mutableStateOf(true) }
        var c5 by remember { mutableStateOf(false) }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Primary / sizes")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BCheckbox(
                    checked = c1,
                    onCheckedChange = { c1 = it },
                    size = BCheckboxSize.Sm,
                    style = BCheckboxStyle.Primary
                )
                BCheckbox(
                    checked = c1,
                    onCheckedChange = { c1 = it },
                    size = BCheckboxSize.Md,
                    style = BCheckboxStyle.Primary
                )
                BCheckbox(
                    checked = c1,
                    onCheckedChange = { c1 = it },
                    size = BCheckboxSize.Lg,
                    style = BCheckboxStyle.Primary
                )
                Spacer(Modifier.width(8.dp)); Text(if (c1) "Checked" else "Unchecked")
            }

            Text("Semantic styles")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BCheckbox(
                    checked = c2,
                    onCheckedChange = { c2 = it },
                    style = BCheckboxStyle.Destructive
                )
                BCheckbox(
                    checked = c3,
                    onCheckedChange = { c3 = it },
                    style = BCheckboxStyle.Success
                )
                BCheckbox(
                    checked = c4,
                    onCheckedChange = { c4 = it },
                    style = BCheckboxStyle.Warning
                )
                BCheckbox(checked = c5, onCheckedChange = { c5 = it }, style = BCheckboxStyle.Info)
            }

            Text("Disabled states")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BCheckbox(
                    checked = true,
                    onCheckedChange = {},
                    enabled = false,
                    style = BCheckboxStyle.Primary
                )
                BCheckbox(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false,
                    style = BCheckboxStyle.Primary
                )
                BCheckbox(
                    checked = true,
                    onCheckedChange = {},
                    enabled = false,
                    style = BCheckboxStyle.Destructive
                )
                BCheckbox(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false,
                    style = BCheckboxStyle.Destructive
                )
            }
        }
    }
}
