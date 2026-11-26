package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingListRepository

class SetShoppingItemsAtHomeUseCase(
    private val repository: ShoppingListRepository
) {
    suspend fun execute(names: Collection<String>, atHome: Boolean) {
        if (names.isEmpty()) return
        repository.setItemsAtHome(names, atHome)
    }
}
