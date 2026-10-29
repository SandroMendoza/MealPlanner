package com.meal.domain.usecase

import com.meal.domain.repository.RecipeRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetRecipeByIdUseCase(
    private val repository: RecipeRepository
) {
    fun execute(id: Uuid) = repository.getRecipeById(id)
}
