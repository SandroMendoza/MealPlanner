package com.meal.presentation.recipes

import com.meal.domain.model.Recipe
import kotlinx.datetime.LocalDate

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pendingDeleteConfirmationDates: List<LocalDate>? = null,
    val isDeleted: Boolean = false,
    val readAloudAvailable: Boolean = false,
    val ingredientsSpeaking: Boolean = false,
    val cookModeActive: Boolean = false,
    val cookModeStepIndex: Int = 0,
    val cookModeSpeaking: Boolean = false
) {
    val notFound: Boolean
        get() = !isLoading && recipe == null && errorMessage == null
}
