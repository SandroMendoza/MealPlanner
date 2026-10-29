package com.meal.domain.usecase

import com.meal.domain.model.enums.MealType
import com.meal.domain.repository.MealPlanRepository
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AssignRecipeToSlotUseCase(
    private val repository: MealPlanRepository
) {
    suspend fun execute(date: LocalDate, mealType: MealType, recipeId: Uuid) =
        repository.assignRecipeToSlot(date, mealType, recipeId)
}
