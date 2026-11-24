package com.meal.planner

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.Language
import com.meal.domain.model.ThemeMode
import com.meal.domain.strings.strings
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.common.LocalExpandedWidth
import com.meal.planner.common.LocalWideWidth
import com.meal.planner.common.SystemBarAppearance
import com.meal.planner.common.isCompactHeight
import com.meal.planner.common.isExpandedWidth
import com.meal.planner.common.isWideWidth
import com.meal.planner.components.KeyboardDismissButton
import com.meal.planner.navigation.AppNavHost
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTheme
import com.meal.presentation.preferences.AppPreferencesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(startRoute: String? = null, quickAddItemName: String? = null) {
    val preferencesViewModel: AppPreferencesViewModel = koinViewModel()
    val themeMode by preferencesViewModel.themeMode.collectAsStateWithLifecycle()
    val language by preferencesViewModel.language.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalStrings provides (language ?: Language.ENGLISH).strings()) {
        val darkTheme = when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
        }
        SystemBarAppearance(darkTheme)
        MealPlannerTheme(darkTheme = darkTheme) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(
                    LocalCompactHeight provides isCompactHeight(maxHeight),
                    LocalWideWidth provides isWideWidth(maxWidth, maxHeight),
                    LocalExpandedWidth provides isExpandedWidth(maxWidth, maxHeight)
                ) {
                    AppNavHost(startRoute = startRoute, quickAddItemName = quickAddItemName)
                    KeyboardDismissButton(modifier = Modifier.align(Alignment.BottomEnd))
                }
            }
        }
    }
}
