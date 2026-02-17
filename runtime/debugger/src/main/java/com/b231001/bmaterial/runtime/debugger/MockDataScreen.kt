package com.b231001.bmaterial.runtime.debugger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun MockDataScreen() {
    val mockDataManager = remember {
        MockDataManager<Color>(initData = Color.Yellow)
    }
    val currentColor by mockDataManager.stateFlowData().collectAsState()
    val listColor = remember { listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow) }

    val rule: EmitRule<Color> = remember {
        RandomFromListRule(
            periodMs = 1000L,
            values = listColor
        )
    }

    val pulseRule: EmitRule<Color> = remember {
        PulseBetweenRule(
            a = Color.Red,
            b = Color.Blue,
            periodMs = 1000L
        )
    }

    LaunchedEffect(Unit) {
//        mockDataManager.addEmitRule(rule)
        mockDataManager.addEmitRule(pulseRule)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Current Color: $currentColor",
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val newColor = listColor.random()
                mockDataManager.updateData(newColor)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change Color")
        }
    }
}
