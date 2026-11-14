package com.meal.data.repository

import com.meal.data.datasource.MealSlotLocalDataSource
import com.meal.data.inMemoryDatabase
import com.meal.domain.model.MealSlot
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class MealPlanRepositoryImplTest {

    private val repository = MealPlanRepositoryImpl(MealSlotLocalDataSource(inMemoryDatabase(), Dispatchers.Unconfined))

    private val weekStart = LocalDate(2026, 11, 8)
    private val weekEnd = LocalDate(2026, 11, 14)
    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    @Test
    fun addedSlotIsReturnedForItsWeek() = runTest {
        val recipeId = Uuid.random()

        repository.addRecipeToSlot(LocalDate(2026, 11, 10), MealType.LUNCH, recipeId)
        val slots = repository.getMealSlotsForWeek(weekStart, weekEnd).first()

        assertEquals(1, slots.size)
        assertEquals(LocalDate(2026, 11, 10), slots.single().date)
        assertEquals(MealType.LUNCH, slots.single().mealType)
        assertEquals(recipeId, slots.single().recipeId)
    }

    @Test
    fun weekQueryIncludesBothEndsAndExcludesOtherWeeks() = runTest {
        repository.addRecipeToSlot(weekStart, MealType.BREAKFAST, Uuid.random())
        repository.addRecipeToSlot(weekEnd, MealType.DINNER, Uuid.random())
        repository.addRecipeToSlot(weekStart.minus(1, DateTimeUnit.DAY), MealType.DINNER, Uuid.random())
        repository.addRecipeToSlot(weekEnd.plus(1, DateTimeUnit.DAY), MealType.DINNER, Uuid.random())

        val dates = repository.getMealSlotsForWeek(weekStart, weekEnd).first().map { it.date }.toSet()

        assertEquals(setOf(weekStart, weekEnd), dates)
    }

    @Test
    fun sameMealCanHoldSeveralRecipes() = runTest {
        repository.addRecipeToSlot(weekStart, MealType.DINNER, Uuid.random())
        repository.addRecipeToSlot(weekStart, MealType.DINNER, Uuid.random())

        assertEquals(2, repository.getMealSlotsForWeek(weekStart, weekEnd).first().size)
    }

    @Test
    fun replaceSwapsTheRecipeAndKeepsTheSlot() = runTest {
        repository.addRecipeToSlot(weekStart, MealType.LUNCH, Uuid.random())
        val slot = repository.getMealSlotsForWeek(weekStart, weekEnd).first().single()
        val newRecipe = Uuid.random()

        repository.replaceRecipeInSlot(slot.id, newRecipe)
        val replaced = repository.getMealSlotsForWeek(weekStart, weekEnd).first().single()

        assertEquals(slot.id, replaced.id)
        assertEquals(newRecipe, replaced.recipeId)
    }

    @Test
    fun removeDeletesOnlyThatSlot() = runTest {
        repository.addRecipeToSlot(weekStart, MealType.LUNCH, Uuid.random())
        repository.addRecipeToSlot(weekStart, MealType.DINNER, Uuid.random())
        val lunch = repository.getMealSlotsForWeek(weekStart, weekEnd).first().first { it.mealType == MealType.LUNCH }

        repository.removeMealSlot(lunch.id)

        assertEquals(listOf(MealType.DINNER), repository.getMealSlotsForWeek(weekStart, weekEnd).first().map { it.mealType })
    }

    @Test
    fun futureSlotDatesStartTodayAreDistinctAndSorted() = runTest {
        val recipeId = Uuid.random()
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        repository.addRecipeToSlot(today.minus(1, DateTimeUnit.DAY), MealType.LUNCH, recipeId)
        repository.addRecipeToSlot(tomorrow, MealType.LUNCH, recipeId)
        repository.addRecipeToSlot(today, MealType.DINNER, recipeId)
        repository.addRecipeToSlot(today, MealType.LUNCH, recipeId)
        repository.addRecipeToSlot(tomorrow, MealType.DINNER, Uuid.random())

        assertEquals(listOf(today, tomorrow), repository.getFutureSlotDatesForRecipe(recipeId))
    }

    @Test
    fun removingFutureSlotsKeepsPastOnesAndOtherRecipes() = runTest {
        val recipeId = Uuid.random()
        val otherRecipe = Uuid.random()
        val yesterday = today.minus(1, DateTimeUnit.DAY)
        repository.addRecipeToSlot(yesterday, MealType.LUNCH, recipeId)
        repository.addRecipeToSlot(today, MealType.LUNCH, recipeId)
        repository.addRecipeToSlot(today.plus(3, DateTimeUnit.DAY), MealType.LUNCH, recipeId)
        repository.addRecipeToSlot(today, MealType.DINNER, otherRecipe)

        repository.removeSlotsForRecipeOnOrAfter(recipeId)
        val remaining = repository.getMealSlotsForWeek(yesterday, today.plus(7, DateTimeUnit.DAY)).first()

        assertEquals(
            setOf(yesterday to recipeId, today to otherRecipe),
            remaining.map { it.date to it.recipeId }.toSet()
        )
    }

    @Test
    fun weekFlowEmitsAgainAfterAWrite() = runTest {
        val emissions = mutableListOf<List<MealSlot>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getMealSlotsForWeek(weekStart, weekEnd).toList(emissions)
        }
        runCurrent()

        repository.addRecipeToSlot(weekStart, MealType.SNACK, Uuid.random())
        runCurrent()

        assertTrue(emissions.first().isEmpty())
        assertEquals(1, emissions.last().size)
    }
}
