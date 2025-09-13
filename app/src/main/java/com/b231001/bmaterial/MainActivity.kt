package com.b231001.bmaterial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.b231001.bmaterial.uicore.resources.ui.InconsolataFontPreviewAll
import com.b231001.bmaterial.uicore.resources.ui.InterFontPreviewAll
import com.b231001.bmaterial.uicore.resources.ui.RobotoFontPreview
import com.b231001.bmaterial.uicore.resources.ui.RubikFontPreview
import com.b231001.bmaterial.uicore.resources.ui.SourGummyFontPreviewAll
import com.b231001.bmaterial.uicore.tokens.BTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RubikFontPreview(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    )

//                    InconsolataFontPreviewAll(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize(),
//                    )

//                    RobotoFontPreview(
//                    Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
//                    )



//                    InterFontPreviewAll(Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
//                    )


//                    SourGummyFontPreviewAll(Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
//                    )



                }
            }
        }
    }
}
