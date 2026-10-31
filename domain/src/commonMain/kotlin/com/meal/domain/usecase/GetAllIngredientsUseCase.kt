package com.meal.domain.usecase

import com.meal.domain.repository.IngredientRepository

class GetAllIngredientsUseCase(
    private val repository: IngredientRepository
) {
    fun execute() = repository.getAllIngredients()
}
