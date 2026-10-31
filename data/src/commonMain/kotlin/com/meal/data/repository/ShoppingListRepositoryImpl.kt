package com.meal.data.repository

import com.meal.data.datasource.ShoppingListLocalDataSource
import com.meal.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow

class ShoppingListRepositoryImpl(
    private val localDataSource: ShoppingListLocalDataSource
) : ShoppingListRepository {

    override fun getCheckedItemKeys(): Flow<Set<String>> =
        localDataSource.checkedKeys()

    override suspend fun setItemChecked(itemKey: String, checked: Boolean) {
        localDataSource.setItemChecked(itemKey, checked)
    }
}
