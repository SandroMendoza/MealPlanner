package com.meal.domain.usecase

import com.meal.domain.model.enums.MealType
import com.meal.domain.repository.MealPlanRepository
import kotlinx.datetime.LocalDate

class RemoveMealSlotUseCase(
    private val repository: MealPlanRepository
) {
    suspend fun execute(date: LocalDate, mealType: MealType) =
        repository.removeMealSlot(date, mealType)
}
