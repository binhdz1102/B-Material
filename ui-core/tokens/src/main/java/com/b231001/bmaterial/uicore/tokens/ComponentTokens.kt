package com.b231001.bmaterial.uicore.tokens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ComponentTokens {
    object Alpha {
        const val DisabledContainer = 0.12f
        const val DisabledContainerSubtle = 0.06f
        const val DisabledContent = 0.38f
        const val DisabledOutline = 0.24f
        const val FocusRing = 0.32f
        const val ChipFocusRing = 0.28f
        const val SelectedOverlay = 0.12f
        const val SemanticContentEmphasis = 0.9f
        const val ThumbBorderEnabled = 0.6f
        const val ThumbBorderDisabled = 0.3f
    }

    object Border {
        val Thin = 1.dp
        val Regular = 2.dp
    }

    object Button {
        val XsHeight = 32.dp
        val SmHeight = 40.dp
        val MdHeight = 48.dp
        val LgHeight = 56.dp
        val XlHeight = 64.dp

        val XsHorizontalPadding = 12.dp
        val SmHorizontalPadding = 14.dp
        val MdHorizontalPadding = 16.dp
        val LgHorizontalPadding = 18.dp
        val XlHorizontalPadding = 20.dp

        val DefaultGap = 8.dp
        val LgGap = 10.dp
        val XlGap = 12.dp

        val ElevatedDefault = 1.dp
        val ElevatedHovered = 2.dp
        val FocusRingCorner = 8.dp
    }

    object IconButton {
        val SmSize = 40.dp
        val MdSize = 48.dp
        val LgSize = 56.dp

        val SmIconSize = 20.dp
        val MdIconSize = 24.dp
        val LgIconSize = 28.dp

        val SmShapeCorner = 20.dp
        val MdShapeCorner = 24.dp
        val LgShapeCorner = 28.dp

        val SmFocusRingRadius = 14.dp
        val MdFocusRingRadius = 18.dp
        val LgFocusRingRadius = 20.dp

        val LgElevation = 1.dp
    }

    object Card {
        val SmallMediaCorner = 8.dp
        val MediumMediaCorner = 12.dp
        val LargeMediaCorner = 16.dp

        val SmallActionGap = 8.dp
        val MediumActionGap = 12.dp
        val LargeActionGap = 16.dp

        val SectionSpacing = 8.dp
        val ActionsSpacing = 12.dp
        val ElevatedDefault = 1.dp
        val ElevatedHovered = 2.dp
    }

    object Checkbox {
        val SmSide = 18.dp
        val MdSide = 22.dp
        val LgSide = 26.dp

        val SmCorner = 4.dp
        val MdCorner = 5.dp
        val LgCorner = 6.dp

        val SmBorderWidth = 1.5.dp
        val DefaultBorderWidth = 2.dp

        val FocusPadding = 2.dp
        val HoveredElevation = 1.dp

        val SmCheckStroke = 2.dp
        val MdCheckStroke = 2.5.dp
        val LgCheckStroke = 3.dp
    }

    object Chip {
        val SmHeight = 28.dp
        val MdHeight = 32.dp
        val LgHeight = 40.dp

        val SmHorizontalPadding = 10.dp
        val MdHorizontalPadding = 12.dp
        val LgHorizontalPadding = 14.dp

        val SmIconSize = 16.dp
        val MdIconSize = 18.dp
        val LgIconSize = 24.dp

        val SmAvatarSize = 18.dp
        val MdAvatarSize = 20.dp
        val LgAvatarSize = 24.dp

        val SmGap = 6.dp
        val MdGap = 8.dp
        val LgGap = 10.dp

        val LeadingGapAdjustment = 2.dp
        val LgElevation = 1.dp
    }

    object ListItem {
        val CompactOneLineHeight = 48.dp
        val CompactTwoLineHeight = 64.dp
        val CompactThreeLineHeight = 80.dp

        val DefaultOneLineHeight = 56.dp
        val DefaultTwoLineHeight = 72.dp
        val DefaultThreeLineHeight = 88.dp

        val LargeOneLineHeight = 72.dp
        val LargeTwoLineHeight = 88.dp
        val LargeThreeLineHeight = 100.dp

        val CompactLeadingSize = 24.dp
        val DefaultLeadingSize = 28.dp
        val LargeLeadingSize = 40.dp

        val CompactTrailingMinWidth = 24.dp
        val DefaultTrailingMinWidth = 28.dp
        val LargeTrailingMinWidth = 28.dp

        val CompactSpacing = 8.dp
        val DefaultSpacing = 12.dp
        val LargeSpacing = 12.dp

        val TwoLineVerticalPaddingExtra = 2.dp
        val ThreeLineVerticalPaddingExtra = 4.dp
        val LineSpacer = 2.dp
        val LargeElevation = 1.dp
    }

    object Loading {
        val CircularSize = 40.dp
        val CircularStrokeWidth = 4.dp
        val LinearHeight = 6.dp
        val DotSize = 10.dp
        val DotSpacing = 8.dp
    }

    object Scrollbar {
        val Thickness = 4.dp
        val Padding = 2.dp
        val MinThumbLength = 28.dp
        val CornerRadius = 999.dp
        val HitSlop = 12.dp
        val TooltipTextSize = 12.sp
        val TooltipPaddingH = 8.dp
        val TooltipPaddingV = 4.dp
        val TooltipGapFromThumb = 8.dp

        const val TrackAlpha = 0.12f
        const val ThumbAlpha = 0.45f
        const val TooltipBackgroundAlpha = 0.78f
        const val AutoHideDelayMillis = 700L
        const val FadeInMillis = 120
        const val FadeOutMillis = 250
    }

    object Slider {
        const val TrackShapePercent = 50
        val SmTrackHeight = 2.dp
        val MdTrackHeight = 4.dp
        val LgTrackHeight = 6.dp

        val SmThumbSize = 16.dp
        val MdThumbSize = 24.dp
        val LgThumbSize = 32.dp

        val SmFocusHaloRadius = 12.dp
        val MdFocusHaloRadius = 16.dp
        val LgFocusHaloRadius = 20.dp

        val SmTickSize = 2.dp
        val MdTickSize = 3.dp
        val LgTickSize = 4.dp

        val TooltipWidth = 44.dp
        val TooltipHeight = 26.dp
        val TooltipArrowHeight = 6.dp
        val TooltipCorner = 6.dp
        val TooltipGap = 4.dp
        val ThumbShadow = 2.dp

        const val ActiveTickScale = 1.25f
        const val TrackGrowFactor = 1.6f
        const val ThumbGrabRangeFactor = 1.5f
        const val OverlapSelectionThresholdFactor = 0.25f
    }

    object Switch {
        val SmWidth = 36.dp
        val MdWidth = 52.dp
        val LgWidth = 64.dp

        val SmHeight = 20.dp
        val MdHeight = 32.dp
        val LgHeight = 36.dp

        val SmThumbDiameter = 16.dp
        val MdThumbDiameter = 24.dp
        val LgThumbDiameter = 28.dp

        val SmThumbPadding = 2.dp
        val DefaultThumbPadding = 4.dp

        val SmTrackCorner = 10.dp
        val MdTrackCorner = 16.dp
        val LgTrackCorner = 18.dp

        val SmFocusRingRadius = 12.dp
        val MdFocusRingRadius = 18.dp
        val LgFocusRingRadius = 20.dp

        val SmThumbElevation = 1.dp
        val MdThumbElevation = 1.dp
        val LgThumbElevation = 2.dp

        val FocusPadding = 2.dp
    }

    object Layout {
        val DividerThickness = 1.dp
        val AutoDividerGap = 8.dp
        val OverlayMargin = 8.dp
    }

    object Overscroll {
        const val RubberBandConstant = 0.55f
        const val MaxRawFactor = 1.5f
        const val TransformLengthFactor = 1.5f
    }

    object TabRow {
        val EdgePadding = 0.dp
        val Gap = 0.dp
        val DividerThickness = 1.dp
        val IndicatorElevation = 2f
        val DividerElevation = 1f
        val TabsElevation = 0f
        val MinScrollableTabWidth = 90.dp

        const val ScrollAnimationMillis = 250
    }
}
