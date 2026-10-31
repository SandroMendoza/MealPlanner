package com.meal.domain.usecase

import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddShoppingListItemUseCase(
    private val repository: ShoppingListRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(name: String, quantity: Double, unit: MeasurementUnit, rangeStart: LocalDate, rangeEnd: LocalDate) {
        val item = ShoppingListItem(
            itemKey = Uuid.random().toString(),
            name = name.trim(),
            totalQuantity = quantity,
            unit = unit,
            checked = false
        )
        repository.addManualItem(item, rangeStart, rangeEnd)
    }
}
