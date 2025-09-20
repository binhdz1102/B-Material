package com.b231001.bmaterial.uicomponents.bswitch

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
fun BSwitchGallery() {
    BTheme {
        var a by remember { mutableStateOf(true) }
        var b by remember { mutableStateOf(true) }
        var c by remember { mutableStateOf(true) }
        var d by remember { mutableStateOf(true) }
        var e by remember { mutableStateOf(true) }
        var f by remember { mutableStateOf(true) }
        var g by remember { mutableStateOf(true) }
        var h by remember { mutableStateOf(true) }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Size variants
            Text("Sizes: Sm / Md / Lg", style = BTokens.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(checked = a, onCheckedChange = { a = it }, size = BSwitchSize.Sm)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Size = Sm", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(checked = b, onCheckedChange = { b = it }, size = BSwitchSize.Md)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Size = Md", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(checked = c, onCheckedChange = { c = it }, size = BSwitchSize.Lg)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Size = Lg", style = BTokens.typography.bodySmall)
                }
            }

            // Style variants
            Text(
                "Styles: Primary / Success / Warning / Info / Destructive",
                style = BTokens.typography.titleSmall
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(checked = d, onCheckedChange = { d = it }, style = BSwitchStyle.Primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Style = Primary", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(checked = e, onCheckedChange = { e = it }, style = BSwitchStyle.Success)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Style = Success", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(checked = f, onCheckedChange = { f = it }, style = BSwitchStyle.Warning)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Style = Warning", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(checked = g, onCheckedChange = { g = it }, style = BSwitchStyle.Info)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Style = Info", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(
                        checked = h,
                        onCheckedChange = { h = it },
                        style = BSwitchStyle.Destructive
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Style = Destructive", style = BTokens.typography.bodySmall)
                }
            }

            // Disabled states
            Text("Disabled states", style = BTokens.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(
                        checked = false,
                        enabled = false,
                        onCheckedChange = {},
                        style = BSwitchStyle.Primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("OFF / Disabled", style = BTokens.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BSwitch(
                        checked = true,
                        enabled = false,
                        onCheckedChange = {},
                        style = BSwitchStyle.Success
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("ON / Disabled", style = BTokens.typography.bodySmall)
                }
            }
        }
    }
}
