package com.b231001.bmaterial.uicomponents.slider

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTheme

@Preview(
    name = "BSlider Gallery – FHD",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BSliderGallery() {
    BTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Continuous with ticks + tooltip
            var sliderVal1 by remember { mutableFloatStateOf(50f) }
            Text("Continuous Slider (0-100): ${sliderVal1.toInt()}")
            BSlider(
                value = sliderVal1,
                onValueChange = { sliderVal1 = it },
                valueRange = 0f..100f,
                showTickMarks = true,
                showValueLabel = true,
                style = BSliderStyle.Primary
            )

            // 2. Discrete
            var sliderVal2 by remember { mutableFloatStateOf(25f) }
            Text("Discrete Slider (0-50, 10 steps): ${sliderVal2.toInt()}")
            BSlider(
                value = sliderVal2,
                onValueChange = { sliderVal2 = it },
                valueRange = 0f..50f,
                steps = 9,
                showTickMarks = true,
                showValueLabel = true,
                style = BSliderStyle.Success
            )

            // 3. Non-uniform values
            val customValues = listOf(-10f, -5f, -3f, 0f, 3f, 5f)
            var sliderVal3 by remember { mutableFloatStateOf(0f) }
            Text("Custom Values Slider [-10, -5, -3, 0, 3, 5]: ${sliderVal3.toInt()}")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${customValues.first().toInt()}", modifier = Modifier.width(40.dp))
                BSlider(
                    value = sliderVal3,
                    onValueChange = { sliderVal3 = it },
                    valueRange = -10f..5f,
                    allowedValues = customValues,
                    showTickMarks = true,
                    showValueLabel = true,
                    style = BSliderStyle.Info,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${customValues.last().toInt()}",
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.End
                )
            }
            Text("(Mốc 0 có tick mặc định trong dải)")

            // 4. Range slider
            var rangeValues by remember { mutableStateOf(20f..80f) }
            Text(
                "Range Slider (0-100): " +
                    "${rangeValues.start.toInt()} - ${rangeValues.endInclusive.toInt()}"
            )
            BRangeSlider(
                value = rangeValues,
                onValueChange = { rangeValues = it },
                valueRange = 0f..100f,
                steps = 4,
                showTickMarks = true,
                showValueLabel = true,
                style = BSliderStyle.Primary
            )

            // 5. Limited region
            var sliderVal5 by remember { mutableFloatStateOf(50f) }
            Text("Limited Slider (limit 30..70): ${sliderVal5.toInt()}")
            BSlider(
                value = sliderVal5,
                onValueChange = { sliderVal5 = it },
                valueRange = 0f..100f,
                limitMin = 30f,
                limitMax = 70f,
                steps = 0,
                showTickMarks = true,
                showValueLabel = true,
                style = BSliderStyle.Warning
            )
            Text("Chỉ điều chỉnh trong khoảng 30–70 (hai đầu bị mờ).")
        }
    }
}
