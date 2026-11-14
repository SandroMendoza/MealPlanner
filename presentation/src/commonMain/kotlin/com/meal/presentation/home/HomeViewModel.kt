package com.meal.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.MealType
import com.meal.domain.service.TtsSpeaker
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetUserProfileUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.presentation.common.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

private val MEAL_TYPE_ORDER = MealType.entries

@OptIn(ExperimentalUuidApi::class)
class HomeViewModel(
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val ttsSpeaker: TtsSpeaker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val state = _uiState.asStateFlow()

    private var allRecipes: List<Recipe> = emptyList()

    init {
        viewModelScope.launch {
            getUserProfileUseCase.execute().collect { profile ->
                _uiState.value = _uiState.value.copy(greetingName = profile.name)
            }
        }
        viewModelScope.launch {
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(readAloudAvailable = settings.readAloudEnabled)
            }
        }
        viewModelScope.launch {
            getAllRecipesUseCase.execute().collect { recipes ->
                allRecipes = recipes
                // Keep the recipe on screen across recipe edits; pick again only once it is deleted.
                val currentId = _uiState.value.featuredRecipe?.id
                val featured = recipes.firstOrNull { it.id == currentId } ?: dailyPick(recipes)
                _uiState.value = _uiState.value.copy(featuredRecipe = featured)
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val todayDate = today()
                getWeeklyMealPlanUseCase.execute(todayDate, todayDate).collect { entries ->
                    _uiState.value = _uiState.value.copy(
                        todaysEntries = entries.sortedBy { MEAL_TYPE_ORDER.indexOf(it.mealType) },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load today's plan: ${e.message}"
                )
            }
        }
    }

    fun shuffleFeaturedRecipe() {
        val currentId = _uiState.value.featuredRecipe?.id
        val candidates = allRecipes.filter { it.id != currentId }
        if (candidates.isEmpty()) return
        _uiState.value = _uiState.value.copy(featuredRecipe = candidates.random())
    }

    // Seeding with the date gives every launch on the same day the same recipe of the day.
    // Sorting by id keeps the pick independent of the order the repository returns.
    private fun dailyPick(recipes: List<Recipe>): Recipe? {
        if (recipes.isEmpty()) return null
        val sorted = recipes.sortedBy { it.id.toString() }
        val random = Random(today().toEpochDays().toLong())
        return sorted[random.nextInt(sorted.size)]
    }

    fun toggleReadAloud(spokenText: String) {
        if (_uiState.value.isSpeaking) {
            ttsSpeaker.stop()
            _uiState.value = _uiState.value.copy(isSpeaking = false)
            return
        }
        if (spokenText.isBlank()) return
        _uiState.value = _uiState.value.copy(isSpeaking = true)
        ttsSpeaker.speak(spokenText) {
            _uiState.value = _uiState.value.copy(isSpeaking = false)
        }
    }

    override fun onCleared() {
        ttsSpeaker.stop()
    }
}
