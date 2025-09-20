package com.b231001.bmaterial.uicomponents.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.bswitch.BSwitch
import com.b231001.bmaterial.uicomponents.bswitch.BSwitchSize
import com.b231001.bmaterial.uicomponents.bswitch.BSwitchStyle
import com.b231001.bmaterial.uicomponents.button.BButton
import com.b231001.bmaterial.uicomponents.button.BButtonSize
import com.b231001.bmaterial.uicomponents.button.BButtonStyle
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

@Composable
fun BListItemGallery() {
    BTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // 1 dòng, có leading icon, trailing switch, meta text
            var wifi by remember { mutableStateOf(true) }
            BListItem(
                style = BListItemStyle.Default,
                size = BListItemSize.Default,
                leading = { Icon(Icons.Default.Wifi, null) },
                headline = "Wi-Fi",
                metaText = if (wifi) "On" else "Off",
                trailing = {
                    BSwitch(
                        checked = wifi,
                        onCheckedChange = { wifi = it },
                        style = BSwitchStyle.Primary,
                        size = BSwitchSize.Sm
                    )
                },
                onClick = { wifi = !wifi }
            )

            // 2 dòng (headline + supporting), avatar leading, trailing action
            BListItem(
                style = BListItemStyle.Elevated,
                size = BListItemSize.Large,
                leading = {
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(BTokens.colorScheme.primary, CircleShape)
                    )
                },
                overline = "NEW MESSAGE",
                headline = "B-Material design review",
                supporting = "Tomorrow 9:00–10:00 — Room 2A",
                metaText = "09:41",
                trailing = {
                    BButton(
                        onClick = { /* join */ },
                        style = BButtonStyle.Text,
                        size = BButtonSize.Sm
                    ) { Text("JOIN") }
                },
                onClick = { /* open */ },
                onLongClick = { /* show menu */ }
            )

            // ListItem “destructive” kiểu cảnh báo
            BListItem(
                style = BListItemStyle.Destructive,
                size = BListItemSize.Default,
                leading = { Icon(Icons.Default.Delete, null) },
                headline = "Clear cache",
                supporting = "Remove temporary data to free up space",
                trailing = { Icon(Icons.Default.ChevronRight, null) },
                onClick = { /* navigate */ }
            )

            // Disabled
            BListItem(
                enabled = false,
                style = BListItemStyle.Tonal,
                leading = { Icon(Icons.Default.BluetoothDisabled, null) },
                headline = "Bluetooth",
                supporting = "Not available on this device",
                trailing = { Icon(Icons.Default.ChevronRight, null) }
            )
        }
    }
}
