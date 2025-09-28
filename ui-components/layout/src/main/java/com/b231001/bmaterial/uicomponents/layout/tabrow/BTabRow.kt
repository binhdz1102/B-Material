package com.b231001.bmaterial.uicomponents.layout.tabrow

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.layout.tabrow.BTabRowDefaults.tabIndicatorOffset
import com.b231001.bmaterial.uicore.tokens.BTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = BTabRowDefaults.containerColor(),
    contentColor: Color = BTabRowDefaults.contentColor(),
    edgePadding: Dp = BTabRowDefaults.edgePadding(),
    gap: Dp = BTabRowDefaults.gap(),
    tabsElevation: Float = BTabRowDefaults.tabsElevation(),
    dividerElevation: Float = BTabRowDefaults.dividerElevation(),
    indicatorElevation: Float = BTabRowDefaults.indicatorElevation(),
    indicator: @Composable (tabPositions: List<BTabPosition>) -> Unit =
        @Composable { tabPositions ->
            if (selectedTabIndex in tabPositions.indices) {
                BTabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }
        },
    divider: @Composable () -> Unit = @Composable { BTabRowDefaults.Divider() },
    tabs: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .selectableGroup(),
        color = containerColor,
        contentColor = contentColor
    ) {
        SubcomposeLayout(Modifier.fillMaxWidth()) { constraints ->
            val tabRowWidth = constraints.maxWidth
            val tabMeasurables = subcompose(BTabSlots.Tabs, tabs)
            val tabCount = tabMeasurables.size

            val paddingPx = edgePadding.roundToPx()
            val gapPx = gap.roundToPx()
            val totalGapsPx = (tabCount - 1).coerceAtLeast(0) * gapPx

            var tabWidthPx = 0
            if (tabCount > 0) {
                val availableForTabs =
                    (tabRowWidth - paddingPx * 2 - totalGapsPx).coerceAtLeast(0)
                tabWidthPx = availableForTabs / tabCount
            }

            val tabRowHeight = tabMeasurables.fold(initial = 0) { max, curr ->
                maxOf(curr.maxIntrinsicHeight(tabWidthPx), max)
            }

            val tabPlaceables = tabMeasurables.map {
                it.measure(
                    constraints.copy(
                        minWidth = tabWidthPx,
                        maxWidth = tabWidthPx,
                        minHeight = tabRowHeight,
                        maxHeight = tabRowHeight
                    )
                )
            }

            val tabPositions = List(tabCount) { index ->
                val leftPx = paddingPx + index * (tabWidthPx + gapPx)
                BTabPosition(left = leftPx.toDp(), width = tabWidthPx.toDp())
            }

            layout(tabRowWidth, tabRowHeight) {
                // Place in order of elevation
                listOf(
                    "tabs" to tabsElevation,
                    "divider" to dividerElevation,
                    "indicator" to indicatorElevation
                ).sortedBy { it.second }
                    .forEach { (slot, _) ->
                        when (slot) {
                            "tabs" -> {
                                tabPlaceables.forEachIndexed { index, placeable ->
                                    val leftPx = paddingPx + index * (tabWidthPx + gapPx)
                                    placeable.placeRelative(leftPx, 0)
                                }
                            }

                            "divider" -> {
                                subcompose(BTabSlots.Divider, divider).forEach {
                                    val p = it.measure(constraints.copy(minHeight = 0))
                                    p.placeRelative(0, tabRowHeight - p.height)
                                }
                            }

                            "indicator" -> {
                                subcompose(BTabSlots.Indicator) { indicator(tabPositions) }
                                    .forEach {
                                        it.measure(Constraints.fixed(tabRowWidth, tabRowHeight))
                                            .placeRelative(0, 0)
                                    }
                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun BScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = BTabRowDefaults.containerColor(),
    contentColor: Color = BTabRowDefaults.contentColor(),
    edgePadding: Dp = BTabRowDefaults.edgePadding(),
    gap: Dp = BTabRowDefaults.gap(),
    tabsElevation: Float = BTabRowDefaults.tabsElevation(),
    dividerElevation: Float = BTabRowDefaults.dividerElevation(),
    indicatorElevation: Float = BTabRowDefaults.indicatorElevation(),
    indicator: @Composable (tabPositions: List<BTabPosition>) -> Unit =
        @Composable { tabPositions ->
            if (selectedTabIndex in tabPositions.indices) {
                BTabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }
        },
    divider: @Composable () -> Unit = @Composable { BTabRowDefaults.Divider() },
    tabs: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor
    ) {
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val scrollableTabData = remember(scrollState, coroutineScope) {
            ScrollableTabData(scrollState, coroutineScope)
        }

        SubcomposeLayout(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .selectableGroup()
                .clipToBounds()
        ) { constraints ->
            val minTabWidthPx = 90.dp.roundToPx()
            val paddingPx = edgePadding.roundToPx()
            val gapPx = gap.roundToPx()

            val tabMeasurables = subcompose(BTabSlots.Tabs, tabs)

            val layoutHeight = tabMeasurables.fold(initial = 0) { curr, measurable ->
                maxOf(curr, measurable.maxIntrinsicHeight(Constraints.Infinity))
            }

            val tabConstraints = constraints.copy(
                minWidth = minTabWidthPx,
                minHeight = layoutHeight,
                maxHeight = layoutHeight
            )

            val tabPlaceables = tabMeasurables.map { it.measure(tabConstraints) }

            val totalTabsWidth = tabPlaceables.sumOf { it.width }
            val totalGapsPx = (tabPlaceables.size - 1).coerceAtLeast(0) * gapPx
            val layoutWidth = paddingPx * 2 + totalTabsWidth + totalGapsPx

            layout(layoutWidth, layoutHeight) {
                val positions = ArrayList<BTabPosition>(tabPlaceables.size)
                run {
                    var x = paddingPx
                    tabPlaceables.forEach { p ->
                        positions += BTabPosition(left = x.toDp(), width = p.width.toDp())
                        x += p.width + gapPx
                    }
                }

                val dividerPlaceables = subcompose(BTabSlots.Divider, divider).map {
                    it.measure(
                        constraints.copy(
                            minHeight = 0,
                            minWidth = layoutWidth,
                            maxWidth = layoutWidth
                        )
                    )
                }
                val indicatorPlaceables = subcompose(BTabSlots.Indicator) { indicator(positions) }
                    .map { it.measure(Constraints.fixed(layoutWidth, layoutHeight)) }

                // Place in order of elevation
                listOf(
                    "tabs" to tabsElevation,
                    "divider" to dividerElevation,
                    "indicator" to indicatorElevation
                ).sortedBy { it.second }
                    .forEach { (slot, _) ->
                        when (slot) {
                            "tabs" -> {
                                var l = paddingPx
                                tabPlaceables.forEach { p ->
                                    p.placeRelative(l, 0)
                                    l += p.width + gapPx
                                }
                            }

                            "divider" -> {
                                dividerPlaceables.forEach { p ->
                                    p.placeRelative(0, layoutHeight - p.height)
                                }
                            }

                            "indicator" -> {
                                indicatorPlaceables.forEach { p ->
                                    p.placeRelative(0, 0)
                                }
                            }
                        }
                    }

                scrollableTabData.onLaidOut(
                    density = this@SubcomposeLayout,
                    edgeOffset = paddingPx,
                    tabPositions = positions,
                    selectedTab = selectedTabIndex
                )
            }
        }
    }
}

@Immutable
class BTabPosition internal constructor(val left: Dp, val width: Dp) {
    val right: Dp get() = left + width
    override fun equals(other: Any?): Boolean =
        other is BTabPosition && left == other.left && width == other.width

    override fun hashCode(): Int = 31 * left.hashCode() + width.hashCode()
    override fun toString(): String = "BTabPosition(left=$left, right=$right, width=$width)"
}

object BTabRowDefaults {
    @Composable
    fun containerColor(): Color = BTokens.colorScheme.surface

    @Composable
    fun contentColor(): Color = BTokens.colorScheme.onSurface

    @Composable
    fun indicatorHeight(): Dp = BTokens.sizes.extraSmall

    @Composable
    fun indicatorColor(): Color = BTokens.colorScheme.primary

    @Composable
    fun edgePadding(): Dp = 0.dp

    @Composable
    fun gap(): Dp = 0.dp

    @Composable
    fun tabsElevation(): Float = 0f

    @Composable
    fun dividerElevation(): Float = 1f

    @Composable
    fun indicatorElevation(): Float = 2f

    @Composable
    fun Divider() {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BTokens.colorScheme.outlineVariant)
        )
    }

    @Composable
    fun Indicator(
        modifier: Modifier = Modifier,
        height: Dp = indicatorHeight(),
        color: Color = indicatorColor()
    ) {
        Box(
            modifier
                .fillMaxWidth()
                .height(height)
                .background(color)
        )
    }

    fun Modifier.tabIndicatorOffset(currentTabPosition: BTabPosition): Modifier = composed(
        inspectorInfo = debugInspectorInfo {
            name = "tabIndicatorOffset"
            value = currentTabPosition
        }
    ) {
        val currentTabWidth by animateDpAsState(
            targetValue = currentTabPosition.width,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = ""
        )
        val indicatorOffset by animateDpAsState(
            targetValue = currentTabPosition.left,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = ""
        )
        fillMaxWidth()
            .wrapContentSize(Alignment.BottomStart)
            .offset(x = indicatorOffset)
            .width(currentTabWidth)
    }
}

private enum class BTabSlots { Tabs, Divider, Indicator }

private class ScrollableTabData(
    private val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope
) {
    private var selectedTab: Int? = null

    fun onLaidOut(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<BTabPosition>,
        selectedTab: Int
    ) {
        if (this.selectedTab != selectedTab) {
            this.selectedTab = selectedTab
            tabPositions.getOrNull(selectedTab)?.let { pos ->
                val calculatedOffset = pos.calculateTabOffset(density, edgeOffset, tabPositions)
                if (scrollState.value != calculatedOffset) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(
                            calculatedOffset,
                            animationSpec = ScrollableTabRowScrollSpec
                        )
                    }
                }
            }
        }
    }

    private fun BTabPosition.calculateTabOffset(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<BTabPosition>
    ): Int = with(density) {
        val totalTabRowWidth = tabPositions.last().right.roundToPx() + edgeOffset
        val visibleWidth = totalTabRowWidth - scrollState.maxValue
        val tabOffset = left.roundToPx()
        val scrollerCenter = visibleWidth / 2
        val tabWidthPx = width.roundToPx()
        val centered = tabOffset - (scrollerCenter - tabWidthPx / 2)
        val available = (totalTabRowWidth - visibleWidth).coerceAtLeast(0)
        centered.coerceIn(0, available)
    }
}

private val ScrollableTabRowScrollSpec: AnimationSpec<Float> = tween(
    durationMillis = 250,
    easing = FastOutSlowInEasing
)
