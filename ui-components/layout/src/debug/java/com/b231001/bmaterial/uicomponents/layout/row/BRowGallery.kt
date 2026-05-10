package com.b231001.bmaterial.uicomponents.layout.row

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.runtime.debugger.PrintLogDebug
import com.b231001.bmaterial.uicomponents.bswitch.BSwitch
import com.b231001.bmaterial.uicomponents.bswitch.BSwitchSize
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

@Composable
fun BRowVisibleRangeDemo() {
    val st = rememberBRowState()
    var first by remember { mutableStateOf<Int?>(null) }
    var last by remember { mutableStateOf<Int?>(null) }
    var prog by remember { mutableFloatStateOf(0f) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BTokens.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    ) {
        BRow(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 8.dp),
            state = st,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            visibleThreshold = 0.5f,
            onVisibleRangeChanged = { f, l, p ->
                first = f; last = l; prog = p
            }
        ) {
            BRowItems(count = 24) { i ->
                val w = when (i % 5) {
                    0 -> 84; 1 -> 112; 2 -> 140; 3 -> 172; else -> 96
                }
                val h = when (i % 3) {
                    0 -> 64; 1 -> 72; else -> 88
                }
                Box(
                    Modifier
                        .width(w.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BTokens.colorScheme.surface1)
                        .padding(8.dp)
                        .bRowItem(i),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Item $i", style = BTokens.typography.bodyLarge)
                }
            }
        }

        Column(
            Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BTokens.colorScheme.surface1)
                .border(1.dp, BTokens.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Text("firstVisible = ${first ?: "-"}", style = BTokens.typography.bodyMedium)
            Text("lastVisible  = ${last ?: "-"}", style = BTokens.typography.bodyMedium)
            Text(
                "progress     = ${"%.0f".format(prog * 100)}%",
                style = BTokens.typography.bodyMedium
            )
            PrintLogDebug(
                color = BTokens.colorScheme.error,
                tag = "BRowVisibleRangeDemo",
                message = "firstVisible=$first, " +
                    "lastVisible=$last, " +
                    "progress=${"%.2f".format(prog)}"
            )
        }
    }
}

@Composable
fun BRowAutoDividerDemo() {
    val st = rememberBRowState()

    Column(
        Modifier
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Auto Divider giữa các item (trong phạm vi BRow)",
            style = BTokens.typography.titleSmall,
            color = BTokens.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BTokens.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .background(BTokens.colorScheme.surface)
        ) {
            BRow(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 12.dp),
                state = st,
                autoDividerEnabled = true,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BRowItems(count = 12) { idx ->
                    Box(
                        Modifier
                            .width(120.dp)
                            .height(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BTokens.colorScheme.surface1)
                            .bRowItem(idx),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cell $idx", style = BTokens.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun BRowDemo() {
    val st = rememberBRowState()
    var lastEdge by remember { mutableStateOf<OverscrollEdge?>(null) }
    var scrollEnabled by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Scroll enabled", style = BTokens.typography.bodyMedium)
            BSwitch(
                checked = scrollEnabled,
                size = BSwitchSize.Sm,
                onCheckedChange = { scrollEnabled = it }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BTokens.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
        ) {
            BRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                state = st,
                scrollEnabled = scrollEnabled,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                autoDividerEnabled = true,
                onOverscrollActivated = { edge -> lastEdge = edge }
            ) {
                BRowItems(count = 18) { i ->
                    Box(
                        Modifier
                            .height((64 + (i % 3) * 12).dp)
                            .width((96 + (i % 4) * 24).dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BTokens.colorScheme.surface1)
                            .bRowItem(i),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Item $i", style = BTokens.typography.bodyLarge)
                    }
                }
            }

            lastEdge?.let {
                Text(
                    "Overscroll at: $it",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    style = BTokens.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun BRowItems(
    count: Int,
    divider: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(BTokens.colorScheme.outlineVariant)
        )
    },
    item: @Composable (index: Int) -> Unit
) {
    Row {
        repeat(count) { i ->
            item(i)
            if (i != count - 1) divider()
        }
    }
}

@Preview(
    name = "FHD Portrait",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BRowGallery() {
    BTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BRowDemo()
            BRowVisibleRangeDemo()
            BRowAutoDividerDemo()
        }
    }
}
