package com.meal.domain.usecase

import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.datetime.LocalDate

class UpdateShoppingListItemAmountUseCase(
    private val repository: ShoppingListRepository
) {
    /** Changes the item's amount and returns true, or returns false when [quantity] isn't above 0. */
    suspend fun execute(itemKey: String, quantity: Double, unit: MeasurementUnit, rangeStart: LocalDate, rangeEnd: LocalDate): Boolean {
        if (quantity <= 0.0) return false
        repository.updateItemAmount(itemKey, rangeStart, rangeEnd, quantity, unit)
        return true
    }
}
