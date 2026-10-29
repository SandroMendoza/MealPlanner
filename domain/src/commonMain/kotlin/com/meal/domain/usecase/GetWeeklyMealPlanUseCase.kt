package com.meal.domain.usecase

import com.meal.domain.model.MealPlanEntry
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class GetWeeklyMealPlanUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) {
    fun execute(weekStart: LocalDate, weekEnd: LocalDate): Flow<List<MealPlanEntry>> =
        mealPlanRepository.getMealSlotsForWeek(weekStart, weekEnd).map { slots ->
            slots.mapNotNull { slot ->
                recipeRepository.getRecipeById(slot.recipeId).first()?.let { recipe ->
                    MealPlanEntry(slot.date, slot.mealType, recipe)
                }
            }
        }
}
