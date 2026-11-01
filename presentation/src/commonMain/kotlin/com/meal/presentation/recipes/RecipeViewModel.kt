package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class RecipeViewModel(
    val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val state = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, recipes = emptyList())
            try {
                getAllRecipesUseCase.execute().collect { recipes ->
                    _uiState.value = _uiState.value.copy(
                        recipes = recipes,
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
