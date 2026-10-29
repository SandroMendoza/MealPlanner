package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingListRepository
import kotlinx.datetime.LocalDate

class ToggleShoppingListItemUseCase(
    private val repository: ShoppingListRepository
) {
    suspend fun execute(weekStart: LocalDate, itemKey: String, checked: Boolean) =
        repository.setItemChecked(weekStart, itemKey, checked)
}
