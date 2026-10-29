package com.meal.data.repository

import com.meal.data.datasource.MealSlotLocalDataSource
import com.meal.data.mappers.MealSlotMapper
import com.meal.domain.model.MealSlot
import com.meal.domain.model.enums.MealType
import com.meal.domain.repository.MealPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
class MealPlanRepositoryImpl(
    private val localDataSource: MealSlotLocalDataSource
) : MealPlanRepository {

    private fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    override fun getMealSlotsForWeek(weekStart: LocalDate, weekEnd: LocalDate): Flow<List<MealSlot>> =
        localDataSource.slotsForWeek(weekStart, weekEnd).map { MealSlotMapper.toDomain(it) }

    override suspend fun assignRecipeToSlot(date: LocalDate, mealType: MealType, recipeId: Uuid) {
        localDataSource.assignRecipeToSlot(date, mealType, recipeId)
    }

    override suspend fun removeMealSlot(date: LocalDate, mealType: MealType) {
        localDataSource.removeSlot(date, mealType)
    }

    override suspend fun getFutureSlotDatesForRecipe(recipeId: Uuid): List<LocalDate> =
        localDataSource.getFutureSlotDatesForRecipe(recipeId, today())

    override suspend fun removeSlotsForRecipeOnOrAfter(recipeId: Uuid) {
        localDataSource.removeSlotsForRecipeOnOrAfter(recipeId, today())
    }
}
