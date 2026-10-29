package com.meal.presentation.shopping

import com.meal.domain.model.ShoppingListItem
import kotlinx.datetime.LocalDate

data class ShoppingListUiState(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val items: List<ShoppingListItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty() && errorMessage == null
}
