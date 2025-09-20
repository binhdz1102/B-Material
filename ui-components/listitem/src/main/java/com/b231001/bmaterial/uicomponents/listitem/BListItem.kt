package com.b231001.bmaterial.uicomponents.listitem

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTokens

@Stable
sealed interface BListItemStyle {
    data object Default : BListItemStyle // surface / onSurface
    data object Tonal : BListItemStyle // surfaceVariant
    data object Elevated : BListItemStyle // surface1 + shadow
    data object Destructive : BListItemStyle // error
    data object Success : BListItemStyle // success
    data object Warning : BListItemStyle // warning
    data object Info : BListItemStyle // info
}

@Stable
enum class BListItemSize { Compact, Default, Large }

@Stable
data class BListItemColors(
    val container: Color,
    val onContainer: Color,
    val supporting: Color,
    val overline: Color,
    val meta: Color,
    val disabledContainer: Color,
    val disabledContent: Color,
    val border: Color? = null
)

@Stable
data class BListItemMetrics(
    val minHeight: Dp,
    val contentPadding: PaddingValues,
    val leadingSize: Dp,
    val trailingMinWidth: Dp,
    val spacing: Dp,
    val shape: Shape,
    val headline: TextStyle,
    val supporting: TextStyle,
    val overline: TextStyle,
    val meta: TextStyle,
    val elevation: Dp
)

object BListItemDefaults {

    @Composable
    fun colors(style: BListItemStyle, enabled: Boolean = true): BListItemColors {
        val cs = BTokens.colorScheme
        val on = cs.onSurface
        val onVar = cs.onSurfaceVariant

        fun disabledContainer() = on.copy(alpha = 0.12f)
        fun disabledContent() = on.copy(alpha = 0.38f)

        val base = when (style) {
            BListItemStyle.Default -> BListItemColors(
                container = cs.surface,
                onContainer = on,
                supporting = onVar,
                overline = onVar,
                meta = onVar,
                disabledContainer = disabledContainer(),
                disabledContent = disabledContent()
            )

            BListItemStyle.Tonal -> BListItemColors(
                container = cs.surfaceVariant,
                onContainer = cs.onSurfaceVariant,
                supporting = cs.onSurface,
                overline = cs.onSurface,
                meta = cs.onSurface,
                disabledContainer = disabledContainer(),
                disabledContent = disabledContent(),
                border = cs.outlineVariant
            )

            BListItemStyle.Elevated -> BListItemColors(
                container = cs.surface1,
                onContainer = on,
                supporting = onVar,
                overline = onVar,
                meta = onVar,
                disabledContainer = disabledContainer(),
                disabledContent = disabledContent()
            )

            BListItemStyle.Destructive -> BListItemColors(
                container = cs.error,
                onContainer = cs.onError,
                supporting = cs.onError.copy(alpha = 0.9f),
                overline = cs.onError.copy(alpha = 0.9f),
                meta = cs.onError.copy(alpha = 0.9f),
                disabledContainer = disabledContainer(),
                disabledContent = disabledContent()
            )

            BListItemStyle.Success -> BListItemColors(
                container = cs.success,
                onContainer = cs.onSuccess,
                supporting = cs.onSuccess.copy(alpha = 0.9f),
                overline = cs.onSuccess.copy(alpha = 0.9f),
                meta = cs.onSuccess.copy(alpha = 0.9f),
                disabledContainer = disabledContainer(),
                disabledContent = disabledContent()
            )

            BListItemStyle.Warning -> BListItemColors(
                container = cs.warning,
                onContainer = cs.onWarning,
                supporting = cs.onWarning.copy(alpha = 0.9f),
                overline = cs.onWarning.copy(alpha = 0.9f),
                meta = cs.onWarning.copy(alpha = 0.9f),
                disabledContainer = disabledContainer(),
                disabledContent = disabledContent()
            )

            BListItemStyle.Info -> BListItemColors(
                container = cs.info,
                onContainer = cs.onInfo,
                supporting = cs.onInfo.copy(alpha = 0.9f),
                overline = cs.onInfo.copy(alpha = 0.9f),
                meta = cs.onInfo.copy(alpha = 0.9f),
                disabledContainer = disabledContainer(),
                disabledContent = disabledContent()
            )
        }

        return if (enabled) {
            base
        } else {
            base.copy(
                container = base.disabledContainer,
                onContainer = base.disabledContent,
                supporting = base.disabledContent,
                overline = base.disabledContent,
                meta = base.disabledContent
            )
        }
    }

    private data class Pack(
        val minH: Dp,
        val leading: Dp,
        val trailing: Dp,
        val spacing: Dp,
        val shape: Shape,
        val elev: Dp
    )

    @Composable
    fun metrics(
        size: BListItemSize,
        withOverline: Boolean,
        withSupporting: Boolean
    ): BListItemMetrics {
        val ty = BTokens.typography
        val sh = BTokens.shapes
        val pad = BTokens.paddings

        // // 1–3 lines depending on the two flags withOverline, withSupporting
        val lines =
            (1 + (if (withOverline) 1 else 0) + (if (withSupporting) 1 else 0)).coerceIn(1, 3)

        // Typography
        val headline = when (size) {
            BListItemSize.Compact -> ty.bodyLarge
            BListItemSize.Default -> ty.titleMedium
            BListItemSize.Large -> ty.titleLarge
        }
        val supporting = ty.bodyMedium
        val overline = ty.labelSmall
        val meta = ty.labelMedium

        val pack: Pack = when (size) {
            BListItemSize.Compact -> when (lines) {
                1 -> Pack(48.dp, 24.dp, 24.dp, 8.dp, sh.small, 0.dp)
                2 -> Pack(64.dp, 24.dp, 24.dp, 8.dp, sh.small, 0.dp)
                else -> Pack(80.dp, 24.dp, 24.dp, 8.dp, sh.small, 0.dp)
            }

            BListItemSize.Default -> when (lines) {
                1 -> Pack(56.dp, 28.dp, 28.dp, 12.dp, sh.medium, 0.dp)
                2 -> Pack(72.dp, 28.dp, 28.dp, 12.dp, sh.medium, 0.dp)
                else -> Pack(88.dp, 28.dp, 28.dp, 12.dp, sh.medium, 0.dp)
            }

            BListItemSize.Large -> when (lines) {
                1 -> Pack(72.dp, 40.dp, 28.dp, 12.dp, sh.large, 1.dp)
                2 -> Pack(88.dp, 40.dp, 28.dp, 12.dp, sh.large, 1.dp)
                else -> Pack(100.dp, 40.dp, 28.dp, 12.dp, sh.large, 1.dp)
            }
        }

        val verticalPad = when (lines) {
            1 -> pad.small
            2 -> pad.small + 2.dp
            else -> pad.small + 4.dp
        }

        return BListItemMetrics(
            minHeight = pack.minH,
            contentPadding = PaddingValues(horizontal = pad.large, vertical = verticalPad),
            leadingSize = pack.leading,
            trailingMinWidth = pack.trailing,
            spacing = pack.spacing,
            shape = pack.shape,
            headline = headline,
            supporting = supporting,
            overline = overline,
            meta = meta,
            elevation = pack.elev
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BListItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: BListItemStyle = BListItemStyle.Default,
    size: BListItemSize = BListItemSize.Default,
    overline: String? = null,
    headline: String,
    supporting: String? = null,
    metaText: String? = null,
    // Slots
    leading: (@Composable (() -> Unit))? = null,
    trailing: (@Composable (() -> Unit))? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    colors: BListItemColors = BListItemDefaults.colors(style, enabled),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val cs = BTokens.colorScheme

    // interaction states
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val dragged by interactionSource.collectIsDraggedAsState()

    val interactionOverlay = when {
        dragged -> cs.overlayPressed
        pressed -> cs.overlayPressed
        hovered -> cs.overlayHover
        focused -> cs.overlayFocus
        else -> Color.Transparent
    }

    val selectedOverlay =
        if (selected) colors.onContainer.copy(alpha = 0.12f) else Color.Transparent

    val targetAlpha = (interactionOverlay.alpha + selectedOverlay.alpha).coerceIn(0f, 1f)
    val overlayAlpha by animateFloatAsState(targetAlpha, label = "li-overlay-alpha")

    val withOverline = overline != null
    val withSupporting = supporting != null
    val m = BListItemDefaults.metrics(size, withOverline, withSupporting)

    val clickable = if (onClick != null || onLongClick != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .combinedClickable(
                enabled = enabled,
                onClick = onClick ?: {},
                onLongClick = onLongClick,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple()
            )
            .semantics {
                this.selected = selected
            }
    } else {
        Modifier
    }

    val borderStroke = colors.border?.let { BorderStroke(1.dp, it) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = m.minHeight)
            .then(clickable),
        shape = m.shape,
        color = if (enabled) colors.container else colors.disabledContainer,
        contentColor = if (enabled) colors.onContainer else colors.disabledContent,
        tonalElevation = m.elevation,
        shadowElevation = if (style == BListItemStyle.Elevated) m.elevation else 0.dp,
        border = borderStroke
    ) {
        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(colors.onContainer.copy(alpha = overlayAlpha))
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = m.minHeight)
                    .padding(m.contentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leading != null) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .widthIn(min = m.leadingSize)
                            .wrapContentWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides contentColorFor(colors.container)
                        ) {
                            leading()
                        }
                    }
                    Spacer(Modifier.width(m.spacing))
                }
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (overline != null) {
                        ProvideTextStyle(m.overline) {
                            CompositionLocalProvider(LocalContentColor provides colors.overline) {
                                Text(overline, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                    ProvideTextStyle(m.headline) {
                        CompositionLocalProvider(LocalContentColor provides colors.onContainer) {
                            Text(
                                headline,
                                maxLines = if (supporting == null) 2 else 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (supporting != null) {
                        Spacer(Modifier.height(2.dp))
                        ProvideTextStyle(m.supporting) {
                            CompositionLocalProvider(LocalContentColor provides colors.supporting) {
                                Text(supporting, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
                Spacer(Modifier.width(m.spacing))
                if (!metaText.isNullOrEmpty()) {
                    Box(
                        Modifier
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        ProvideTextStyle(m.meta) {
                            CompositionLocalProvider(LocalContentColor provides colors.meta) {
                                Text(metaText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    Spacer(Modifier.width(m.spacing))
                }
                if (trailing != null) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .defaultMinSize(minWidth = m.trailingMinWidth)
                            .wrapContentWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides contentColorFor(colors.container)
                        ) {
                            trailing()
                        }
                    }
                }
            }
        }
    }
}
