package com.meal.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.MealReminder
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetMealRemindersUseCase
import com.meal.domain.usecase.UpdateMealReminderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class NotificationSettingsViewModel(
    private val getMealRemindersUseCase: GetMealRemindersUseCase,
    private val updateMealReminderUseCase: UpdateMealReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val state = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getMealRemindersUseCase.execute().collect { reminders ->
                _uiState.value = _uiState.value.copy(
                    reminders = reminders.map { MealReminderUiState(it.mealType, it.enabled, it.time) },
                    isLoading = false
                )
            }
        }
    }

    fun setEnabled(mealType: MealType, enabled: Boolean) = persist(mealType) { it.copy(enabled = enabled) }
    fun setTime(mealType: MealType, time: LocalTime) = persist(mealType) { it.copy(time = time) }

    private fun persist(mealType: MealType, transform: (MealReminder) -> MealReminder) {
        val current = _uiState.value.reminders.first { it.mealType == mealType }
        val updated = transform(MealReminder(mealType, current.enabled, current.time))
        _uiState.value = _uiState.value.copy(
            reminders = _uiState.value.reminders.map {
                if (it.mealType == mealType) MealReminderUiState(updated.mealType, updated.enabled, updated.time) else it
            }
        )
        viewModelScope.launch { updateMealReminderUseCase.execute(updated) }
    }
}
