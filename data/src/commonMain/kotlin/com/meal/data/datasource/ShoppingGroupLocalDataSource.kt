package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.db.ShoppingGroupEntity
import com.meal.domain.model.ShoppingGroup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ShoppingGroupLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val groupQueries = database.shoppingGroupQueries
    private val assignmentQueries = database.shoppingItemGroupQueries

    fun groups(): Flow<List<ShoppingGroup>> =
        groupQueries.selectAllGroups()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> rows.map { toDomain(it) } }

    fun assignments(): Flow<Map<String, String>> =
        assignmentQueries.selectAllAssignments()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> rows.associate { it.itemKey to it.groupId } }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun createGroup(name: String): ShoppingGroup = withContext(ioDispatcher) {
        val nextSortOrder = (groupQueries.selectMaxSortOrder().executeAsOne().maxSortOrder ?: -1) + 1
        val group = ShoppingGroup(id = Uuid.random().toString(), name = name, sortOrder = nextSortOrder.toInt())
        groupQueries.insertGroup(group.id, group.name, group.sortOrder.toLong())
        group
    }

    suspend fun renameGroup(id: String, name: String) = withContext(ioDispatcher) {
        groupQueries.renameGroup(name, id)
    }

    suspend fun deleteGroup(id: String) = withContext(ioDispatcher) {
        database.transaction {
            assignmentQueries.clearAssignmentsForGroup(id)
            groupQueries.deleteGroup(id)
        }
    }

    suspend fun assignItem(itemKey: String, groupId: String?) = withContext(ioDispatcher) {
        if (groupId == null) {
            assignmentQueries.clearItemGroup(itemKey)
        } else {
            assignmentQueries.setItemGroup(itemKey, groupId)
        }
    }

    private fun toDomain(row: ShoppingGroupEntity) = ShoppingGroup(
        id = row.id,
        name = row.name,
        sortOrder = row.sortOrder.toInt()
    )
}
