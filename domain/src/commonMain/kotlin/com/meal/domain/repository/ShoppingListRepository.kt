package com.meal.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ShoppingListRepository {
    fun getCheckedItemKeys(weekStart: LocalDate): Flow<Set<String>>
    suspend fun setItemChecked(weekStart: LocalDate, itemKey: String, checked: Boolean)
}
