package com.b231001.bmaterial.uicomponents.layout.row

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicomponents.layout.util.BLazyListScope
import com.b231001.bmaterial.uicomponents.layout.util.LocalCurrentItemIndex
import com.b231001.bmaterial.uicomponents.layout.util.LocalTagRegistrar
import com.b231001.bmaterial.uicomponents.layout.util.TagRegistrar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class BLazyRowState internal constructor(
    val listState: LazyListState
) : TagRegistrar {

    private val _tagToIndex = mutableStateMapOf<String, Int>()
    val tagSnapshot: Map<String, Int> get() = _tagToIndex

    internal var startPaddingPx by mutableIntStateOf(0)

    private val stickyHeaderWidthsPx = mutableStateMapOf<Int, Int>()

    private fun startPinnedSpaceForIndex(targetIndex: Int): Int {
        var best = -1
        for (idx in stickyHeaderWidthsPx.keys) {
            if (idx in (best + 1)..targetIndex) best = idx
        }
        return if (best >= 0) stickyHeaderWidthsPx[best] ?: 0 else 0
    }

    internal fun updateStickyHeaderWidth(index: Int, widthPx: Int) {
        stickyHeaderWidthsPx[index] = widthPx
    }

    internal fun beginRegistration() {
        _tagToIndex.clear()
    }

    override fun register(tag: String, index: Int) {
        _tagToIndex[tag] = index
    }

    fun indexOf(tag: String): Int? = _tagToIndex[tag]

    suspend fun scrollToTag(tag: String, scrollOffset: Int = 0) {
        val idx = indexOf(tag) ?: return
        val inset = startPaddingPx + startPinnedSpaceForIndex(idx)
        listState.scrollToItem(idx, scrollOffset - inset)
    }

    suspend fun animateScrollToTag(tag: String, scrollOffset: Int = 0) {
        val idx = indexOf(tag) ?: return
        val inset = startPaddingPx + startPinnedSpaceForIndex(idx)
        listState.animateScrollToItem(idx, scrollOffset - inset)
    }
}

@Composable
fun rememberBLazyRowState(
    lazyListState: LazyListState = rememberLazyListState()
): BLazyRowState = remember { BLazyRowState(lazyListState) }

private class BLazyRowScopeImpl(
    private val delegate: LazyListScope,
    private val registrar: TagRegistrar,
    private val stateOwner: BLazyRowState
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
        for (i in 0 until count) tagAt(i)?.let { registrar.register(it, base + i) }
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
        items.forEachIndexed { i, item ->
            tagOf(item)?.let { tag ->
                registrar.register(
                    tag,
                    base + i
                )
            }
        }
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
            // Measure the sticky header width so scroll offsets can compensate for pinned space.
            val measureModifier = Modifier.onSizeChanged { size ->
                stateOwner.updateStickyHeaderWidth(myIndex, size.width)
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
fun BLazyRow(
    modifier: Modifier = Modifier,
    state: BLazyRowState = rememberBLazyRowState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal =
        if (!reverseLayout) Arrangement.Start else Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    content: BLazyListScope.() -> Unit
) {
    val density = LocalDensity.current
    val layoutDir = LocalLayoutDirection.current
    val startPadPx = with(density) { contentPadding.calculateStartPadding(layoutDir).roundToPx() }
    SideEffect { state.startPaddingPx = startPadPx }

    LazyRow(
        modifier = modifier,
        state = state.listState,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled
    ) {
        state.beginRegistration()
        val scopeImpl = BLazyRowScopeImpl(
            delegate = this,
            registrar = state,
            stateOwner = state
        )
        content(scopeImpl)
    }
}

fun CoroutineScope.scrollToTag(
    blazyState: BLazyRowState,
    tag: String,
    offsetPx: Int = 0
) = launch { blazyState.scrollToTag(tag, offsetPx) }

fun CoroutineScope.animateScrollToTag(
    blazyState: BLazyRowState,
    tag: String,
    offsetPx: Int = 0
) = launch { blazyState.animateScrollToTag(tag, offsetPx) }
