package com.meal.data.repository

import com.meal.data.datasource.ShoppingListLocalDataSource
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class ShoppingListRepositoryImpl(
    private val localDataSource: ShoppingListLocalDataSource
) : ShoppingListRepository {

    override fun getCheckedItemKeys(weekStart: LocalDate): Flow<Set<String>> =
        localDataSource.checkedKeysForWeek(weekStart)

    override suspend fun setItemChecked(weekStart: LocalDate, itemKey: String, checked: Boolean) {
        localDataSource.setItemChecked(weekStart, itemKey, checked)
    }
}
