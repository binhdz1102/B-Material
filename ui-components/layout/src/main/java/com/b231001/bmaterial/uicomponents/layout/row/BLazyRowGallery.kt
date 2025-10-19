package com.b231001.bmaterial.uicomponents.layout.row

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.b231001.bmaterial.uicomponents.layout.column.bTag

@Preview(showSystemUi = true, device = "id:pixel_7")
@Composable
fun BLazyRowGallery() {
    val rState = rememberBLazyRowState()
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { scope.animateScrollToTag(rState, tag = "special") }) {
                Text("Row → #special")
            }
            Button(onClick = { scope.animateScrollToTag(rState, tag = "special2") }) {
                Text("Row → #special2")
            }
        }

        BLazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            state = rState
        ) {
            item {
                Box(
                    Modifier
                        .size(width = 240.dp, height = 120.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp))
                )
            }

            stickyHeader {
                Box(
                    Modifier
                        .width(120.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFEFEFEF))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Header ◀") }
            }

            items(count = 20) { i ->
                Box(
                    Modifier
                        .padding(horizontal = 6.dp, vertical = 10.dp)
                        .size(100.dp, 80.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("i=$i") }
            }

            itemWithTag(tag = "special") {
                Box(
                    Modifier
                        .padding(horizontal = 6.dp, vertical = 10.dp)
                        .size(120.dp, 80.dp)
                        .background(Color(0xFF1E88E5), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("⭐ special", color = Color.White) }
            }

            items(count = 40) { idx ->
                val at = idx + 1
                val tagged = at == 15
                Box(
                    (if (tagged) Modifier.bTag("special2") else Modifier)
                        .padding(horizontal = 6.dp, vertical = 10.dp)
                        .size(100.dp, 80.dp)
                        .background(
                            if (tagged) Color(0xFF43A047) else Color(0xFFF5F5F5),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (tagged) "⭐ special2" else "it=$at",
                        color = if (tagged) Color.White else Color.Unspecified
                    )
                }
            }
        }
    }
}
