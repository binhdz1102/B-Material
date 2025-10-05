package com.b231001.bmaterial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.bswitch.BSwitchGallery
import com.b231001.bmaterial.uicomponents.button.BButtonGallery
import com.b231001.bmaterial.uicomponents.button.BIconButtonGallery
import com.b231001.bmaterial.uicomponents.card.BCardGallery
import com.b231001.bmaterial.uicomponents.checkbox.BCheckboxGallery
import com.b231001.bmaterial.uicomponents.chip.BChipGallery
import com.b231001.bmaterial.uicomponents.layout.column.BColumnGallery
import com.b231001.bmaterial.uicomponents.layout.tabrow.BTabRowGallery
import com.b231001.bmaterial.uicomponents.listitem.BListItemGallery
import com.b231001.bmaterial.uicomponents.slider.BSliderGallery
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(50.dp)
                    ) {
                        BColumnGallery()

                        BTabRowGallery()

                        BSliderGallery()

                        BChipGallery()

                        BListItemGallery()

                        BCardGallery()

                        BCheckboxGallery()

                        BSwitchGallery()

                        BIconButtonGallery()

                        BButtonGallery()

                        Box(
                            Modifier
                                .padding(start = BTokens.paddings.large)
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
                        // /////////////////////////////////////////////////////////////////
                    }
                }
            }
        }
    }
}
