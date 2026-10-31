package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ShoppingListLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val queries = database.shoppingListCheckedItemQueries

    fun checkedKeys(): Flow<Set<String>> =
        queries.selectCheckedKeys()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { it.toSet() }

    suspend fun setItemChecked(itemKey: String, checked: Boolean) =
        withContext(ioDispatcher) {
            if (checked) {
                queries.setChecked(itemKey)
            } else {
                queries.setUnchecked(itemKey)
            }
        }
}
