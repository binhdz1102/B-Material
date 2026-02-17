package com.b231001.bmaterial.runtime.ktx

import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

private val DEFAULT_CONCURRENCY: Int =
    max(1, Runtime.getRuntime().availableProcessors())

fun interface Command<T> {
    suspend fun run(): T
}

sealed class ExecutionPolicy {
    data object Sequential : ExecutionPolicy()
    data class Parallel(val maxConcurrency: Int = DEFAULT_CONCURRENCY) : ExecutionPolicy()
}

data class CommandState(
    val pendingCount: Int = 0,
    val runningCount: Int = 0
)

class CommandManager(
    policy: ExecutionPolicy = ExecutionPolicy.Sequential,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    coroutineContext: CoroutineContext = SupervisorJob() + dispatcher
) : AutoCloseable {

    private val scope = CoroutineScope(coroutineContext)

    private val _policy = MutableStateFlow(policy)
    val policy: StateFlow<ExecutionPolicy> = _policy.asStateFlow()

    private val _state = MutableStateFlow(CommandState())
    val state: StateFlow<CommandState> = _state.asStateFlow()

    private val queue = Channel<QueuedCommand>(capacity = Channel.UNLIMITED)
    private val idCounter = AtomicLong(0L)

    // concurrent semaphores cache
    private val semMutex = Mutex()
    private val semaphores = HashMap<Int, Semaphore>()

    private val workerJob = scope.launch {
        // for-loop consumption pattern for Channel (sequential worker)
        for (item in queue) {
            executeInternal(item)
        }
    }

    fun setPolicy(policy: ExecutionPolicy) {
        _policy.value = policy
    }

    fun <T> submit(
        timeout: Duration? = null,
        command: Command<T>
    ): Long {
        val id = idCounter.incrementAndGet()
        val qc = QueuedCommand(
            id = id,
            timeout = timeout,
            runBlock = { command.run() }
        )

        bumpPending(+1)

        when (val p = _policy.value) {
            ExecutionPolicy.Sequential -> {
                queue.trySend(qc)
            }
            is ExecutionPolicy.Parallel -> {
                scope.launch { executeParallelWithLimit(qc, p.maxConcurrency) }
            }
        }
        return id
    }

    /**
     * Submit a list command.
     * - Sequential: ensures the submission order.
     * - Parallel: launches jobs immediately, with a maximum Concurrency limit.
     */
    fun submitAll(
        commands: List<Command<*>>,
        timeout: Duration? = null
    ): List<Long> = commands.map { cmd ->
        submit(timeout = timeout, command = cmd)
    }

    /**
     * Cancels running jobs (children of the scope) and clears the queue (if sequential).
     * Note: cancellation in coroutines throws a CancellationException: contentReference
     */
    suspend fun cancelAll() {
        // Cancel child coroutines (including parallel jobs)
        scope.coroutineContext.job.cancelChildren()

        // drain queue
        while (!queue.isEmpty) {
            queue.tryReceive().getOrNull()?.let {
                bumpPending(-1)
            }
        }
    }

    override fun close() {
        queue.close()
        workerJob.cancel()
        scope.cancel()
    }

    private suspend fun executeParallelWithLimit(item: QueuedCommand, maxConcurrency: Int) {
        val cap = maxConcurrency.coerceAtLeast(1)

        // Semaphore acquire/release to limit concurrency
        val sem = semMutex.withLock {
            semaphores.getOrPut(cap) { Semaphore(cap) }
        }

        sem.acquire()
        try {
            executeInternal(item)
        } finally {
            sem.release()
        }

        // Option: dispatcher.limitedParallelism(cap)
    }

    private suspend fun executeInternal(item: QueuedCommand) {
        bumpPending(-1)
        bumpRunning(+1)

        try {
            if (item.timeout != null) {
                withTimeout(item.timeout.inWholeMilliseconds) { item.runBlock() }
            } else {
                item.runBlock()
            }
        } finally {
            bumpRunning(-1)
        }
    }

    private fun bumpPending(delta: Int) {
        _state.update { it.copy(pendingCount = (it.pendingCount + delta).coerceAtLeast(0)) }
    }

    private fun bumpRunning(delta: Int) {
        _state.update { it.copy(runningCount = (it.runningCount + delta).coerceAtLeast(0)) }
    }

    private data class QueuedCommand(
        val id: Long,
        val timeout: Duration?,
        val runBlock: suspend () -> Any?
    )
}

internal fun demoCommandManager() = runBlocking {
//    val manager = CommandManager(policy = ExecutionPolicy.Sequential)
    val manager = CommandManager(policy = ExecutionPolicy.Parallel(2))

    val p1 = println(1)
    val p2 = println(2)
    val p3 = println(3)
    val p4 = println(4)

    val listP = listOf(p1, p2, p3, p4).map { Command { it } }

//    val stateJob = manager.state
//        .onEach { println("STATE pending=${it.pendingCount}, running=${it.runningCount}") }
//        .launchIn(this)

    manager.submitAll(
        commands = listP
    )

    delay(10000)
//    stateJob.cancel()
    manager.close()
}
