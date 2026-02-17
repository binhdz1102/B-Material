package com.b231001.bmaterial.runtime.debugger

import java.io.Closeable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun interface EmitRule<T> {
    fun start(
        scope: CoroutineScope,
        emit: (T) -> Unit,
        current: () -> T
    ): Job
}

class MockDataManager<T>(
    initData: T,
    externalScope: CoroutineScope? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : Closeable {

    private val internalScope: CoroutineScope
    private val ownsScope: Boolean

    init {
        if (externalScope != null) {
            internalScope = externalScope
            ownsScope = false
        } else {
            internalScope = CoroutineScope(SupervisorJob() + dispatcher)
            ownsScope = true
        }
    }

    private val _state = MutableStateFlow(initData)
    val state: StateFlow<T> = _state.asStateFlow()

    private var ruleJob: Job? = null

    fun stateFlowData(): StateFlow<T> = state

    fun getData(): T = _state.value

    fun flowData(): Flow<T> = flowOf(_state.value)

    fun updateData(value: T): Boolean {
        val changed = _state.value != value
        _state.value = value
        return changed
    }

    /** Add a rule to automatically emit the new value. */
    fun addEmitRule(rule: EmitRule<T>) {
        ruleJob?.cancel()
        ruleJob = rule.start(
            scope = internalScope,
            emit = { v -> _state.value = v },
            current = { _state.value }
        )
    }

    /** Stop auto emit. */
    fun clearEmitRule() {
        ruleJob?.cancel()
        ruleJob = null
    }

    override fun close() {
        ruleJob?.cancel()
        ruleJob = null

        if (ownsScope) {
            internalScope.cancel()
        }
    }
}

/* Sample rules */
/**
 * Emits a random value every [periodMs] milliseconds.
 */
class RandomFromListRule<T>(
    private val values: List<T>,
    private val periodMs: Long
) : EmitRule<T> {
    override fun start(
        scope: CoroutineScope,
        emit: (T) -> Unit,
        current: () -> T
    ): Job = scope.launch {
        if (values.isEmpty()) return@launch

        while (isActive) {
            delay(periodMs)
            emit(values.random())
        }
    }
}

/**
 * Emits values from a predefined list [values] sequentially.
 */
class SequentialListRule<T>(
    private val values: List<T>,
    private val periodMs: Long,
    private val loop: Boolean = true,
    private val startIndex: Int = 0
) : EmitRule<T> {
    override fun start(
        scope: CoroutineScope,
        emit: (T) -> Unit,
        current: () -> T
    ): Job = scope.launch {
        if (values.isEmpty()) return@launch
        var index = startIndex.coerceIn(0, values.lastIndex)

        while (isActive) {
            delay(periodMs)
            emit(values[index])

            index++
            if (index > values.lastIndex) {
                if (!loop) break
                index = 0
            }
        }
    }
}

/**
 * Toggles between two values, [a] and [b], on each emission cycle.
 */
class PulseBetweenRule<T>(
    private val a: T,
    private val b: T,
    private val periodMs: Long
) : EmitRule<T> {
    override fun start(
        scope: CoroutineScope,
        emit: (T) -> Unit,
        current: () -> T
    ): Job = scope.launch {
        while (isActive) {
            delay(periodMs)
            val nextValue = if (current() == a) b else a
            emit(nextValue)
        }
    }
}
