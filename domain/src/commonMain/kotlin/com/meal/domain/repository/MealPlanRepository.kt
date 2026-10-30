package com.meal.domain.repository

import com.meal.domain.model.MealSlot
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface MealPlanRepository {
    fun getMealSlotsForWeek(weekStart: LocalDate, weekEnd: LocalDate): Flow<List<MealSlot>>
    suspend fun addRecipeToSlot(date: LocalDate, mealType: MealType, recipeId: Uuid)
    suspend fun replaceRecipeInSlot(slotId: Uuid, recipeId: Uuid)
    suspend fun removeMealSlot(slotId: Uuid)
    suspend fun getFutureSlotDatesForRecipe(recipeId: Uuid): List<LocalDate>
    suspend fun removeSlotsForRecipeOnOrAfter(recipeId: Uuid)
}
