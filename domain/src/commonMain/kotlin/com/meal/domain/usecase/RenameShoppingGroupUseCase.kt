package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingGroupRepository

class RenameShoppingGroupUseCase(
    private val repository: ShoppingGroupRepository
) {
    suspend fun execute(id: String, name: String) = repository.renameGroup(id, name.trim())
}
