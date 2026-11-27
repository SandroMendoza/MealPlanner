package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val REFRESH_INDICATOR_MILLIS = 400L

@OptIn(ExperimentalUuidApi::class)
class RecipeViewModel(
    val getAllRecipesUseCase: GetAllRecipesUseCase,
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

    /** Gives every recipe a new random position; search and filters stay as they are. */
    fun reshuffle() {
        if (_uiState.value.isRefreshing) return
        shuffleKeys.clear()
        _uiState.value = _uiState.value.copy(
            recipes = _uiState.value.recipes.sortedBy { shuffleKeys.getOrPut(it.id) { Random.nextDouble() } },
            isRefreshing = true
        )
        viewModelScope.launch {
            // The reorder is instant, so the indicator stays up briefly to show the pull worked.
            delay(REFRESH_INDICATOR_MILLIS)
            _uiState.value = _uiState.value.copy(isRefreshing = false)
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

    fun onMealTypeSelected(mealType: MealType?) {
        val current = _uiState.value.selectedMealType
        _uiState.value = _uiState.value.copy(selectedMealType = if (current == mealType) null else mealType)
    }

    /** Filters by [mealType] without toggling, for screens that open on a given meal. */
    fun setMealTypeFilter(mealType: MealType?) {
        _uiState.value = _uiState.value.copy(selectedMealType = mealType)
    }

    fun onViewModeToggle() {
        val next = if (_uiState.value.viewMode == RecipeViewMode.GRID) RecipeViewMode.LIST else RecipeViewMode.GRID
        _uiState.value = _uiState.value.copy(viewMode = next)
    }

    fun onPrepTimeRangeChange(range: ClosedFloatingPointRange<Float>) {
        _uiState.value = _uiState.value.copy(prepTimeRange = range)
    }

    fun onFavoritesTabSelected(showFavoritesOnly: Boolean) {
        _uiState.value = _uiState.value.copy(showFavoritesOnly = showFavoritesOnly)
    }

    fun onCreatedOnlyTabSelected(showCreatedOnly: Boolean) {
        _uiState.value = _uiState.value.copy(showCreatedOnly = showCreatedOnly)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCategory = null,
            selectedCuisine = null,
            selectedMealType = null,
            showFavoritesOnly = false,
            showCreatedOnly = false,
            prepTimeRange = RecipeUiState.FULL_PREP_TIME_RANGE
        )
    }
}
