package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingListRepository
import kotlinx.datetime.LocalDate

class RecordItemPriceUseCase(
    private val repository: ShoppingListRepository
) {
    suspend fun execute(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, price: Double) =
        repository.setItemPrice(itemKey, rangeStart, rangeEnd, price)
}
