package com.meal.domain.usecase

import com.meal.domain.repository.MealPlanRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ReplaceRecipeInSlotUseCase(
    private val repository: MealPlanRepository
) {
    suspend fun execute(slotId: Uuid, recipeId: Uuid) =
        repository.replaceRecipeInSlot(slotId, recipeId)
}
