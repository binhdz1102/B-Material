package com.b231001.bmaterial.runtime.ktx

import kotlin.math.max
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private val DEFAULT_CONCURRENCY: Int =
    max(1, Runtime.getRuntime().availableProcessors())

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Iterable<T>.parallelForEach(
    concurrency: Int = DEFAULT_CONCURRENCY,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend (T) -> Unit
) = coroutineScope {
    val d = dispatcher.limitedParallelism(concurrency)
    for (item in this@parallelForEach) {
        launch(d) { block(item) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Iterable<T>.parallelForEachIndexed(
    concurrency: Int = DEFAULT_CONCURRENCY,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend (index: Int, value: T) -> Unit
) = coroutineScope {
    val d = dispatcher.limitedParallelism(concurrency)
    val list = this@parallelForEachIndexed.toList()
    for (i in list.indices) {
        launch(d) { block(i, list[i]) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T, R> Iterable<T>.parallelMap(
    concurrency: Int = DEFAULT_CONCURRENCY,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    transform: suspend (T) -> R
): List<R> = coroutineScope {
    val d = dispatcher.limitedParallelism(concurrency)
    this@parallelMap.map { item ->
        async(d) { transform(item) }
    }.awaitAll()
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Iterable<T>.parallelFilter(
    concurrency: Int = DEFAULT_CONCURRENCY,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    predicate: suspend (T) -> Boolean
): List<T> = coroutineScope {
    val d = dispatcher.limitedParallelism(concurrency)
    val list = this@parallelFilter.toList()

    val keep = list.map { item ->
        async(d) { predicate(item) }
    }.awaitAll()

    buildList {
        for (i in list.indices) if (keep[i]) add(list[i])
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Iterable<T>.parallelFind(
    concurrency: Int = DEFAULT_CONCURRENCY,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    predicate: suspend (T) -> Boolean
): T? = coroutineScope {
    val d = dispatcher.limitedParallelism(concurrency)
    val list = this@parallelFind.toList()

    val matches: List<Boolean> = list
        .map { item -> async(d) { predicate(item) } }
        .awaitAll()

    val firstIndex = matches.indexOfFirst { it }
    if (firstIndex >= 0) list[firstIndex] else null
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Iterable<T>.parallelFindAll(
    concurrency: Int = DEFAULT_CONCURRENCY,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    predicate: suspend (T) -> Boolean
): List<T> = coroutineScope {
    val d = dispatcher.limitedParallelism(concurrency)
    val list = this@parallelFindAll.toList()

    val keep: List<Boolean> = list
        .map { item -> async(d) { predicate(item) } }
        .awaitAll()

    buildList {
        for (i in list.indices) if (keep[i]) add(list[i])
    }
}
