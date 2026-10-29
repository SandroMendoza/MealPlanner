package com.meal.domain.usecase

import com.meal.domain.model.Recipe
import com.meal.domain.repository.RecipeRepository

class UpdateRecipeUseCase(
    private val repository: RecipeRepository
) {
    suspend fun execute(recipe: Recipe) = repository.updateRecipe(recipe)
}
