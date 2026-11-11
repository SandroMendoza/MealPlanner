package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.db.SelectPricedItemsInRange
import com.meal.data.db.ShoppingListItemEntity
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.SpendingLineItem
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class ShoppingListLocalDataSource(
    private val database: MealPlannerDatabase,
    private val groupLocalDataSource: ShoppingGroupLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val queries = database.shoppingListItemQueries

    fun items(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<ShoppingListItem>> =
        combine(
            queries.selectActiveItems(rangeStart.toString(), rangeEnd.toString()).asFlow().mapToList(ioDispatcher),
            groupLocalDataSource.assignments()
        ) { rows, assignments -> rows.map { toDomain(it, assignments[it.itemKey]) } }

    suspend fun syncGenerated(rangeStart: LocalDate, rangeEnd: LocalDate, required: List<ShoppingListItem>) =
        withContext(ioDispatcher) {
            val existingKeys = queries.selectKeysForRange(rangeStart.toString(), rangeEnd.toString()).executeAsList().toSet()
            val missing = required.filter { it.itemKey !in existingKeys }
            if (missing.isNotEmpty()) {
                database.transaction {
                    missing.forEach { item ->
                        queries.insertItem(
                            itemKey = item.itemKey,
                            rangeStart = rangeStart.toString(),
                            rangeEnd = rangeEnd.toString(),
                            name = item.name,
                            quantity = item.totalQuantity,
                            unit = item.unit.name,
                            checked = 0
                        )
                    }
                }
            }
        }

    suspend fun setChecked(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, checked: Boolean) =
        withContext(ioDispatcher) {
            queries.setChecked(if (checked) 1 else 0, itemKey, rangeStart.toString(), rangeEnd.toString())
        }

    suspend fun addManual(item: ShoppingListItem, rangeStart: LocalDate, rangeEnd: LocalDate) =
        withContext(ioDispatcher) {
            queries.insertItem(
                itemKey = item.itemKey,
                rangeStart = rangeStart.toString(),
                rangeEnd = rangeEnd.toString(),
                name = item.name,
                quantity = item.totalQuantity,
                unit = item.unit.name,
                checked = 0
            )
        }

    suspend fun remove(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate) =
        withContext(ioDispatcher) {
            queries.setRemoved(itemKey, rangeStart.toString(), rangeEnd.toString())
        }

    suspend fun setItemPrice(itemKey: String, rangeStart: LocalDate, rangeEnd: LocalDate, price: Double) =
        withContext(ioDispatcher) {
            queries.setItemPrice(price, itemKey, rangeStart.toString(), rangeEnd.toString())
        }

    fun pricedItems(rangeStart: LocalDate, rangeEnd: LocalDate): Flow<List<SpendingLineItem>> =
        queries.selectPricedItemsInRange(rangeEnd = rangeEnd.toString(), rangeStart = rangeStart.toString())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> rows.map { toSpendingLineItem(it) } }

    private fun toDomain(row: ShoppingListItemEntity, groupId: String?) = ShoppingListItem(
        itemKey = row.itemKey,
        name = row.name,
        totalQuantity = row.quantity,
        unit = MeasurementUnit.valueOf(row.unit),
        checked = row.checked != 0L,
        price = row.price,
        groupId = groupId
    )

    private fun toSpendingLineItem(row: SelectPricedItemsInRange) = SpendingLineItem(
        itemKey = row.itemKey,
        name = row.name,
        totalQuantity = row.quantity,
        unit = MeasurementUnit.valueOf(row.unit),
        price = row.price,
        rangeStart = LocalDate.parse(row.rangeStart),
        rangeEnd = LocalDate.parse(row.rangeEnd)
    )
}
