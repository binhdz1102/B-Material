package com.b231001.bmaterial.uicomponents.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTheme
import com.b231001.bmaterial.uicore.tokens.BTokens

@Preview(
    name = "FHD Portrait",
    showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=1920,unit=px,dpi=440"
)
@Composable
fun BLoadingGallery() {
    BTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Circular (Indeterminate)", style = BTokens.typography.titleSmall)
            BCircularLoading()

            Text("Circular (Percent)", style = BTokens.typography.titleSmall)
            BCircularLoading(state = BLoadingState.Percent(72f))

            Text("Circular (Range)", style = BTokens.typography.titleSmall)
            BCircularLoading(state = BLoadingState.Range(current = 38f, max = 60f))

            Text("Linear (Indeterminate)", style = BTokens.typography.titleSmall)
            BLinearLoading(modifier = Modifier.width(260.dp))

            Text("Linear (Percent)", style = BTokens.typography.titleSmall)
            BLinearLoading(
                state = BLoadingState.Percent(45f),
                modifier = Modifier.width(260.dp)
            )

            Text("Linear (Range)", style = BTokens.typography.titleSmall)
            BLinearLoading(
                state = BLoadingState.Range(current = 20f, max = 80f),
                modifier = Modifier.width(260.dp)
            )

            Text("Dots (Infinite)", style = BTokens.typography.titleSmall)
            BDotsLoading()

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
