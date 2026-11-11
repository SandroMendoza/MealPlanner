package com.meal.planner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.Language
import com.meal.domain.strings.strings
import com.meal.planner.components.KeyboardDismissButton
import com.meal.planner.navigation.AppNavHost
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTheme
import com.meal.presentation.preferences.AppPreferencesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val preferencesViewModel: AppPreferencesViewModel = koinViewModel()
    val darkModeEnabled by preferencesViewModel.darkModeEnabled.collectAsStateWithLifecycle()
    val language by preferencesViewModel.language.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalStrings provides (language ?: Language.ENGLISH).strings()) {
        MealPlannerTheme(darkTheme = darkModeEnabled == true) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppNavHost()
                KeyboardDismissButton(modifier = Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}
