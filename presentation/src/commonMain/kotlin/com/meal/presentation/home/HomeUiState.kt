package com.meal.presentation.home

import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.Recipe

data class HomeUiState(
    val greetingName: String = "",
    val todaysEntries: List<MealPlanEntry> = emptyList(),
    val featuredRecipe: Recipe? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val readAloudAvailable: Boolean = false,
    val isSpeaking: Boolean = false
)
