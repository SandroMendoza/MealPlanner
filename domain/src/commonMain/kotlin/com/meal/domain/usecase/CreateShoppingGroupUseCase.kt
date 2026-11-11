package com.meal.domain.usecase

import com.meal.domain.model.ShoppingGroup
import com.meal.domain.repository.ShoppingGroupRepository

class CreateShoppingGroupUseCase(
    private val repository: ShoppingGroupRepository
) {
    suspend fun execute(name: String): ShoppingGroup = repository.createGroup(name.trim())
}
