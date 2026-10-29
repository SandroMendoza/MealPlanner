package com.meal.domain.usecase

import com.meal.domain.repository.RecipeRepository

class GetAllRecipesUseCase(
    private val repository: RecipeRepository
) {
    fun execute() = repository.getAllRecipes()
}
