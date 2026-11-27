package com.meal.domain.repository

import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.SpendingLineItem
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ShoppingListRepository {
    fun getItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<ShoppingListItem>>
    suspend fun syncGeneratedItems(rangeStart: LocalDate, rangeEnd: LocalDate, required: List<ShoppingListItem>)
    suspend fun setItemChecked(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, checked: Boolean)
    /**
     * Adds [item] to the range's list. When the list already has an item with the same name
     * (ignoring case) and unit, [item]'s quantity is added to that item instead of a new row.
     * Returns the key of the item that now holds it.
     */
    suspend fun addManualItem(item: ShoppingListItem, rangeStart: LocalDate, rangeEnd: LocalDate): String
    /** Changes the item's quantity and unit for this range only; its key, group, price and checked state stay. */
    suspend fun updateItemAmount(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, quantity: Double, unit: MeasurementUnit)
    /** Puts the named items in the "Pantry staples" section, or back among the items to buy, for every list. */
    suspend fun setItemsAtHome(names: Collection<String>, atHome: Boolean)
    suspend fun removeItem(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate)
    suspend fun setItemPrice(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, price: Double)
    fun getPricedItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<SpendingLineItem>>
}
