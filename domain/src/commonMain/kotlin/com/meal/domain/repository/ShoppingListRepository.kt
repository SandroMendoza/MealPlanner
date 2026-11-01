package com.meal.domain.repository

import com.meal.domain.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ShoppingListRepository {
    fun getItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<ShoppingListItem>>
    suspend fun syncGeneratedItems(rangeStart: LocalDate, rangeEnd: LocalDate, required: List<ShoppingListItem>)
    suspend fun setItemChecked(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, checked: Boolean)
    suspend fun addManualItem(item: ShoppingListItem, rangeStart: LocalDate, rangeEnd: LocalDate)
    suspend fun removeItem(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate)
    suspend fun setItemPrice(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, price: Double)
}
