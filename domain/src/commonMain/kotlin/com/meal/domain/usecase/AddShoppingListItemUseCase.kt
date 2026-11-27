package com.meal.domain.usecase

import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.ingredientDisplayName
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddShoppingListItemUseCase(
    private val repository: ShoppingListRepository
) {
    /**
     * Adds the item and returns the key of the list item that holds it. With no [quantity], or
     * one that isn't above 0, the item has no measurement and [unit] is ignored.
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(name: String, quantity: Double?, unit: MeasurementUnit?, rangeStart: LocalDate, rangeEnd: LocalDate): String {
        val measured = quantity != null && quantity > 0.0 && unit != null
        val item = ShoppingListItem(
            itemKey = Uuid.random().toString(),
            name = ingredientDisplayName(name),
            totalQuantity = if (measured) quantity else null,
            unit = if (measured) unit else null,
            checked = false
        )
        return repository.addManualItem(item, rangeStart, rangeEnd)
    }
}
