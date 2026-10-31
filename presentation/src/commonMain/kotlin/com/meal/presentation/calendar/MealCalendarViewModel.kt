package com.meal.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.RemoveMealSlotUseCase
import com.meal.presentation.common.currentWeekMonday
import com.meal.presentation.common.mondayOf
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private fun buildWeekState(weekStart: LocalDate): MealCalendarUiState {
    val weekEnd = weekEndFor(weekStart)
    val days = (0..6).map { weekStart.plus(it, DateTimeUnit.DAY) }
    return MealCalendarUiState(weekStart = weekStart, weekEnd = weekEnd, days = days)
}

@OptIn(ExperimentalUuidApi::class)
class MealCalendarViewModel(
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase,
    private val removeMealSlotUseCase: RemoveMealSlotUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildWeekState(currentWeekMonday()))
    val state = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadWeek()
    }

    private fun loadWeek() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                getWeeklyMealPlanUseCase.execute(_uiState.value.weekStart, _uiState.value.weekEnd)
                    .collect { entries ->
                        _uiState.value = _uiState.value.copy(entries = entries, isLoading = false)
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load meal plan: ${e.message}"
                )
            }
        }
    }

    fun previousWeek() = changeWeek(-7)

    fun nextWeek() = changeWeek(7)

    private fun changeWeek(deltaDays: Int) {
        val newStart = _uiState.value.weekStart.plus(deltaDays, DateTimeUnit.DAY)
        _uiState.value = buildWeekState(newStart)
        loadWeek()
    }

    fun removeSlot(slotId: Uuid) {
        viewModelScope.launch {
            removeMealSlotUseCase.execute(slotId)
        }
    }

    fun goToToday() {
        _uiState.value = buildWeekState(currentWeekMonday())
        loadWeek()
    }

    fun goToDate(date: LocalDate) {
        _uiState.value = buildWeekState(mondayOf(date))
        loadWeek()
    }
}
