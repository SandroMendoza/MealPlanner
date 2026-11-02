package com.meal.presentation.spending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetSpendingReportUseCase
import com.meal.presentation.common.currentWeekMonday
import com.meal.presentation.common.mondayOf
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import com.meal.presentation.shopping.ShoppingListRangeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class SpendingReportsViewModel(
    private val getSpendingReportUseCase: GetSpendingReportUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val initialWeekStart = currentWeekMonday()
    private val _uiState = MutableStateFlow(
        SpendingReportsUiState(
            mode = ShoppingListRangeMode.WEEK,
            rangeStart = initialWeekStart,
            rangeEnd = weekEndFor(initialWeekStart)
        )
    )
    val state = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadReport()
        viewModelScope.launch {
            getSettingsUseCase.execute().collect { settings ->
                _uiState.value = _uiState.value.copy(currencySymbol = settings.currency.symbol)
            }
        }
    }

    private fun loadReport() {
        loadJob?.cancel()
        val rangeStart = _uiState.value.rangeStart
        val rangeEnd = _uiState.value.rangeEnd
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                getSpendingReportUseCase.execute(rangeStart, rangeEnd).collect { report ->
                    _uiState.value = _uiState.value.copy(report = report, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load spending report: ${e.message}"
                )
            }
        }
    }

    fun previousWeek() = shiftWeek(-7)

    fun nextWeek() = shiftWeek(7)

    private fun shiftWeek(deltaDays: Int) {
        val newStart = _uiState.value.rangeStart.plus(deltaDays, DateTimeUnit.DAY)
        setRange(ShoppingListRangeMode.WEEK, newStart, weekEndFor(newStart))
    }

    fun previousDay() = shiftDay(-1)

    fun nextDay() = shiftDay(1)

    private fun shiftDay(deltaDays: Int) {
        val newDate = _uiState.value.rangeStart.plus(deltaDays, DateTimeUnit.DAY)
        setRange(ShoppingListRangeMode.DAY, newDate, newDate)
    }

    fun selectMode(mode: ShoppingListRangeMode) {
        if (mode == _uiState.value.mode) return
        when (mode) {
            ShoppingListRangeMode.WEEK -> {
                val start = currentWeekMonday()
                setRange(ShoppingListRangeMode.WEEK, start, weekEndFor(start))
            }
            ShoppingListRangeMode.DAY -> {
                val date = today()
                setRange(ShoppingListRangeMode.DAY, date, date)
            }
            ShoppingListRangeMode.RANGE -> {
                val start = currentWeekMonday()
                setRange(ShoppingListRangeMode.RANGE, start, weekEndFor(start))
            }
        }
    }

    fun goToToday() {
        when (_uiState.value.mode) {
            ShoppingListRangeMode.WEEK -> {
                val start = currentWeekMonday()
                setRange(ShoppingListRangeMode.WEEK, start, weekEndFor(start))
            }
            ShoppingListRangeMode.DAY -> {
                val date = today()
                setRange(ShoppingListRangeMode.DAY, date, date)
            }
            ShoppingListRangeMode.RANGE -> Unit
        }
    }

    fun goToDate(date: LocalDate) {
        when (_uiState.value.mode) {
            ShoppingListRangeMode.WEEK -> {
                val start = mondayOf(date)
                setRange(ShoppingListRangeMode.WEEK, start, weekEndFor(start))
            }
            ShoppingListRangeMode.DAY -> setRange(ShoppingListRangeMode.DAY, date, date)
            ShoppingListRangeMode.RANGE -> Unit
        }
    }

    fun selectRange(start: LocalDate, end: LocalDate) {
        val rangeStart = if (start <= end) start else end
        val rangeEnd = if (start <= end) end else start
        setRange(ShoppingListRangeMode.RANGE, rangeStart, rangeEnd)
    }

    private fun setRange(mode: ShoppingListRangeMode, start: LocalDate, end: LocalDate) {
        _uiState.value = _uiState.value.copy(mode = mode, rangeStart = start, rangeEnd = end)
        loadReport()
    }
}
