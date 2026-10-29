package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.usecase.GetAllRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(
    val getAllRecipesUseCase: GetAllRecipesUseCase
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
}
