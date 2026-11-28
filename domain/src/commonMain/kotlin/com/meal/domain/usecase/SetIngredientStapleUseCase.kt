package com.meal.domain.usecase

import com.meal.domain.repository.IngredientRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class SetIngredientStapleUseCase(
    private val repository: IngredientRepository
) {
    suspend fun execute(id: Uuid, isStaple: Boolean) = repository.setStaple(id, isStaple)
}
