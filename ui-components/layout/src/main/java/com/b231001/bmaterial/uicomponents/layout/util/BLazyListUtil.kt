package com.b231001.bmaterial.uicomponents.layout.util

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.b231001.bmaterial.uicomponents.layout.column.BLazyScopeMarker

@BLazyScopeMarker
interface BLazyListScope {
    fun item(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable LazyItemScope.() -> Unit
    )

    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit
    )

    fun stickyHeader(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable LazyItemScope.() -> Unit
    )

    fun itemWithTag(
        tag: String,
        key: Any? = "tag:$tag",
        contentType: Any? = null,
        content: @Composable LazyItemScope.() -> Unit
    )

    /**
     * Register a tag for a range of [count] elements. `tagAt(index)` returns tag or null (ignored).
     */
    fun itemsTagged(
        count: Int,
        tagAt: (index: Int) -> String?,
        key: ((index: Int) -> Any)? = null,
        contentType: (index: Int) -> Any? = { null },
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit
    )

    /** Convenient overload for List<T>. */
    fun <T> itemsTagged(
        items: List<T>,
        tagOf: (item: T) -> String?,
        key: ((item: T) -> Any)? = null,
        contentType: (item: T) -> Any? = { null },
        itemContent: @Composable LazyItemScope.(item: T) -> Unit
    )
}

internal interface TagRegistrar {
    fun register(tag: String, index: Int)
}

internal val LocalTagRegistrar = compositionLocalOf<TagRegistrar?> { null }
internal val LocalCurrentItemIndex = compositionLocalOf<Int?> { null }
