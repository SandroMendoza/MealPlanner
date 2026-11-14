package com.meal.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.enums.MealType
import com.meal.domain.service.TtsSpeaker
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetUserProfileUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.presentation.common.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val MEAL_TYPE_ORDER = MealType.entries

class HomeViewModel(
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val ttsSpeaker: TtsSpeaker
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
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(readAloudAvailable = settings.readAloudEnabled)
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
