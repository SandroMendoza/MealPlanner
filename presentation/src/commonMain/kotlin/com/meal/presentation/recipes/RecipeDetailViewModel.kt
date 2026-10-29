package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.usecase.DeleteRecipeResult
import com.meal.domain.usecase.DeleteRecipeUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeDetailViewModel(
    private val recipeId: Uuid,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val state = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                getRecipeByIdUseCase.execute(recipeId).collect { recipe ->
                    _uiState.value = _uiState.value.copy(
                        recipe = recipe,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load recipe: ${e.message}"
                )
            }
        }
    }

    fun delete(force: Boolean = false) {
        viewModelScope.launch {
            when (val result = deleteRecipeUseCase.execute(recipeId, force)) {
                is DeleteRecipeResult.Deleted -> _uiState.value = _uiState.value.copy(
                    pendingDeleteConfirmationDates = null,
                    isDeleted = true
                )
                is DeleteRecipeResult.RequiresConfirmation -> _uiState.value = _uiState.value.copy(
                    pendingDeleteConfirmationDates = result.affectedDates
                )
            }
        }
    }

    fun dismissDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(pendingDeleteConfirmationDates = null)
    }
}
