package com.meal.planner.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * True when the app window is short, such as a phone in landscape. Screens use it to let
 * headers scroll with their content and to swap the bottom navigation bar for a rail.
 */
val LocalCompactHeight = staticCompositionLocalOf { false }

/**
 * True when the window is wide and not short, such as a tablet in either orientation or an
 * unfolded foldable. Screens use it to lay content out in columns or panes, or to keep it to
 * a readable width, instead of stretching phone rows across the screen. A phone in landscape
 * is wide but short, so it keeps its own short-window layouts.
 */
val LocalWideWidth = staticCompositionLocalOf { false }

/**
 * True when the window is at least Material's expanded width and not short. The navigation
 * bar becomes a rail at this width.
 */
val LocalExpandedWidth = staticCompositionLocalOf { false }

/** Material's compact window height class ends at 480dp. */
private val CompactHeightThreshold = 480.dp

/** Material's medium window width class starts at 600dp. */
private val MediumWidthThreshold = 600.dp

/** Material's expanded window width class starts at 840dp. */
private val ExpandedWidthThreshold = 840.dp

/** The widest a single column of text, rows and controls gets on a wide window. */
val ReadableContentWidth = 720.dp

/** The widest a multi-column layout gets on a wide window. */
val WideContentWidth = 1040.dp

fun isCompactHeight(windowHeight: Dp): Boolean = windowHeight < CompactHeightThreshold

fun isWideWidth(windowWidth: Dp, windowHeight: Dp): Boolean =
    windowWidth >= MediumWidthThreshold && !isCompactHeight(windowHeight)

fun isExpandedWidth(windowWidth: Dp, windowHeight: Dp): Boolean =
    windowWidth >= ExpandedWidthThreshold && !isCompactHeight(windowHeight)

/** Fills the available width up to [maxWidth] and centres the content inside it. */
fun Modifier.centeredMaxWidth(maxWidth: Dp = ReadableContentWidth): Modifier =
    fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally).widthIn(max = maxWidth).fillMaxWidth()
