package com.b231001.bmaterial.uicore.resources.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b231001.bmaterial.uicore.resources.Inconsolata
import com.b231001.bmaterial.uicore.resources.InconsolataCondensed
import com.b231001.bmaterial.uicore.resources.InconsolataExpanded
import com.b231001.bmaterial.uicore.resources.InconsolataExtraCondensed
import com.b231001.bmaterial.uicore.resources.InconsolataExtraExpanded
import com.b231001.bmaterial.uicore.resources.InconsolataSemiCondensed
import com.b231001.bmaterial.uicore.resources.InconsolataSemiExpanded
import com.b231001.bmaterial.uicore.resources.InconsolataUltraCondensed
import com.b231001.bmaterial.uicore.resources.InconsolataUltraExpanded
import com.b231001.bmaterial.uicore.resources.Inter18
import com.b231001.bmaterial.uicore.resources.Inter24
import com.b231001.bmaterial.uicore.resources.Inter28
import com.b231001.bmaterial.uicore.resources.Roboto
import com.b231001.bmaterial.uicore.resources.RobotoCondensed
import com.b231001.bmaterial.uicore.resources.RobotoSemiCondensed
import com.b231001.bmaterial.uicore.resources.Rubik
import com.b231001.bmaterial.uicore.resources.SourGummy
import com.b231001.bmaterial.uicore.resources.SourGummyExpanded

// Helpers
@Composable
private fun FontFamilyBlock(
    title: String,
    family: FontFamily,
    weights: List<Pair<String, FontWeight>>,
    showItalic: Boolean = true
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.W700,
                fontSize = 20.sp
            )
        )
        weights.forEach { (label, weight) ->
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = family,
                    fontWeight = weight,
                    fontSize = 18.sp
                )
            )
            if (showItalic) {
                Text(
                    text = "$label – Italic",
                    style = TextStyle(
                        fontFamily = family,
                        fontWeight = weight,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun FontPreviewGroup(
    screenTitle: String,
    groups: List<Pair<String, FontFamily>>,
    weights: List<Pair<String, FontWeight>>,
    showItalic: Boolean = true,
    headerFamily: FontFamily? = null
) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = screenTitle,
            style = TextStyle(
                fontFamily = headerFamily ?: MaterialTheme.typography.titleLarge.fontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 22.sp
            )
        )
        groups.forEachIndexed { index, (title, family) ->
            FontFamilyBlock(
                title = title,
                family = family,
                weights = weights,
                showItalic = showItalic
            )
            if (index < groups.lastIndex) Divider()
        }
    }
}

// Preview
// Rubik (300–900)
@Composable
fun RubikFontPreview(modifier: Modifier = Modifier) {
    val weights = listOf(
        "Light (300)" to FontWeight.W300,
        "Regular (400)" to FontWeight.W400,
        "Medium (500)" to FontWeight.W500,
        "SemiBold (600)" to FontWeight.W600,
        "Bold (700)" to FontWeight.W700,
        "ExtraBold (800)" to FontWeight.W800,
        "Black (900)" to FontWeight.W900
    )
    FontPreviewGroup(
        screenTitle = "Rubik Font Family",
        groups = listOf("Rubik" to Rubik),
        weights = weights,
        showItalic = true,
        headerFamily = Rubik
    )
}

// Inconsolata (200–900)
@Composable
fun InconsolataFontPreviewAll(modifier: Modifier = Modifier) {
    val groups = listOf(
        "Inconsolata (Normal)" to Inconsolata,
        "Inconsolata Condensed" to InconsolataCondensed,
        "Inconsolata ExtraCondensed" to InconsolataExtraCondensed,
        "Inconsolata SemiCondensed" to InconsolataSemiCondensed,
        "Inconsolata Expanded" to InconsolataExpanded,
        "Inconsolata ExtraExpanded" to InconsolataExtraExpanded,
        "Inconsolata SemiExpanded" to InconsolataSemiExpanded,
        "Inconsolata UltraCondensed" to InconsolataUltraCondensed,
        "Inconsolata UltraExpanded" to InconsolataUltraExpanded
    )
    val weights = listOf(
        "ExtraLight (200)" to FontWeight.W200,
        "Light (300)" to FontWeight.W300,
        "Regular (400)" to FontWeight.W400,
        "Medium (500)" to FontWeight.W500,
        "SemiBold (600)" to FontWeight.W600,
        "Bold (700)" to FontWeight.W700,
        "ExtraBold (800)" to FontWeight.W800,
        "Black (900)" to FontWeight.W900
    )

    FontPreviewGroup(
        screenTitle = "Inconsolata Font Family – All Width Sets",
        groups = groups,
        weights = weights,
        showItalic = false,
        headerFamily = Inconsolata
    )
}

// Roboto (Normal/Condensed/SemiCondensed, 100–900)
@Composable
fun RobotoFontPreview(modifier: Modifier = Modifier) {
    val groups = listOf(
        "Roboto (Normal width)" to Roboto,
        "Roboto Condensed" to RobotoCondensed,
        "Roboto SemiCondensed" to RobotoSemiCondensed
    )
    val weights = listOf(
        "Thin (100)" to FontWeight.W100,
        "ExtraLight (200)" to FontWeight.W200,
        "Light (300)" to FontWeight.W300,
        "Regular (400)" to FontWeight.W400,
        "Medium (500)" to FontWeight.W500,
        "SemiBold (600)" to FontWeight.W600,
        "Bold (700)" to FontWeight.W700,
        "ExtraBold (800)" to FontWeight.W800,
        "Black (900)" to FontWeight.W900
    )
    FontPreviewGroup(
        screenTitle = "Roboto Font Family – Normal / Condensed / SemiCondensed",
        groups = groups,
        weights = weights,
        showItalic = true,
        headerFamily = Roboto
    )
}

// Inter (18pt / 24pt / 28pt, 100–900)
@Composable
fun InterFontPreviewAll(
    modifier: Modifier = Modifier,
    inter18: FontFamily = Inter18,
    inter24: FontFamily = Inter24,
    inter28: FontFamily = Inter28
) {
    val groups = listOf(
        "Inter 18pt" to inter18,
        "Inter 24pt" to inter24,
        "Inter 28pt" to inter28
    )
    val weights = listOf(
        "Thin (100)" to FontWeight.W100,
        "ExtraLight (200)" to FontWeight.W200,
        "Light (300)" to FontWeight.W300,
        "Regular (400)" to FontWeight.W400,
        "Medium (500)" to FontWeight.W500,
        "SemiBold (600)" to FontWeight.W600,
        "Bold (700)" to FontWeight.W700,
        "ExtraBold (800)" to FontWeight.W800,
        "Black (900)" to FontWeight.W900
    )
    FontPreviewGroup(
        screenTitle = "Inter Font Family – 18pt / 24pt / 28pt",
        groups = groups,
        weights = weights,
        showItalic = true,
        headerFamily = inter24
    )
}

// Sour Gummy (Normal / SemiExpanded / Expanded, 100–900)
@Composable
fun SourGummyFontPreviewAll(
    modifier: Modifier = Modifier,
    normal: FontFamily = SourGummy,
    expanded: FontFamily = SourGummyExpanded
) {
    val groups = listOf(
        "Sour Gummy (Normal)" to normal,
        "Sour Gummy Expanded" to expanded
    )
    val weights = listOf(
        "Thin (100)" to FontWeight.W100,
        "ExtraLight (200)" to FontWeight.W200,
        "Light (300)" to FontWeight.W300,
        "Regular (400)" to FontWeight.W400,
        "Medium (500)" to FontWeight.W500,
        "SemiBold (600)" to FontWeight.W600,
        "Bold (700)" to FontWeight.W700,
        "ExtraBold (800)" to FontWeight.W800,
        "Black (900)" to FontWeight.W900
    )
    FontPreviewGroup(
        screenTitle = "Sour Gummy Font Family – Normal / SemiExpanded / Expanded",
        groups = groups,
        weights = weights,
        showItalic = true,
        headerFamily = normal
    )
}
