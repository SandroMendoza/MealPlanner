package com.meal.presentation.recipes

import com.meal.domain.model.Recipe

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val hasRecipes: Boolean
        get() = recipes.isNotEmpty()

    val showEmptyState: Boolean
        get() = !isLoading && !hasRecipes && errorMessage == null
}
