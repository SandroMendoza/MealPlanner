package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingListRepository
import kotlinx.datetime.LocalDate

class ToggleShoppingListItemUseCase(
    private val repository: ShoppingListRepository
) {
    suspend fun execute(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, checked: Boolean) =
        repository.setItemChecked(itemKey, rangeStart, rangeEnd, checked)
}
