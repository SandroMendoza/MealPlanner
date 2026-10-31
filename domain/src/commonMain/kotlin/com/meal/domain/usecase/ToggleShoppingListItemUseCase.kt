package com.meal.domain.usecase

import com.meal.domain.repository.ShoppingListRepository

class ToggleShoppingListItemUseCase(
    private val repository: ShoppingListRepository
) {
    suspend fun execute(itemKey: String, checked: Boolean) =
        repository.setItemChecked(itemKey, checked)
}
