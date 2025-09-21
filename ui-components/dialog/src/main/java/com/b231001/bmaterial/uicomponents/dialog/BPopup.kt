package com.b231001.bmaterial.uicomponents.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.b231001.bmaterial.uicore.tokens.BTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.DpOffset
import com.b231001.bmaterial.uicore.tokens.BTokens


@Stable
class BPopupState(initial: Boolean = false) {
    var isShown by mutableStateOf(initial)
    fun show() { isShown = true }
    fun hide() { isShown = false }
    fun toggle() { isShown = !isShown }
}

@Composable
private fun FullscreenScrim(color: Color, onDismiss: (() -> Unit)?) {
    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .then(
                if (onDismiss != null)
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
                else Modifier
            )
    )
}

@Composable
fun BPopup(
    state: BPopupState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    mode: BPopupMode = BPopupMode.Anchored,
    placement: BPopupPlacement = BPopupPlacement.Auto,
    dismissPolicy: Set<BPopupDismissPolicy> =
        setOf(BPopupDismissPolicy.OnClickOutside, BPopupDismissPolicy.OnBackPress),
    style: BPopupStyle = BPopupDefaults.style(),
    metrics: BPopupMetrics = BPopupDefaults.metrics(),
    anchorBoundsInWindow:  Rect? = null,
    content: @Composable () -> Unit
) {
    if (!state.isShown) return

    val enter = fadeIn() + scaleIn(initialScale = 0.96f)
    val exit  = fadeOut() + scaleOut(targetScale = 0.96f)
    val props = PopupProperties(
        focusable = metrics.focusTrap || dismissPolicy.contains(BPopupDismissPolicy.OnLoseFocus),
        dismissOnBackPress = dismissPolicy.contains(BPopupDismissPolicy.OnBackPress),
        dismissOnClickOutside = false
    )

    if (style.scrimColor != null) {
        FullscreenScrim(style.scrimColor, onDismiss =
        if (dismissPolicy.contains(BPopupDismissPolicy.OnClickOutside)) onDismissRequest else null)
    }

    AnimatedVisibility(visible = state.isShown, enter = enter, exit = exit) {
        if (mode == BPopupMode.Anchored && anchorBoundsInWindow != null) {
            Popup(
                properties = props,
                popupPositionProvider = AnchorPositionProvider(
                    placement = placement,
                    density = LocalDensity.current,
                    offset = metrics.offset
                ),
                onDismissRequest = onDismissRequest
            ) { PopupSurface(modifier, style, metrics, content) }
        } else {
            // Centered
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val outsideDismiss = style.scrimColor == null &&
                    dismissPolicy.contains(BPopupDismissPolicy.OnClickOutside)
                Box(
                    Modifier
                        .matchParentSize()
                        .then(if (outsideDismiss) Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onDismissRequest() } else Modifier)
                )
                PopupSurface(modifier, style, metrics, content)
            }
        }
    }
}

@Composable
private fun PopupSurface(
    modifier: Modifier,
    style: BPopupStyle,
    metrics: BPopupMetrics,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .widthIn(min = metrics.minWidth, max = metrics.maxWidth)
            .heightIn(min = metrics.minHeight, max = metrics.maxHeight)
            .semantics { popup() },
        color = style.containerColor,
        contentColor = style.contentColor,
        shape = style.shape,
        tonalElevation = style.elevation,
        shadowElevation = style.elevation,
        border = style.border
    ) {
        Box(Modifier.padding(style.padding)) { content() }
    }
}

