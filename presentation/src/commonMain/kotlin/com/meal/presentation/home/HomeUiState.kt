package com.meal.presentation.home

import com.meal.domain.model.MealPlanEntry

data class HomeUiState(
    val greetingName: String = "Sandro",
    val todaysEntries: List<MealPlanEntry> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
