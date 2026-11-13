package com.meal.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.service.TtsSpeaker
import com.meal.domain.usecase.DeleteRecipeResult
import com.meal.domain.usecase.DeleteRecipeUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.ToggleFavoriteUseCase
import com.meal.presentation.common.formatQuantity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeDetailViewModel(
    private val recipeId: Uuid,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val ttsSpeaker: TtsSpeaker
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val state = _uiState.asStateFlow()

    init {
        loadRecipe()
        viewModelScope.launch {
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(readAloudAvailable = settings.readAloudEnabled)
            }
        }
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

    fun toggleFavorite() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase.execute(recipeId, !recipe.isFavorite)
        }
    }

    fun toggleReadIngredientsAloud() {
        if (_uiState.value.ingredientsSpeaking) {
            ttsSpeaker.stop()
            _uiState.value = _uiState.value.copy(ingredientsSpeaking = false)
            return
        }
        val recipe = _uiState.value.recipe ?: return
        val text = recipe.ingredients.joinToString(". ") { "${formatQuantity(it.quantity)} ${it.unit.unit} ${it.ingredient.name}" }
        _uiState.value = _uiState.value.copy(ingredientsSpeaking = true)
        ttsSpeaker.speak(text) {
            _uiState.value = _uiState.value.copy(ingredientsSpeaking = false)
        }
    }

    fun startCookMode() {
        _uiState.value = _uiState.value.copy(cookModeActive = true, cookModeStepIndex = 0)
        speakCurrentStep()
    }

    fun stopCookMode() {
        ttsSpeaker.stop()
        _uiState.value = _uiState.value.copy(cookModeActive = false, cookModeSpeaking = false)
    }

    fun togglePlayPause() {
        if (_uiState.value.cookModeSpeaking) {
            ttsSpeaker.stop()
            _uiState.value = _uiState.value.copy(cookModeSpeaking = false)
        } else {
            speakCurrentStep()
        }
    }

    fun nextStep() = moveStep(1)

    fun previousStep() = moveStep(-1)

    private fun moveStep(delta: Int) {
        val steps = _uiState.value.recipe?.instructions ?: return
        val newIndex = (_uiState.value.cookModeStepIndex + delta).coerceIn(0, steps.lastIndex)
        _uiState.value = _uiState.value.copy(cookModeStepIndex = newIndex)
        speakCurrentStep()
    }

    private fun speakCurrentStep() {
        val steps = _uiState.value.recipe?.instructions ?: return
        val index = _uiState.value.cookModeStepIndex
        val step = steps.getOrNull(index) ?: return
        _uiState.value = _uiState.value.copy(cookModeSpeaking = true)
        ttsSpeaker.speak("Step ${step.stepNumber}. ${step.description}") { completedNormally ->
            if (!completedNormally) {
                _uiState.value = _uiState.value.copy(cookModeSpeaking = false)
            } else if (index >= steps.lastIndex) {
                stopCookMode()
            } else {
                nextStep()
            }
        }
    }

    override fun onCleared() {
        ttsSpeaker.stop()
    }
}
