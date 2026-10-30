package com.meal.domain.usecase

import com.meal.domain.repository.RecipeRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ToggleFavoriteUseCase(
    private val repository: RecipeRepository
) {
    suspend fun execute(id: Uuid, isFavorite: Boolean) = repository.setFavorite(id, isFavorite)
}
