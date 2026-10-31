package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingListRepository
import kotlinx.datetime.LocalDate

class RemoveShoppingListItemUseCase(
    private val repository: ShoppingListRepository
) {
    suspend fun execute(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate) =
        repository.removeItem(itemKey, rangeStart, rangeEnd)
}
