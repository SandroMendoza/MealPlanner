package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingGroupRepository

class DeleteShoppingGroupUseCase(
    private val repository: ShoppingGroupRepository
) {
    suspend fun execute(id: String) = repository.deleteGroup(id)
}
