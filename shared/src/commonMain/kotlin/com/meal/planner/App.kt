package com.meal.planner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.planner.navigation.AppNavHost
import com.meal.planner.theme.MealPlannerTheme
import com.meal.presentation.theme.ThemeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val darkModeEnabled by themeViewModel.darkModeEnabled.collectAsStateWithLifecycle()

    MealPlannerTheme(darkTheme = darkModeEnabled == true) {
        AppNavHost()
    }
}
