package com.b231001.bmaterial.uicomponents.layout.column

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.layout.util.BLazyListScope
import com.b231001.bmaterial.uicomponents.layout.util.LocalCurrentItemIndex
import com.b231001.bmaterial.uicomponents.layout.util.LocalTagRegistrar
import com.b231001.bmaterial.uicomponents.layout.util.TagRegistrar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@DslMarker
annotation class BLazyScopeMarker

/**
 * Map tag→index
 * */
@Stable
class BLazyListState internal constructor(
    val listState: LazyListState
) : TagRegistrar {

    private val _tagToIndex = mutableStateMapOf<String, Int>()
    val tagSnapshot: Map<String, Int> get() = _tagToIndex

    internal var topPaddingPx by mutableIntStateOf(0)

    private val stickyHeaderHeightsPx = mutableStateMapOf<Int, Int>()

    private fun topPinnedSpaceForIndex(targetIndex: Int): Int {
        var best = -1
        for (idx in stickyHeaderHeightsPx.keys) {
            if (idx in (best + 1)..targetIndex) best = idx
        }
        return if (best >= 0) stickyHeaderHeightsPx[best] ?: 0 else 0
    }

    internal fun updateStickyHeaderHeight(index: Int, heightPx: Int) {
        stickyHeaderHeightsPx[index] = heightPx
    }

    internal fun beginRegistration() {
        _tagToIndex.clear()
    }

    override fun register(tag: String, index: Int) { _tagToIndex[tag] = index }

    fun indexOf(tag: String): Int? = _tagToIndex[tag]

    suspend fun scrollToTag(tag: String, scrollOffset: Int = 0) {
        val idx = indexOf(tag) ?: return
        val inset = topPaddingPx + topPinnedSpaceForIndex(idx)
        listState.scrollToItem(idx, scrollOffset - inset)
    }

    suspend fun animateScrollToTag(tag: String, scrollOffset: Int = 0) {
        val idx = indexOf(tag) ?: return
        val inset = topPaddingPx + topPinnedSpaceForIndex(idx)
        listState.animateScrollToItem(idx, scrollOffset - inset)
    }
}

@Composable
fun rememberBLazyListState(
    lazyListState: LazyListState = rememberLazyListState()
): BLazyListState = remember { BLazyListState(lazyListState) }

/**
 * Lazy modifier that registers tags based on the current index of the item.
 * Note: only called when the item is composed (usually when the item becomes visible/prefetched).
 * If you need the full map immediately (regardless of visibility) — use itemWithTag / itemsTagged.
 */
fun Modifier.bTag(tag: String): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "bTag"
        value = tag
    }
) {
    val registrar = LocalTagRegistrar.current
    val idx = LocalCurrentItemIndex.current
    if (registrar != null && idx != null) {
        SideEffect { registrar.register(tag, idx) }
    }
    this
}

private class BLazyColumnScopeImpl(
    private val delegate: LazyListScope,
    private val registrar: TagRegistrar,
    private val listStateOwner: BLazyListState
) : BLazyListScope {

    private var cursorIndex = 0

    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit
    ) {
        val myIndex = cursorIndex++
        delegate.item(key = key, contentType = contentType) {
            CompositionLocalProvider(
                LocalTagRegistrar provides registrar,
                LocalCurrentItemIndex provides myIndex
            ) { content() }
        }
    }

    override fun itemWithTag(
        tag: String,
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit
    ) {
        val myIndex = cursorIndex++
        registrar.register(tag, myIndex)
        delegate.item(key = key, contentType = contentType) {
            CompositionLocalProvider(
                LocalTagRegistrar provides registrar,
                LocalCurrentItemIndex provides myIndex
            ) { content() }
        }
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit
    ) {
        val base = cursorIndex
        cursorIndex += count
        delegate.items(count = count, key = key, contentType = contentType) { i ->
            CompositionLocalProvider(
                LocalTagRegistrar provides registrar,
                LocalCurrentItemIndex provides (base + i)
            ) { itemContent(i) }
        }
    }

    override fun itemsTagged(
        count: Int,
        tagAt: (index: Int) -> String?,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit
    ) {
        val base = cursorIndex
        for (i in 0 until count) {
            tagAt(i)?.let { registrar.register(it, base + i) }
        }
        items(count, key, contentType, itemContent)
    }

    override fun <T> itemsTagged(
        items: List<T>,
        tagOf: (item: T) -> String?,
        key: ((item: T) -> Any)?,
        contentType: (item: T) -> Any?,
        itemContent: @Composable LazyItemScope.(item: T) -> Unit
    ) {
        val base = cursorIndex
        items.forEachIndexed { i, it -> tagOf(it)?.let { t -> registrar.register(t, base + i) } }
        items(
            count = items.size,
            key = if (key == null) null else { idx -> key(items[idx]) },
            contentType = { idx -> contentType(items[idx]) }
        ) { idx -> itemContent(items[idx]) }
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun stickyHeader(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit
    ) {
        val myIndex = cursorIndex++
        delegate.stickyHeader(key = key, contentType = contentType) {
            val measureModifier = Modifier.onSizeChanged { size ->
                listStateOwner.updateStickyHeaderHeight(myIndex, size.height)
            }

            CompositionLocalProvider(
                LocalTagRegistrar provides registrar,
                LocalCurrentItemIndex provides myIndex
            ) {
                Box(measureModifier) { content() }
            }
        }
    }
}

@Composable
fun BLazyColumn(
    modifier: Modifier = Modifier,
    state: BLazyListState = rememberBLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    content: BLazyListScope.() -> Unit
) {
    val density = LocalDensity.current
    val topPadPx = with(density) { contentPadding.calculateTopPadding().roundToPx() }
    SideEffect { state.topPaddingPx = topPadPx }

    LazyColumn(
        modifier = modifier,
        state = state.listState,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled
    ) {
        state.beginRegistration()
        val scopeImpl = BLazyColumnScopeImpl(
            delegate = this,
            registrar = state,
            listStateOwner = state
        )
        content(scopeImpl)
    }
}

fun CoroutineScope.scrollToTag(
    blazyState: BLazyListState,
    tag: String,
    offsetPx: Int = 0
) = launch { blazyState.scrollToTag(tag, offsetPx) }

fun CoroutineScope.animateScrollToTag(
    blazyState: BLazyListState,
    tag: String,
    offsetPx: Int = 0
) = launch { blazyState.animateScrollToTag(tag, offsetPx) }
