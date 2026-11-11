package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingGroupRepository

class AssignItemToGroupUseCase(
    private val repository: ShoppingGroupRepository
) {
    suspend fun execute(itemKey: String, groupId: String?) = repository.assignItemToGroup(itemKey, groupId)
}
