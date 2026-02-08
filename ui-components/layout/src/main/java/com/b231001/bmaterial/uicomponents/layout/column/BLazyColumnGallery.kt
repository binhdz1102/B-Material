package com.b231001.bmaterial.uicomponents.layout.column

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showSystemUi = true, device = "id:pixel_7")
@Composable
fun BLazyColumnGallery() {
    val bState = rememberBLazyListState()
    val scope = rememberCoroutineScope()

    Column(Modifier.height(500.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { scope.animateScrollToTag(bState, tag = "special") }) {
                Text("Scroll to #special")
            }
            Button(onClick = { scope.animateScrollToTag(bState, tag = "special2") }) {
                Text("Scroll to #special2")
            }
        }

        val total = 120
        BLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = bState
        ) {
            item {
                Box(
                    modifier = Modifier
                        .background(Color.Yellow)
                        .size(300.dp)
                )
            }

            stickyHeader {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFEFEF))
                        .padding(12.dp)
                ) { Text("Sticky header") }
            }

            items(count = 40) { i ->
                DemoItem(index = i)
            }

            itemWithTag(tag = "special") {
                DemoItem(
                    index = 4444,
                    highlight = true,
                    label = "⭐ SPECIAL (#40)"
                )
            }

            items(count = total - 41) { i ->
                if (i == 20) {
                    DemoItem(
                        modifier = Modifier.bTag("special2"),
                        index = 5555,
                        highlight = true,
                        label = "⭐ SPECIAL 2"
                    )
                } else {
                    val realIndex = 41 + i
                    DemoItem(
                        index = realIndex,
                        modifier = Modifier.then(
                            if (realIndex == 100) Modifier.bTag("late-100") else Modifier
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoItem(
    index: Int,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    label: String = "Item #$index"
) {
    val bg = if (highlight) Color(0xFF1E88E5) else Color(0xFFF7F7F7)
    val fg = if (highlight) Color.White else Color(0xFF222222)

    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .heightIn(min = 56.dp)
            .background(bg, RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) { Text(label, color = fg) }
}
