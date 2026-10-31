package com.meal.data.repository

import com.meal.data.datasource.ShoppingListLocalDataSource
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class ShoppingListRepositoryImpl(
    private val localDataSource: ShoppingListLocalDataSource
) : ShoppingListRepository {

    override fun getItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<ShoppingListItem>> =
        localDataSource.items(rangeStart, rangeEnd)

    override suspend fun syncGeneratedItems(rangeStart: LocalDate, rangeEnd: LocalDate, required: List<ShoppingListItem>) {
        localDataSource.syncGenerated(rangeStart, rangeEnd, required)
    }

    override suspend fun setItemChecked(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, checked: Boolean) {
        localDataSource.setChecked(itemKey, rangeStart, rangeEnd, checked)
    }

    override suspend fun addManualItem(item: ShoppingListItem, rangeStart: LocalDate, rangeEnd: LocalDate) {
        localDataSource.addManual(item, rangeStart, rangeEnd)
    }

    override suspend fun removeItem(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate) {
        localDataSource.remove(itemKey, rangeStart, rangeEnd)
    }
}
