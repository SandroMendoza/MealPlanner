package com.meal.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.RemoveMealSlotUseCase
import com.meal.presentation.common.currentWeekMonday
import com.meal.presentation.common.mondayOf
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.uuid.ExperimentalUuidApi

private fun buildWeekState(weekStart: LocalDate, selectedDate: LocalDate, lockPastDates: Boolean): MealCalendarUiState {
    val weekEnd = weekEndFor(weekStart)
    val days = (0..6).map { weekStart.plus(it, DateTimeUnit.DAY) }
    return MealCalendarUiState(
        weekStart = weekStart,
        weekEnd = weekEnd,
        selectedDate = selectedDate,
        days = days,
        lockPastDates = lockPastDates
    )
}

@OptIn(ExperimentalUuidApi::class)
class MealCalendarViewModel(
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase,
    private val removeMealSlotUseCase: RemoveMealSlotUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildWeekState(currentWeekMonday(), today(), lockPastDates = false))
    val state = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadWeek()
        viewModelScope.launch {
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(lockPastDates = settings.lockPastDates)
            }
        }
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
        val newDays = (0..6).map { newStart.plus(it, DateTimeUnit.DAY) }
        val newSelectedDate = today().takeIf { it in newDays } ?: newStart
        _uiState.value = buildWeekState(newStart, newSelectedDate, _uiState.value.lockPastDates)
        loadWeek()
    }

    fun removeSlot(entry: MealPlanEntry) {
        if (_uiState.value.isLocked(entry.date)) return
        viewModelScope.launch {
            removeMealSlotUseCase.execute(entry.id)
        }
    }

    fun goToToday() {
        _uiState.value = buildWeekState(currentWeekMonday(), today(), _uiState.value.lockPastDates)
        loadWeek()
    }

    fun goToDate(date: LocalDate) {
        _uiState.value = buildWeekState(mondayOf(date), date, _uiState.value.lockPastDates)
        loadWeek()
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }
}
