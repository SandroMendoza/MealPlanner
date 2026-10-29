package com.meal.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.ToggleShoppingListItemUseCase
import com.meal.presentation.common.currentWeekMonday
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val generateShoppingListUseCase: GenerateShoppingListUseCase,
    private val toggleShoppingListItemUseCase: ToggleShoppingListItemUseCase
) : ViewModel() {

    private val weekStart = currentWeekMonday()
    private val weekEnd = weekEndFor(weekStart)

    private val _uiState = MutableStateFlow(ShoppingListUiState(weekStart = weekStart, weekEnd = weekEnd))
    val state = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                generateShoppingListUseCase.execute(weekStart, weekEnd).collect { items ->
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
            toggleShoppingListItemUseCase.execute(weekStart, itemKey, checked)
        }
    }
}
