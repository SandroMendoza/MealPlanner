package com.meal.planner.common

import androidx.compose.runtime.Composable

/**
 * Sets the status and navigation bar icon colours to match the app's theme, which can
 * differ from the system's dark mode when the user picks Light or Dark in Settings.
 */
@Composable
expect fun SystemBarAppearance(darkTheme: Boolean)
