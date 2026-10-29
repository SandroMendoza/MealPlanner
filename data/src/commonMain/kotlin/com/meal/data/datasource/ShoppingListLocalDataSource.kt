package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class ShoppingListLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val queries = database.shoppingListCheckedItemQueries

    fun checkedKeysForWeek(weekStart: LocalDate): Flow<Set<String>> =
        queries.selectCheckedKeysForWeek(weekStart.toString())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { it.toSet() }

    suspend fun setItemChecked(weekStart: LocalDate, itemKey: String, checked: Boolean) =
        withContext(ioDispatcher) {
            if (checked) {
                queries.setChecked(weekStart.toString(), itemKey)
            } else {
                queries.setUnchecked(weekStart.toString(), itemKey)
            }
        }
}
