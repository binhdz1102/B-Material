package com.b231001.bmaterial.uicomponents.layout.tabrow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.layout.tabrow.BTabRowDefaults.tabIndicatorOffset
import com.b231001.bmaterial.uicore.tokens.BTokens

@Composable
fun BTabRowGallery() {
    var selected by remember { mutableStateOf(0) }
    val titles = listOf("Home", "Feed", "Messages", "Profile", "Settings")

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        BTabRow(
            modifier = Modifier,
            selectedTabIndex = selected,
            containerColor = Color.LightGray,
            contentColor = Color.Blue,
            edgePadding = 8.dp,
            gap = 16.dp,
            divider = {
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.Gray)
                )
            },
            indicator = { positions ->
                val h = if (isSystemInDarkTheme()) BTokens.sizes.small else BTokens.sizes.extraSmall
                Box(
                    Modifier
                        .tabIndicatorOffset(positions[selected])
                        .height(h)
                        .background(
                            color = BTokens.colorScheme.primary,
                            shape = BTokens.shapes.small
                        )
                )
            }
        ) {
            titles.forEachIndexed { i, label ->
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxSize()
                        .clickable {
                            selected = i
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        style = BTokens.typography.bodyMedium
                    )
                }
            }
        }

        BScrollableTabRow(
            modifier = Modifier.width(350.dp),
            selectedTabIndex = selected,
            containerColor = Color.LightGray,
            contentColor = Color.Blue,
            edgePadding = 8.dp,
            gap = 16.dp,
            divider = {
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.Gray)
                )
            },
            indicator = { positions ->
                val h = if (isSystemInDarkTheme()) BTokens.sizes.small else BTokens.sizes.extraSmall
                Box(
                    Modifier
                        .tabIndicatorOffset(positions[selected])
                        .height(h)
                        .background(
                            color = BTokens.colorScheme.primary,
                            shape = BTokens.shapes.small
                        )
                )
            }
        ) {
            titles.forEachIndexed { i, label ->
                Tab(
                    selected = i == selected,
                    onClick = { selected = i },
                    text = {
                        Text(
                            label,
                            style = BTokens.typography.bodyMedium
                        )
                    }
                )
            }
        }

        BTabRow(
            modifier = Modifier.clip(BTokens.shapes.medium),
            selectedTabIndex = selected,
            containerColor = Color.LightGray,
            contentColor = Color.Blue,
            edgePadding = 0.dp,
            gap = 16.dp,
            tabsElevation = 2f,
            dividerElevation = 3f,
            indicatorElevation = 1f,
            divider = { /* No divider */ },
            indicator = { positions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(positions[selected])
                        .fillMaxSize()
                        .background(
                            color = Color.Gray,
                            shape = BTokens.shapes.medium
                        )
                )
            }
        ) {
            titles.forEachIndexed { i, label ->
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxSize()
                        .clip(BTokens.shapes.medium)
                        .clickable {
                            selected = i
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        style = BTokens.typography.bodyMedium
                    )
                }
            }
        }
    }
}
