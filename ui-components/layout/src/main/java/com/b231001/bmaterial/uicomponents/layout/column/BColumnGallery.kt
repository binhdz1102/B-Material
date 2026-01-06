package com.b231001.bmaterial.uicomponents.layout.column

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.runtime.debugger.PrintLogDebug
import com.b231001.bmaterial.uicomponents.bswitch.BSwitch
import com.b231001.bmaterial.uicomponents.bswitch.BSwitchSize
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens
import kotlin.random.Random

private fun rH(first: Int = 30, last: Int = 101): Int {
    return Random.nextInt(from = first, until = last)
}

@Composable
fun BColumnDemo() {
    val st = rememberBColumnState()
    var lastEdge by remember { mutableStateOf<OverscrollEdge?>(null) }
    var scrollEnabled by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Scroll enabled", style = BTokens.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            BSwitch(
                checked = scrollEnabled,
                size = BSwitchSize.Sm,
                onCheckedChange = { scrollEnabled = it }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BTokens.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
        ) {
            BColumn(
                modifier = Modifier.fillMaxSize(),
                state = st,
                scrollEnabled = scrollEnabled,
                onOverscrollActivated = { edge -> lastEdge = edge }
            ) {
                repeat(30) { i ->
                    Text(
                        "Row $i",
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .bColumnItem(i), // đánh dấu để BColumn đo
                        style = BTokens.typography.bodyMedium
                    )
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
fun BColumnVisibleRangeDemo() {
    val st = rememberBColumnState()
    var first by remember { mutableStateOf<Int?>(null) }
    var last by remember { mutableStateOf<Int?>(null) }
    var prog by remember { mutableFloatStateOf(0f) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BTokens.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    ) {
        BColumn(
            modifier = Modifier.matchParentSize(),
            state = st,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            visibleThreshold = 0.5f,
            onVisibleRangeChanged = { f, l, p ->
                first = f; last = l; prog = p
            }
        ) {
            BColumnItems(count = 40) { i ->
                if (i % 4 == 0) {
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .background(Color.Gray)
                            .bColumnItem(i), // đánh dấu để BColumn đo
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Item #$i",
                            Modifier.padding(12.dp),
                            style = BTokens.typography.bodyLarge
                        )
                    }
                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .height(remember { rH().dp })
                            .clip(RoundedCornerShape(10.dp))
                            .background(BTokens.colorScheme.surface1)
                            .bColumnItem(i), // đánh dấu để BColumn đo
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Item #$i",
                            Modifier.padding(12.dp),
                            style = BTokens.typography.bodyLarge
                        )
                    }
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
                tag = "BColumnVisibleRangeDemo",
                message = "firstVisible=$first, " +
                    "lastVisible=$last, " +
                    "progress=${"%.2f".format(prog)}"
            )
        }
    }
}

@Composable
fun BColumnAutoDividerDemo() {
    val st = rememberBColumnState()

    Column(Modifier.fillMaxWidth()) {
        Text(
            "Auto Divider giữa các item (trong phạm vi BColumn)",
            style = BTokens.typography.titleSmall,
            color = BTokens.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BTokens.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .background(BTokens.colorScheme.surface),
            state = st,
            autoDividerEnabled = true
        ) {
            BColumnItems(count = 15) { idx ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BTokens.colorScheme.surface1)
                        .bColumnItem(idx),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Row $idx", Modifier.padding(12.dp), style = BTokens.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun BColumnItems(
    count: Int,
    divider: @Composable () -> Unit = {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = BTokens.colorScheme.outlineVariant,
            thickness = 1.dp
        )
    },
    item: @Composable (index: Int) -> Unit
) {
    Column {
        repeat(count) { i ->
            item(i)
            if (i != count - 1) divider()
        }
    }
}

@Composable
fun BColumnGallery(modifier: Modifier = Modifier) {
    BTheme {
        Column(
            modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BColumnDemo()
            BColumnVisibleRangeDemo()
            BColumnAutoDividerDemo()
        }
    }
}

@Preview(
    name = "BColumn Gallery – FHD Portrait",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
private fun PreviewBColumnGallery() {
    BColumnGallery()
}
