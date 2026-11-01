package com.meal.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.usecase.AddShoppingListItemUseCase
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.RecordItemPriceUseCase
import com.meal.domain.usecase.RemoveShoppingListItemUseCase
import com.meal.domain.usecase.ToggleShoppingListItemUseCase
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

class ShoppingListViewModel(
    private val generateShoppingListUseCase: GenerateShoppingListUseCase,
    private val toggleShoppingListItemUseCase: ToggleShoppingListItemUseCase,
    private val addShoppingListItemUseCase: AddShoppingListItemUseCase,
    private val removeShoppingListItemUseCase: RemoveShoppingListItemUseCase,
    private val recordItemPriceUseCase: RecordItemPriceUseCase,
    private val getAllIngredientsUseCase: GetAllIngredientsUseCase
) : ViewModel() {

    private val initialWeekStart = currentWeekMonday()
    private val _uiState = MutableStateFlow(
        ShoppingListUiState(
            mode = ShoppingListRangeMode.WEEK,
            rangeStart = initialWeekStart,
            rangeEnd = weekEndFor(initialWeekStart)
        )
    )
    val state = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadList()
        viewModelScope.launch {
            getAllIngredientsUseCase.execute().collect { list ->
                _uiState.value = _uiState.value.copy(availableIngredients = list)
            }
        }
    }

    private fun loadList() {
        loadJob?.cancel()
        val rangeStart = _uiState.value.rangeStart
        val rangeEnd = _uiState.value.rangeEnd
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                generateShoppingListUseCase.execute(rangeStart, rangeEnd).collect { items ->
                    _uiState.value = _uiState.value.copy(items = items, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load shopping list: ${e.message}"
                )
            }
        }
    }

    fun toggleItem(itemKey: String, checked: Boolean) {
        viewModelScope.launch {
            toggleShoppingListItemUseCase.execute(itemKey, _uiState.value.rangeStart, _uiState.value.rangeEnd, checked)
        }
    }

    fun removeItem(itemKey: String) {
        viewModelScope.launch {
            removeShoppingListItemUseCase.execute(itemKey, _uiState.value.rangeStart, _uiState.value.rangeEnd)
        }
    }

    fun addItem(name: String, quantity: Double, unit: MeasurementUnit) {
        viewModelScope.launch {
            addShoppingListItemUseCase.execute(name, quantity, unit, _uiState.value.rangeStart, _uiState.value.rangeEnd)
        }
        dismissAddItemDialog()
    }

    fun openAddItemDialog() {
        _uiState.value = _uiState.value.copy(showAddItemDialog = true)
    }

    fun dismissAddItemDialog() {
        _uiState.value = _uiState.value.copy(showAddItemDialog = false)
    }

    fun recordItemPrice(itemKey: String, price: Double) {
        viewModelScope.launch {
            recordItemPriceUseCase.execute(itemKey, _uiState.value.rangeStart, _uiState.value.rangeEnd, price)
        }
        dismissRecordPriceDialog()
    }

    fun openRecordPriceDialog(item: ShoppingListItem) {
        _uiState.value = _uiState.value.copy(itemBeingPriced = item)
    }

    fun dismissRecordPriceDialog() {
        _uiState.value = _uiState.value.copy(itemBeingPriced = null)
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
        loadList()
    }
}
