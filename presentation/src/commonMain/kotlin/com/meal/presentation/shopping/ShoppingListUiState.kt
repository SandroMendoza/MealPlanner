package com.meal.presentation.shopping

import com.meal.domain.model.ShoppingListItem
import kotlinx.datetime.LocalDate

enum class ShoppingListRangeMode { WEEK, RANGE, DAY }

data class ShoppingListUiState(
    val mode: ShoppingListRangeMode,
    val rangeStart: LocalDate,
    val rangeEnd: LocalDate,
    val items: List<ShoppingListItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty() && errorMessage == null
}
