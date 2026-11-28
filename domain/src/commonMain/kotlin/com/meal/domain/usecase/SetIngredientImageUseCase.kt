package com.meal.domain.usecase

import com.meal.domain.repository.IngredientRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class SetIngredientImageUseCase(
    private val repository: IngredientRepository
) {
    /** A null [imageUri] goes back to the built-in picture. */
    suspend fun execute(id: Uuid, imageUri: String?) = repository.setImage(id, imageUri)
}
