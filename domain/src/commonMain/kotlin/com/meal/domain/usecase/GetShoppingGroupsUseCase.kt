package com.meal.domain.usecase

import com.meal.domain.model.ShoppingGroup
import com.meal.domain.repository.ShoppingGroupRepository
import kotlinx.coroutines.flow.Flow

class GetShoppingGroupsUseCase(
    private val repository: ShoppingGroupRepository
) {
    fun execute(): Flow<List<ShoppingGroup>> = repository.getGroups()
}
