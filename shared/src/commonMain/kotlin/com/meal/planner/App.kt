package com.meal.planner

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meal.planner.navigation.AppNavHost
import com.meal.planner.theme.MealPlannerTheme

@Composable
@Preview
fun App() {
    MealPlannerTheme {
        AppNavHost()
    }
}
