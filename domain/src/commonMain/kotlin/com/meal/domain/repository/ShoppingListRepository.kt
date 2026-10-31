package com.meal.domain.repository

import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getCheckedItemKeys(): Flow<Set<String>>
    suspend fun setItemChecked(itemKey: String, checked: Boolean)
}
