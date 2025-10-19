package com.b231001.bmaterial.runtime.flow

import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

/**
 * If the upstream does **not** emit within [timeoutMillis], emits [fallback] exactly once.
 * When the upstream eventually emits, cancels the timer and forwards all subsequent values normally.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.fallbackIfLate(
    timeoutMillis: Long = 1_000,
    fallback: T
): Flow<T> = channelFlow {
    val firstFired = AtomicBoolean(false)

    val timer = launch {
        delay(timeoutMillis)
        if (firstFired.compareAndSet(false, true)) {
            trySend(fallback)
        }
    }

    val collector = launch {
        this@fallbackIfLate.collect { v ->
            if (firstFired.compareAndSet(false, true)) {
                timer.cancel()
            }
            trySend(v)
        }
    }

    invokeOnClose {
        timer.cancel()
        collector.cancel()
    }
}
