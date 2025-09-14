package com.b231001.bmaterial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(Modifier.padding(innerPadding)) {
                        Box(
                            Modifier
                                .padding(BTokens.sizes.small)
                                .clip(BTokens.shapes.large)
                                .background(color = BTokens.colorPalette.red.c40),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(BTokens.sizes.medium),
                                text = "thử Nghiệm gõ Tiếng viỆt",
                                style = BTokens.typography.titleMedium,
                                color = BTokens.colorScheme.background
                            )
                        }
                    }
                }
            }
        }
    }
}
