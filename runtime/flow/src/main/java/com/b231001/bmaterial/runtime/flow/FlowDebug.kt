package com.b231001.bmaterial.runtime.flow

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Print a "START collect" log as soon as the flow is collected, then
 * log each emission with wall-clock time (HH:mm:ss.SSS), elapsed since start (t+ms),
 * and delta since previous emission (Δms). Returns the same stream (passthrough).
 *
 * Set [enabled] = false in production to remove overhead.
 */
fun <T> Flow<T>.debugLog(
    enabled: Boolean = true,
    tag: String = "check123",
    message: String? = null
): Flow<T> {
    if (!enabled) return this
    return flow {
        val fmt = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

        val startElapsed = System.nanoTime() / 1_000_000
        val startWall = System.currentTimeMillis()
        println(
            "[$tag] ${message.orEmpty()} | START collect | ${fmt.format(Date(startWall))} | t+0ms"
        )

        var last = startElapsed

        this@debugLog.collect { v ->
            val nowElapsed = System.nanoTime() / 1_000_000
            val nowWall = System.currentTimeMillis()
            val sinceStart = nowElapsed - startElapsed
            val sincePrev = nowElapsed - last
            last = nowElapsed

            val msg = buildString {
                if (!message.isNullOrBlank()) append(message).append(' ')
                append(
                    "| value=$v | ${fmt.format(Date(nowWall))} | " +
                        "t+${sinceStart}ms | Δ${sincePrev}ms"
                )
            }
            println("[$tag] $msg")
            emit(v)
        }
    }
}
