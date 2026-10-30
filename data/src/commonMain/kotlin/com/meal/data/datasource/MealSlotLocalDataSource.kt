package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.model.MealSlotDto
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MealSlotLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val queries = database.mealSlotQueries

    fun slotsForWeek(startDate: LocalDate, endDate: LocalDate): Flow<List<MealSlotDto>> =
        queries.selectSlotsForWeek(startDate.toString(), endDate.toString())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows ->
                rows.map { row ->
                    MealSlotDto(
                        id = Uuid.parse(row.id),
                        date = LocalDate.parse(row.date),
                        mealType = MealType.valueOf(row.mealType),
                        recipeId = Uuid.parse(row.recipeId)
                    )
                }
            }

    suspend fun addRecipeToSlot(date: LocalDate, mealType: MealType, recipeId: Uuid) = withContext(ioDispatcher) {
        queries.insertSlot(Uuid.random().toString(), date.toString(), mealType.name, recipeId.toString())
    }

    suspend fun replaceRecipeInSlot(slotId: Uuid, recipeId: Uuid) = withContext(ioDispatcher) {
        queries.updateSlotRecipe(recipeId.toString(), slotId.toString())
    }

    suspend fun removeSlot(slotId: Uuid) = withContext(ioDispatcher) {
        queries.deleteSlotById(slotId.toString())
    }

    suspend fun getFutureSlotDatesForRecipe(recipeId: Uuid, fromDate: LocalDate): List<LocalDate> =
        withContext(ioDispatcher) {
            queries.selectFutureSlotDatesForRecipe(recipeId.toString(), fromDate.toString())
                .executeAsList()
                .map { LocalDate.parse(it) }
        }

    suspend fun removeSlotsForRecipeOnOrAfter(recipeId: Uuid, fromDate: LocalDate) = withContext(ioDispatcher) {
        queries.deleteSlotsForRecipeOnOrAfter(recipeId.toString(), fromDate.toString())
    }
}
