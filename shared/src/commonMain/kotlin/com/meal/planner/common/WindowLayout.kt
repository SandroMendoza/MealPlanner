package com.meal.planner.common

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * True when the app window is short, such as a phone in landscape. Screens use it to let
 * headers scroll with their content and to swap the bottom navigation bar for a rail.
 */
val LocalCompactHeight = staticCompositionLocalOf { false }

/** Material's compact window height class ends at 480dp. */
private val CompactHeightThreshold = 480.dp

fun isCompactHeight(windowHeight: Dp): Boolean = windowHeight < CompactHeightThreshold
