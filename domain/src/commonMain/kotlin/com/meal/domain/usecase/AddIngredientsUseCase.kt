package com.meal.domain.usecase

import com.meal.domain.repository.IngredientRepository

class AddIngredientsUseCase(
    private val repository: IngredientRepository
) {
    /** Returns how many of [names] were new ingredients. */
    suspend fun execute(names: List<String>): Int {
        val trimmed = names.map { it.trim() }.filter { it.isNotEmpty() }
        if (trimmed.isEmpty()) return 0
        return repository.addIngredients(trimmed)
    }
}
