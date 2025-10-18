package com.b231001.bmaterial.runtime.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Emits **true** immediately (a "pulse") whenever **any** upstream flow emits `true`.
 * Emits **false** only when:
 *  - every upstream has produced at least one value,
 *  - **no** upstream is currently `true` (i.e., all are `false`),
 *  - and false for this "all-false epoch" hasn't been emitted yet.
 */
fun truePulseFalseLatch(
    vararg sources: Flow<Boolean>
): Flow<Boolean> = channelFlow {
    val n = sources.size
    require(n > 0) { "Require at least 1 source" }

    val updates = Channel<Pair<Int, Boolean>>(UNLIMITED)

    val jobs = sources.mapIndexed { idx, f ->
        launch {
            f.collect { v -> updates.send(idx to v) }
        }
    }

    launch {
        jobs.joinAll()
        updates.close()
    }

    launch {
        val latest = BooleanArray(n)
        val seen = BooleanArray(n)
        var seenCount = 0
        var trueCount = 0
        var lastEmittedFalse = false

        for ((i, value) in updates) {
            val wasSeen = seen[i]
            val wasTrue = wasSeen && latest[i]

            if (!wasSeen) {
                seen[i] = true
                seenCount++
            }

            if (wasTrue && !value) trueCount--
            if (!wasTrue && value) trueCount++

            latest[i] = value

            if (value) {
                trySend(true)
                lastEmittedFalse = false
            } else if (seenCount == n && trueCount == 0 && !lastEmittedFalse) {
                trySend(false)
                lastEmittedFalse = true
            }
        }
    }
}

fun Iterable<Flow<Boolean>>.truePulseFalseLatch(): Flow<Boolean> =
    truePulseFalseLatch(*this.toList().toTypedArray())
