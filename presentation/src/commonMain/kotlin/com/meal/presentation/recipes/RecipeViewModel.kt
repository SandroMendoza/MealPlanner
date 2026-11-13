package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.Recipe
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeViewModel(
    val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val state = _uiState.asStateFlow()

    // Each recipe keeps the random position it was given for as long as this screen lives, so
    // the order only reshuffles when the screen is opened again, not on every database change.
    private val shuffleKeys = mutableMapOf<Uuid, Double>()

    init {
        loadRecipes()
        viewModelScope.launch {
            val defaultView = getSettingsUseCase.execute().first().defaultRecipeView
            _uiState.value = _uiState.value.copy(viewMode = defaultView)
        }
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, recipes = emptyList())
            try {
                getAllRecipesUseCase.execute().collect { recipes ->
                    _uiState.value = _uiState.value.copy(
                        recipes = recipes.sortedBy { shuffleKeys.getOrPut(it.id) { Random.nextDouble() } },
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load recipes: ${e.message}"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onCategorySelected(category: FoodCategory?) {
        val current = _uiState.value.selectedCategory
        _uiState.value = _uiState.value.copy(selectedCategory = if (current == category) null else category)
    }

    fun onCuisineSelected(cuisine: Cuisine?) {
        val current = _uiState.value.selectedCuisine
        _uiState.value = _uiState.value.copy(selectedCuisine = if (current == cuisine) null else cuisine)
    }

    fun onViewModeToggle() {
        val next = if (_uiState.value.viewMode == RecipeViewMode.GRID) RecipeViewMode.LIST else RecipeViewMode.GRID
        _uiState.value = _uiState.value.copy(viewMode = next)
    }

    fun onMealTypeSelected(mealType: MealType?) {
        val current = _uiState.value.selectedMealType
        _uiState.value = _uiState.value.copy(selectedMealType = if (current == mealType) null else mealType)
    }

    fun onPrepTimeRangeChange(range: ClosedFloatingPointRange<Float>) {
        _uiState.value = _uiState.value.copy(prepTimeRange = range)
    }

    fun onCalorieRangeChange(range: ClosedFloatingPointRange<Float>) {
        _uiState.value = _uiState.value.copy(calorieRange = range)
    }

    fun onFavoritesTabSelected(showFavoritesOnly: Boolean) {
        _uiState.value = _uiState.value.copy(showFavoritesOnly = showFavoritesOnly)
    }

    fun onCreatedOnlyTabSelected(showCreatedOnly: Boolean) {
        _uiState.value = _uiState.value.copy(showCreatedOnly = showCreatedOnly)
    }

    fun onToggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase.execute(recipe.id, !recipe.isFavorite)
        }
    }
}
