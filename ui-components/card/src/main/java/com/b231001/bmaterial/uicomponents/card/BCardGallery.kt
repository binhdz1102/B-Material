package com.b231001.bmaterial.uicomponents.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.button.BButton
import com.b231001.bmaterial.uicomponents.button.BButtonStyle
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

@Preview(
    name = "BCard Gallery",
    showBackground = true,
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BCardGallery() {
    BTheme {
        Column(
            Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filled
            BCard(
                style = BCardStyle.Filled,
                header = {
                    Text("Filled card", color = LocalContentColor.current)
                },
                media = {
                    // Demo media box
                    Box(
                        Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                            .background(BTokens.colorScheme.primary.copy(alpha = 0.12f))
                    )
                },
                content = {
                    Text("Nội dung mô tả ngắn gọn. Có thể là 2–3 dòng.")
                },
                actions = {
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Text
                    ) { Text("DETAILS") }
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Filled
                    ) { Text("ACTION") }
                }
            )

            // Elevated + clickable
            var selected by remember { mutableStateOf(false) }
            BCard(
                style = BCardStyle.Elevated,
                onClick = { selected = !selected },
                selected = selected,
                header = { Text(if (selected) "Selected" else "Tap to select") },
                content = { Text("Elevated card nhấn mạnh bằng tonal elevation.") },
                actions = {
                    BButton(onClick = {}, style = BButtonStyle.Tonal) { Text("Secondary") }
                }
            )

            // Outlined
            BCard(
                style = BCardStyle.Outlined,
                header = { Text("Outlined card") },
                content = { Text("Viền outline, nền trong suốt/tonal rất nhẹ.") },
                actions = {
                    BButton(onClick = {}, style = BButtonStyle.Text) { Text("DISMISS") }
                }
            )

            // Semantic color cards
            BCard(
                style = BCardStyle.Success,
                header = { Text("Success") },
                content = { Text("Mọi thứ đều ổn.") }
            )
            BCard(
                style = BCardStyle.Warning,
                header = { Text("Warning") },
                content = { Text("Cần chú ý hành động tiếp theo.") }
            )

            BCard(
                style = BCardStyle.Info,
                header = { Text("Info") },
                content = { Text("Thông tin bổ sung cho người dùng.") }
            )
            BCard(
                style = BCardStyle.Destructive,
                header = { Text("Destructive") },
                content = { Text("Hành động không thể hoàn tác.") },
                actions = {
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Outlined
                    ) { Text("CANCEL") }
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Destructive
                    ) { Text("DELETE") }
                }
            )

            // Disabled case
            val disabledColors = BCardDefaults.colors(
                style = BCardStyle.Filled,
                enabled = false
            )
            BCard(
                style = BCardStyle.Filled,
                colors = disabledColors,
                header = { Text("Disabled card") },
                media = {
                    Box(
                        Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .background(BTokens.colorScheme.onSurface.copy(alpha = 0.06f))
                    )
                },
                content = {
                    Text("Card bị vô hiệu hóa: hiển thị mờ đi, không thể bấm.")
                },
                actions = {
                    BButton(
                        modifier = Modifier.width(140.dp),
                        onClick = {},
                        enabled = false,
                        style = BButtonStyle.Text
                    ) { Text("DETAILS") }
                    BButton(
                        modifier = Modifier.width(140.dp),
                        onClick = {},
                        enabled = false,
                        style = BButtonStyle.Filled
                    ) { Text("ACTION") }
                }
            )
        }
    }
}
