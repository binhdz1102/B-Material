package com.b231001.bmaterial.runtime.debugger

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

fun PrintLogDebug(
    message: String,
    color: Color? = null,
    priority: Int = 0,
    key: String? = null,
    tag: String? = null
) {
    DebuggerConsoleStore.log(
        message = message,
        color = color,
        priority = priority,
        key = key,
        tag = tag
    )
}

fun ClearDebugConsole() = DebuggerConsoleStore.clear()
fun RemoveDebugLine(key: String) = DebuggerConsoleStore.removeKey(key)
fun SetDebugConsoleEnabled(enabled: Boolean) = DebuggerConsoleStore.setEnabled(enabled)

data class DebuggerConsoleLine(
    val seq: Long,
    val timeMs: Long,
    val priority: Int,
    val key: String?,
    val tag: String?,
    val message: String,
    val color: Color?
)

object DebuggerConsoleStore {
    private val seqGen = AtomicLong(0)

    private val _enabled = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled

    private val _maxEntries = MutableStateFlow(250)
    val maxEntries: StateFlow<Int> = _maxEntries

    private val _lines = MutableStateFlow<List<DebuggerConsoleLine>>(emptyList())
    val lines: StateFlow<List<DebuggerConsoleLine>> = _lines

    fun setEnabled(enabled: Boolean) {
        _enabled.value = enabled
    }

    fun setMaxEntries(max: Int) {
        _maxEntries.value = max.coerceAtLeast(1)
        _lines.update { it.takeLast(_maxEntries.value) }
    }

    fun clear() {
        _lines.value = emptyList()
    }

    fun removeKey(key: String) {
        _lines.update { old -> old.filterNot { it.key == key } }
    }

    fun log(
        message: String,
        color: Color? = null,
        priority: Int = 0,
        key: String? = null,
        tag: String? = null
    ) {
        if (!_enabled.value) return

        val newLine = DebuggerConsoleLine(
            seq = seqGen.incrementAndGet(),
            timeMs = System.currentTimeMillis(),
            priority = priority,
            key = key,
            tag = tag,
            message = message,
            color = color
        )

        _lines.update { old ->
            val updated = if (key != null) {
                val idx = old.indexOfFirst { it.key == key }
                if (idx >= 0) old.toMutableList().also { it[idx] = newLine } else old + newLine
            } else {
                old + newLine
            }

            val max = _maxEntries.value
            if (updated.size <= max) updated else updated.takeLast(max)
        }
    }
}

data class DebuggerConsoleColor(
    val background: Color = Color(0xCC111111),
    val border: Color = Color(0x55FFFFFF),
    val headerBackground: Color = Color(0x55111111),
    val text: Color = Color(0xFFEAEAEA),
    val headerText: Color = Color(0xFFFFFFFF)
)

enum class DebuggerConsoleSort {
    PriorityDescThenNewestTop,
    PriorityDescThenNewestBottom
}

enum class DebuggerConsoleDisplayMode {
    LatestPerTag,
    History
}

data class DebuggerConsoleConfig(
    val paddingFromEdges: Dp = 8.dp,
    val initialOffset: DpOffset? = null,
    val wrapLines: Boolean = true,
    val entryMaxLinesWhenWrap: Int = 8,
    val showHeader: Boolean = true,
    val showClearButton: Boolean = true,
    val showCollapseButton: Boolean = true,
    val showLineNumbers: Boolean = false,
    val showTag: Boolean = true,
    val sort: DebuggerConsoleSort = DebuggerConsoleSort.PriorityDescThenNewestTop,
    val displayMode: DebuggerConsoleDisplayMode = DebuggerConsoleDisplayMode.LatestPerTag,
    val globalMaxEntries: Int = 250,
    val cornerRadius: Dp = 10.dp,
    val elevation: Dp = 10.dp
)

private fun reduceLines(
    lines: List<DebuggerConsoleLine>,
    mode: DebuggerConsoleDisplayMode
): List<DebuggerConsoleLine> {
    if (mode == DebuggerConsoleDisplayMode.History) return lines
    if (lines.isEmpty()) return lines

    val latest = LinkedHashMap<String, DebuggerConsoleLine>()

    for (line in lines) {
        val groupKey = when {
            !line.tag.isNullOrBlank() -> "tag:${line.tag}"
            !line.key.isNullOrBlank() -> "key:${line.key}"
            else -> "seq:${line.seq}"
        }

        val prev = latest[groupKey]
        if (prev == null || line.seq > prev.seq) {
            latest[groupKey] = line
        }
    }

    return latest.values.toList()
}

data class ContentBoundsPx(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float
) {
    val right: Float get() = left + width
    val bottom: Float get() = top + height
}

private fun LayoutCoordinates.boundsInRootPx(): ContentBoundsPx {
    val pos = positionInRoot()
    val size = size
    return ContentBoundsPx(
        left = pos.x,
        top = pos.y,
        width = size.width.toFloat(),
        height = size.height.toFloat()
    )
}

@Composable
fun DebuggerConsoleHost(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: DebuggerConsoleColor = DebuggerConsoleColor(),
    config: DebuggerConsoleConfig = DebuggerConsoleConfig(),
    windowModifier: Modifier = Modifier.requiredSize(420.dp, 220.dp),
    content: @Composable () -> Unit
) {
    LaunchedEffect(config.globalMaxEntries) {
        DebuggerConsoleStore.setMaxEntries(config.globalMaxEntries)
    }
    LaunchedEffect(enabled) {
        DebuggerConsoleStore.setEnabled(enabled)
    }

    var contentBounds by remember { mutableStateOf<ContentBoundsPx?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coords ->
                    contentBounds = coords.boundsInRootPx()
                }
        ) {
            content()
        }

        DebuggerConsoleOverlay(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(9999f),
            contentBoundsPx = contentBounds,
            windowModifier = windowModifier,
            colors = colors,
            config = config
        )
    }
}

@Composable
fun DebuggerConsoleOverlay(
    modifier: Modifier = Modifier,
    contentBoundsPx: ContentBoundsPx? = null,
    windowModifier: Modifier = Modifier.requiredSize(420.dp, 220.dp),
    colors: DebuggerConsoleColor = DebuggerConsoleColor(),
    config: DebuggerConsoleConfig = DebuggerConsoleConfig()
) {
    val enabled by DebuggerConsoleStore.enabled.collectAsState()
    if (!enabled) return

    val lines by DebuggerConsoleStore.lines.collectAsState()

    val reducedLines = remember(lines, config.displayMode) {
        reduceLines(lines, config.displayMode)
    }

    val displayLines = remember(reducedLines, config.sort) {
        val cmp = Comparator<DebuggerConsoleLine> { a, b ->
            val p = b.priority.compareTo(a.priority)
            if (p != 0) return@Comparator p
            when (config.sort) {
                DebuggerConsoleSort.PriorityDescThenNewestTop -> b.seq.compareTo(a.seq)
                DebuggerConsoleSort.PriorityDescThenNewestBottom -> a.seq.compareTo(b.seq)
            }
        }
        reducedLines.sortedWith(cmp)
    }

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val padPx = with(density) { config.paddingFromEdges.toPx() }

        val fallbackBounds = remember(maxWidth, maxHeight, density) {
            ContentBoundsPx(
                left = 0f,
                top = 0f,
                width = with(density) { maxWidth.toPx() },
                height = with(density) { maxHeight.toPx() }
            )
        }

        val bounds = contentBoundsPx ?: fallbackBounds

        var windowWpx by remember { mutableFloatStateOf(1f) }
        var windowHpx by remember { mutableFloatStateOf(1f) }

        fun clamp(x: Float, y: Float): Offset {
            val minX = bounds.left + padPx
            val minY = bounds.top + padPx
            val maxX = (bounds.right - windowWpx - padPx).coerceAtLeast(minX)
            val maxY = (bounds.bottom - windowHpx - padPx).coerceAtLeast(minY)
            return Offset(
                x = x.coerceIn(minX, maxX),
                y = y.coerceIn(minY, maxY)
            )
        }

        val initialOffsetPx = remember(
            config.initialOffset,
            config.paddingFromEdges,
            density,
            bounds.left,
            bounds.top
        ) {
            val initDp = config.initialOffset ?: DpOffset(
                config.paddingFromEdges,
                config.paddingFromEdges
            )
            val initX = bounds.left + with(density) { initDp.x.toPx() }
            val initY = bounds.top + with(density) { initDp.y.toPx() }
            Offset(initX, initY)
        }

        var offsetX by rememberSaveable { mutableFloatStateOf(initialOffsetPx.x) }
        var offsetY by rememberSaveable { mutableFloatStateOf(initialOffsetPx.y) }

        LaunchedEffect(bounds.left, bounds.top, bounds.width, bounds.height, windowWpx, windowHpx) {
            val c = clamp(offsetX, offsetY)
            offsetX = c.x
            offsetY = c.y
        }

        var collapsed by rememberSaveable { mutableStateOf(false) }
        val shape = RoundedCornerShape(config.cornerRadius)

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .then(windowModifier) // ✅ size controlled precisely by user modifier
                .onGloballyPositioned {
                    windowWpx = it.size.width.toFloat()
                    windowHpx = it.size.height.toFloat()
                    val c = clamp(offsetX, offsetY)
                    offsetX = c.x
                    offsetY = c.y
                }
                .shadow(config.elevation, shape, clip = false)
                .clip(shape)
                .background(colors.background)
                .border(1.dp, colors.border, shape)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDrag = { change, drag ->
                            change.consume()
                            val nextX = offsetX + drag.x
                            val nextY = offsetY + drag.y
                            val c = clamp(nextX, nextY)
                            offsetX = c.x
                            offsetY = c.y
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (config.showHeader) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.headerBackground)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val headerText =
                            "DBG (${displayLines.size}/${lines.size})  (long-press to drag)"
                        DebugText(
                            text = headerText,
                            color = colors.headerText,
                            maxLines = 1
                        )
                        Spacer(Modifier.weight(1f))

                        if (config.showClearButton) {
                            DebugText(
                                text = "CLEAR",
                                color = colors.headerText,
                                maxLines = 1,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { DebuggerConsoleStore.clear() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (config.showCollapseButton) {
                            Spacer(Modifier.width(6.dp))
                            DebugText(
                                text = if (collapsed) "EXPAND" else "COLLAPSE",
                                color = colors.headerText,
                                maxLines = 1,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { collapsed = !collapsed }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (!collapsed) {
                    val vScroll = rememberScrollState()
                    val hScroll = rememberScrollState()

                    val bodyModifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .then(
                            if (config.wrapLines) {
                                Modifier.verticalScroll(vScroll)
                            } else {
                                Modifier.horizontalScroll(hScroll).verticalScroll(vScroll)
                            }
                        )

                    val maxLines = if (config.wrapLines) config.entryMaxLinesWhenWrap else 1
                    val softWrap = config.wrapLines

                    SelectionContainer {
                        Column(modifier = bodyModifier) {
                            if (displayLines.isEmpty()) {
                                DebugText(
                                    text = "No logs yet…",
                                    color = colors.text,
                                    maxLines = 1
                                )
                            } else {
                                displayLines.forEachIndexed { index, line ->
                                    val lineColor = line.color ?: colors.text
                                    val prefix = buildString {
                                        if (config.showLineNumbers) append("${index + 1}. ")
                                        append("[p=${line.priority}] ")
                                        if (config.showTag && !line.tag.isNullOrBlank()) {
                                            append("${line.tag}: ")
                                        }
                                    }

                                    DebugText(
                                        text = prefix + line.message,
                                        color = lineColor,
                                        maxLines = maxLines,
                                        softWrap = softWrap,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    )

                                    Spacer(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(colors.border)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    maxLines: Int,
    softWrap: Boolean = true
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
        ),
        maxLines = maxLines,
        softWrap = softWrap
    )
}
