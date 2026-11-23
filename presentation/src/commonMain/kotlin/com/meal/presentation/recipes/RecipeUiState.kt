package com.meal.presentation.recipes

import com.meal.domain.model.Recipe
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory

enum class RecipeEmptyState { NO_RECIPES, NO_FAVORITES, NO_CREATED, NO_MATCHES }

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: FoodCategory? = null,
    val selectedCuisine: Cuisine? = null,
    val prepTimeRange: ClosedFloatingPointRange<Float> = FULL_PREP_TIME_RANGE,
    val showFavoritesOnly: Boolean = false,
    val showCreatedOnly: Boolean = false,
    val viewMode: RecipeViewMode = RecipeViewMode.GRID
) {
    val hasRecipes: Boolean
        get() = recipes.isNotEmpty()

    val filteredRecipes: List<Recipe>
        get() = recipes
            .asSequence()
            .filter { !showFavoritesOnly || it.isFavorite }
            .filter { !showCreatedOnly || it.isUserCreated }
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { selectedCuisine == null || it.cuisine == selectedCuisine }
            .filter { matchesPrepTime(it.preparationTimeMinutes) }
            .filter { recipe -> matchesSearch(recipe, searchQuery) }
            .toList()

    val hasFilteredRecipes: Boolean
        get() = filteredRecipes.isNotEmpty()

    val showEmptyState: Boolean
        get() = !isLoading && !hasFilteredRecipes && errorMessage == null

    val emptyState: RecipeEmptyState
        get() = when {
            !hasRecipes -> RecipeEmptyState.NO_RECIPES
            showFavoritesOnly -> RecipeEmptyState.NO_FAVORITES
            showCreatedOnly -> RecipeEmptyState.NO_CREATED
            else -> RecipeEmptyState.NO_MATCHES
        }

    /** At the slider's top value the range has no upper limit, so longer recipes still match. */
    val prepTimeHasNoMax: Boolean
        get() = prepTimeRange.endInclusive >= MAX_PREP_TIME_MINUTES

    private fun matchesPrepTime(minutes: Int): Boolean =
        minutes >= prepTimeRange.start && (prepTimeHasNoMax || minutes <= prepTimeRange.endInclusive)

    private fun matchesSearch(recipe: Recipe, query: String): Boolean {
        if (query.isBlank()) return true
        val needle = query.trim()
        return recipe.name.contains(needle, ignoreCase = true) ||
            recipe.description?.contains(needle, ignoreCase = true) == true ||
            recipe.ingredients.any { it.ingredient.name.contains(needle, ignoreCase = true) }
    }

    companion object {
        const val MAX_PREP_TIME_MINUTES = 180f
        val FULL_PREP_TIME_RANGE = 0f..MAX_PREP_TIME_MINUTES
    }
}
