package com.b231001.bmaterial.uicomponents.layout.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun StickyOverlayGallery() {
    // 3 anchors for demo
    val anchorA = rememberStickyOverlayState()
    val anchorB = rememberStickyOverlayState()
    val anchorC = rememberStickyOverlayState()

    var shown by remember { mutableStateOf(false) }
    var activeAnchor by remember { mutableStateOf<StickyOverlayState?>(null) }

    var outsideMode by remember { mutableStateOf(OutsideTapBehavior.Dismiss) }
    var prefer by remember {
        mutableStateOf(listOf(StickySide.Bottom, StickySide.Top, StickySide.Right, StickySide.Left))
    }
    var crossAlign by remember { mutableStateOf(StickyCrossAlign.Center) }

    var underlayClicks by remember { mutableIntStateOf(0) }

    // Observe placement of the currently active anchor
    val placement by (activeAnchor?.placementFlow ?: remember { MutableStateFlow(null) })
        .collectAsState(initial = null)

    Box(Modifier.background(Color.LightGray).fillMaxSize()) {
        // Underlay UI (to prove "PassThrough" can click through outside area)
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Underlay clicks = $underlayClicks",
                style = BTokens.typography.bodyMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    outsideMode =
                        if (outsideMode == OutsideTapBehavior.Dismiss) {
                            OutsideTapBehavior.PassThrough
                        } else {
                            OutsideTapBehavior.Dismiss
                        }
                }) {
                    Text("Outside: $outsideMode", style = BTokens.typography.labelLarge)
                }

                Button(onClick = {
                    crossAlign = when (crossAlign) {
                        StickyCrossAlign.Start -> StickyCrossAlign.Center
                        StickyCrossAlign.Center -> StickyCrossAlign.End
                        StickyCrossAlign.End -> StickyCrossAlign.Start
                    }
                }) { Text("CrossAlign: $crossAlign", style = BTokens.typography.labelLarge) }
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    prefer = listOf(
                        StickySide.Bottom,
                        StickySide.Top,
                        StickySide.Right,
                        StickySide.Left
                    )
                }) { Text("Prefer Bottom", style = BTokens.typography.labelLarge) }
                Button(onClick = {
                    prefer = listOf(
                        StickySide.Top,
                        StickySide.Bottom,
                        StickySide.Right,
                        StickySide.Left
                    )
                }) { Text("Prefer Top", style = BTokens.typography.labelLarge) }
                Button(onClick = {
                    prefer = listOf(
                        StickySide.Right,
                        StickySide.Left,
                        StickySide.Bottom,
                        StickySide.Top
                    )
                }) { Text("Prefer Right", style = BTokens.typography.labelLarge) }
                Button(onClick = {
                    prefer = listOf(
                        StickySide.Left,
                        StickySide.Right,
                        StickySide.Bottom,
                        StickySide.Top
                    )
                }) { Text("Prefer Left", style = BTokens.typography.labelLarge) }
            }

            Text(
                "Placement = ${placement?.chosenSide ?: "-"}\n" +
                    "anchor=${placement?.anchorBoundsInWindow}\n" +
                    "popup=${placement?.popupBoundsInWindow}",
                style = BTokens.typography.bodySmall
            )

            // Clickable underlay area
            Button(
                onClick = { underlayClicks++ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Click me (underlay)", style = BTokens.typography.labelLarge) }

            Spacer(Modifier.weight(1f))

            // Anchors near different edges
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    Modifier
                        .stickyOverlayAnchor(anchorA)
                        .border(1.dp, BTokens.colorScheme.outline, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text("Anchor A (top-start)", style = BTokens.typography.bodyMedium)
                }

                Box(
                    Modifier
                        .stickyOverlayAnchor(anchorB)
                        .border(1.dp, BTokens.colorScheme.outline, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text("Anchor B (top-end)", style = BTokens.typography.bodyMedium)
                }
            }

            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .stickyOverlayAnchor(anchorC)
                    .border(1.dp, BTokens.colorScheme.outline, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text("Anchor C (center)", style = BTokens.typography.bodyMedium)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        activeAnchor = anchorA; shown = true
                    }
                ) { Text("Show @A", style = BTokens.typography.labelLarge) }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        activeAnchor = anchorB; shown = true
                    }
                ) { Text("Show @B", style = BTokens.typography.labelLarge) }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        activeAnchor = anchorC; shown = true
                    }
                ) { Text("Show @C", style = BTokens.typography.labelLarge) }
            }
        }

        // The overlay itself (Popup)
        val st = activeAnchor
        if (st != null) {
            StickyOverlayLayout(
                shown = shown,
                anchorState = st,
                preferredSides = prefer,
                crossAxisAlign = crossAlign,
                outsideTapBehavior = outsideMode,
                onDismissRequest = {
                    shown = false
                    activeAnchor = null
                }
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .widthIn(min = 200.dp, max = 280.dp)
                        .border(
                            1.dp,
                            BTokens.colorScheme.outlineVariant,
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Column(
                        Modifier
                            .background(BTokens.colorScheme.surface)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("StickyOverlay", style = BTokens.typography.titleMedium)
                        Text(
                            "Chosen side: ${placement?.chosenSide ?: "-"}",
                            style = BTokens.typography.bodyMedium
                        )
                        Text(
                            "Outside mode: $outsideMode\n" +
                                "Prefer: ${prefer.joinToString()}",
                            style = BTokens.typography.bodySmall
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                // In PassThrough mode: outside click won't dismiss -> provide close button.
                                shown = false
                                activeAnchor = null
                            }) { Text("Close", style = BTokens.typography.labelLarge) }

                            Button(onClick = { underlayClicks++ }) {
                                Text("Do action", style = BTokens.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}
