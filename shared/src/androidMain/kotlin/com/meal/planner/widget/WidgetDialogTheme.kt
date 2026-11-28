package com.meal.planner.widget

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.meal.domain.model.AppSettings
import com.meal.domain.model.ThemeMode
import com.meal.domain.strings.Strings
import com.meal.domain.strings.strings
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTheme

/**
 * The app's theme and language for the dialogs widgets open over the home screen. Shows nothing
 * until the settings are read, so the dialog never flashes in the wrong language or theme.
 */
@Composable
internal fun WidgetDialogTheme(content: @Composable (Strings) -> Unit) {
    val settingsFlow = remember { settingsFlow() }
    val settings: AppSettings? by settingsFlow.collectAsState(null)
    val current = settings ?: return
    val strings = current.language.strings()
    val darkTheme = when (current.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    CompositionLocalProvider(LocalStrings provides strings) {
        MealPlannerTheme(darkTheme = darkTheme) { content(strings) }
    }
}
