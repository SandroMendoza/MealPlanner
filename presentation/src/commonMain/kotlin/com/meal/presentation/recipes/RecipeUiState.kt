package com.meal.presentation.recipes

import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: FoodCategory? = null,
    val selectedMealType: MealType? = null,
    val prepTimeRange: ClosedFloatingPointRange<Float> = 0f..180f,
    val calorieRange: ClosedFloatingPointRange<Float> = 0f..1500f,
    val showFavoritesOnly: Boolean = false
) {
    val hasRecipes: Boolean
        get() = recipes.isNotEmpty()

    val filteredRecipes: List<Recipe>
        get() = recipes
            .asSequence()
            .filter { !showFavoritesOnly || it.isFavorite }
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { selectedMealType == null || it.mealTypes.contains(selectedMealType) }
            .filter { it.preparationTimeMinutes.toFloat() in prepTimeRange }
            .filter { recipe -> recipe.calories?.let { it.toFloat() in calorieRange } ?: true }
            .filter { recipe -> matchesSearch(recipe, searchQuery) }
            .toList()

    val hasFilteredRecipes: Boolean
        get() = filteredRecipes.isNotEmpty()

    val showEmptyState: Boolean
        get() = !isLoading && !hasFilteredRecipes && errorMessage == null

    val emptyStateMessage: String
        get() = when {
            !hasRecipes -> "No recipes yet."
            showFavoritesOnly -> "No favorites yet. Tap the heart on a recipe to add one."
            else -> "No recipes match your filters."
        }

    private fun matchesSearch(recipe: Recipe, query: String): Boolean {
        if (query.isBlank()) return true
        val needle = query.trim()
        return recipe.name.contains(needle, ignoreCase = true) ||
            recipe.description?.contains(needle, ignoreCase = true) == true ||
            recipe.ingredients.any { it.name.contains(needle, ignoreCase = true) }
    }
}
