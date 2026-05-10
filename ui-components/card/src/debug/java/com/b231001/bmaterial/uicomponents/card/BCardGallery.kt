package com.b231001.bmaterial.uicomponents.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filled
            BCard(
                style = BCardStyle.Filled,
                header = {
                    Text(
                        text = "Filled card",
                        color = LocalContentColor.current,
                        style = BTokens.typography.titleSmall
                    )
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
                    Text(
                        text = "Short description content. It can span 2-3 lines.",
                        style = BTokens.typography.bodyMedium
                    )
                },
                actions = {
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Text
                    ) {
                        Text("DETAILS", style = BTokens.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Filled
                    ) {
                        Text("ACTION", style = BTokens.typography.labelLarge)
                    }
                }
            )

            // Elevated + clickable
            var selected by remember { mutableStateOf(false) }
            BCard(
                style = BCardStyle.Elevated,
                onClick = { selected = !selected },
                selected = selected,
                header = {
                    Text(
                        text = if (selected) "Selected" else "Tap to select",
                        style = BTokens.typography.titleSmall
                    )
                },
                content = {
                    Text(
                        text = "Elevated card emphasized with tonal elevation.",
                        style = BTokens.typography.bodyMedium
                    )
                },
                actions = {
                    BButton(onClick = {}, style = BButtonStyle.Tonal) {
                        Text("Secondary", style = BTokens.typography.labelLarge)
                    }
                }
            )

            // Outlined
            BCard(
                style = BCardStyle.Outlined,
                header = { Text("Outlined card", style = BTokens.typography.titleSmall) },
                content = {
                    Text(
                        text = "Outline border with a transparent or lightly tonal surface.",
                        style = BTokens.typography.bodyMedium
                    )
                },
                actions = {
                    BButton(onClick = {}, style = BButtonStyle.Text) {
                        Text("DISMISS", style = BTokens.typography.labelLarge)
                    }
                }
            )

            // Semantic color cards
            BCard(
                style = BCardStyle.Success,
                header = { Text("Success", style = BTokens.typography.titleSmall) },
                content = { Text("Everything is fine.", style = BTokens.typography.bodyMedium) }
            )
            BCard(
                style = BCardStyle.Warning,
                header = { Text("Warning", style = BTokens.typography.titleSmall) },
                content = {
                    Text(
                        text = "Review the next action carefully.",
                        style = BTokens.typography.bodyMedium
                    )
                }
            )

            BCard(
                style = BCardStyle.Info,
                header = { Text("Info", style = BTokens.typography.titleSmall) },
                content = {
                    Text(
                        text = "Additional information for the user.",
                        style = BTokens.typography.bodyMedium
                    )
                }
            )
            BCard(
                style = BCardStyle.Destructive,
                header = { Text("Destructive", style = BTokens.typography.titleSmall) },
                content = {
                    Text(
                        text = "This action cannot be undone.",
                        style = BTokens.typography.bodyMedium
                    )
                },
                actions = {
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Outlined
                    ) {
                        Text("CANCEL", style = BTokens.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    BButton(
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        style = BButtonStyle.Destructive
                    ) {
                        Text("DELETE", style = BTokens.typography.labelLarge)
                    }
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
                header = { Text("Disabled card", style = BTokens.typography.titleSmall) },
                media = {
                    Box(
                        Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .background(BTokens.colorScheme.onSurface.copy(alpha = 0.06f))
                    )
                },
                content = {
                    Text(
                        text = "Disabled card: dimmed and not clickable.",
                        style = BTokens.typography.bodyMedium
                    )
                },
                actions = {
                    BButton(
                        modifier = Modifier.width(140.dp),
                        onClick = {},
                        enabled = false,
                        style = BButtonStyle.Text
                    ) {
                        Text("DETAILS", style = BTokens.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    BButton(
                        modifier = Modifier.width(140.dp),
                        onClick = {},
                        enabled = false,
                        style = BButtonStyle.Filled
                    ) {
                        Text("ACTION", style = BTokens.typography.labelLarge)
                    }
                }
            )
        }
    }
}
