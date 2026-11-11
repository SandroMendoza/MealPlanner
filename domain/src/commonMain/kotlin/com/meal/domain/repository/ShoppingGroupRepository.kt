package com.meal.domain.repository

import com.meal.domain.model.ShoppingGroup
import kotlinx.coroutines.flow.Flow

interface ShoppingGroupRepository {
    fun getGroups(): Flow<List<ShoppingGroup>>
    suspend fun createGroup(name: String): ShoppingGroup
    suspend fun renameGroup(id: String, name: String)
    suspend fun deleteGroup(id: String)
    suspend fun assignItemToGroup(itemKey: String, groupId: String?)
}
