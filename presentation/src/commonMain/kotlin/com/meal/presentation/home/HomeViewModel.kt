package com.meal.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetUserProfileUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.presentation.common.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val MEAL_TYPE_ORDER = MealType.entries

class HomeViewModel(
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val state = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserProfileUseCase.execute().collect { profile ->
                _uiState.value = _uiState.value.copy(greetingName = profile.name)
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
}
