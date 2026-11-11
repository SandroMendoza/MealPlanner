package com.meal.data.repository

import com.meal.data.datasource.ShoppingGroupLocalDataSource
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.repository.ShoppingGroupRepository
import kotlinx.coroutines.flow.Flow

class ShoppingGroupRepositoryImpl(
    private val localDataSource: ShoppingGroupLocalDataSource
) : ShoppingGroupRepository {

    override fun getGroups(): Flow<List<ShoppingGroup>> = localDataSource.groups()

    override suspend fun createGroup(name: String): ShoppingGroup = localDataSource.createGroup(name)

    override suspend fun renameGroup(id: String, name: String) {
        localDataSource.renameGroup(id, name)
    }

    override suspend fun deleteGroup(id: String) {
        localDataSource.deleteGroup(id)
    }

    override suspend fun assignItemToGroup(itemKey: String, groupId: String?) {
        localDataSource.assignItem(itemKey, groupId)
    }
}
