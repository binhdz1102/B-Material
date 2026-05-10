package com.b231001.bmaterial.uicomponents.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTheme

@Preview(
    name = "IconButton Gallery",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BIconButtonGallery() {
    BTheme {
        var a by remember { mutableStateOf(false) }
        var b by remember { mutableStateOf(true) }
        var c by remember { mutableStateOf(false) }
        var d by remember { mutableStateOf(true) }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BIconButton(a, { a = it }, style = BIconButtonStyle.Filled) {
                    Icon(Icons.Default.Favorite, null)
                }
                BIconButton(b, { b = it }, style = BIconButtonStyle.Tonal) {
                    Icon(Icons.Default.Bookmark, null)
                }
                BIconButton(false, {}, enabled = false, style = BIconButtonStyle.Outlined) {
                    Icon(Icons.Default.FavoriteBorder, null)
                }
                BIconButton(true, {}, enabled = false, style = BIconButtonStyle.Text) {
                    Icon(Icons.Default.Star, null)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BIconButton(c, { c = it }, style = BIconButtonStyle.Destructive) {
                    Icon(Icons.Default.Delete, null)
                }
                BIconButton(d, { d = it }, style = BIconButtonStyle.Success) {
                    Icon(Icons.Default.Check, null)
                }
                BIconButton(false, { d = it }, style = BIconButtonStyle.Warning) {
                    Icon(Icons.Default.Warning, null)
                }
                BIconButton(true, { d = it }, style = BIconButtonStyle.Info) {
                    Icon(Icons.Default.Info, null)
                }
            }
        }
    }
}
