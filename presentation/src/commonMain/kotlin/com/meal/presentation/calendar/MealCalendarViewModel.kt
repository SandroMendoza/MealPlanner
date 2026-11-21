package com.meal.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.model.CalendarViewMode as DomainCalendarViewMode
import com.meal.domain.usecase.GetWeekNoteUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.RemoveMealSlotUseCase
import com.meal.domain.usecase.UpdateWeekNoteUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.sundayOf
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.uuid.ExperimentalUuidApi

private fun DomainCalendarViewMode.toPresentation(): CalendarViewMode = when (this) {
    DomainCalendarViewMode.DAY -> CalendarViewMode.DAY
    DomainCalendarViewMode.WEEK -> CalendarViewMode.WEEK
}

private fun buildWeekState(
    weekStart: LocalDate,
    selectedDate: LocalDate,
    lockPastDates: Boolean,
    viewMode: CalendarViewMode
): MealCalendarUiState {
    val weekEnd = weekEndFor(weekStart)
    val days = (0..6).map { weekStart.plus(it, DateTimeUnit.DAY) }
    return MealCalendarUiState(
        weekStart = weekStart,
        weekEnd = weekEnd,
        selectedDate = selectedDate,
        days = days,
        lockPastDates = lockPastDates,
        viewMode = viewMode
    )
}

@OptIn(ExperimentalUuidApi::class)
class MealCalendarViewModel(
    private val getWeeklyMealPlanUseCase: GetWeeklyMealPlanUseCase,
    private val removeMealSlotUseCase: RemoveMealSlotUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getWeekNoteUseCase: GetWeekNoteUseCase,
    private val updateWeekNoteUseCase: UpdateWeekNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        buildWeekState(currentWeekSunday(), today(), lockPastDates = false, viewMode = CalendarViewMode.DAY)
    )
    val state = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var noteJob: Job? = null
    private var defaultViewModeApplied = false

    // Saves run one at a time from this conflated flow, so they reach the database in typing
    // order and the latest text is always the last one written.
    private val pendingNote = MutableStateFlow<Pair<LocalDate, String>?>(null)

    init {
        loadWeek()
        loadNote()
        viewModelScope.launch {
            pendingNote.filterNotNull().collect { (weekStart, note) ->
                updateWeekNoteUseCase.execute(weekStart, note)
            }
        }
        viewModelScope.launch {
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    lockPastDates = settings.lockPastDates,
                    viewMode = if (!defaultViewModeApplied) {
                        settings.defaultCalendarView.toPresentation()
                    } else {
                        _uiState.value.viewMode
                    }
                )
                defaultViewModeApplied = true
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

    // The note is read once per week: the text field owns the value while the user types,
    // and replaying the saved note back into it would overwrite keystrokes typed since.
    private fun loadNote() {
        noteJob?.cancel()
        val weekStart = _uiState.value.weekStart
        noteJob = viewModelScope.launch {
            val note = getWeekNoteUseCase.execute(weekStart).first()
            _uiState.value = _uiState.value.copy(weekNote = note)
        }
    }

    fun previousWeek() = changeWeek(-7)

    fun nextWeek() = changeWeek(7)

    private fun changeWeek(deltaDays: Int) {
        val newStart = _uiState.value.weekStart.plus(deltaDays, DateTimeUnit.DAY)
        val newDays = (0..6).map { newStart.plus(it, DateTimeUnit.DAY) }
        val newSelectedDate = today().takeIf { it in newDays } ?: newStart
        _uiState.value = buildWeekState(newStart, newSelectedDate, _uiState.value.lockPastDates, _uiState.value.viewMode)
        loadWeek()
        loadNote()
    }

    fun removeSlot(entry: MealPlanEntry) {
        if (_uiState.value.isLocked(entry.date)) return
        viewModelScope.launch {
            removeMealSlotUseCase.execute(entry.id)
        }
    }

    fun requestRemoveSlot(entry: MealPlanEntry) {
        if (_uiState.value.isLocked(entry.date)) return
        _uiState.value = _uiState.value.copy(entryPendingRemoval = entry)
    }

    fun confirmRemoveSlot() {
        _uiState.value.entryPendingRemoval?.let(::removeSlot)
        _uiState.value = _uiState.value.copy(entryPendingRemoval = null)
    }

    fun dismissRemoveSlot() {
        _uiState.value = _uiState.value.copy(entryPendingRemoval = null)
    }

    fun goToToday() {
        _uiState.value = buildWeekState(currentWeekSunday(), today(), _uiState.value.lockPastDates, _uiState.value.viewMode)
        loadWeek()
        loadNote()
    }

    fun goToDate(date: LocalDate) {
        _uiState.value = buildWeekState(sundayOf(date), date, _uiState.value.lockPastDates, _uiState.value.viewMode)
        loadWeek()
        loadNote()
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun setViewMode(mode: CalendarViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
    }

    fun updateWeekNote(note: String) {
        val weekStart = _uiState.value.weekStart
        _uiState.value = _uiState.value.copy(weekNote = note)
        pendingNote.value = weekStart to note
    }
}
