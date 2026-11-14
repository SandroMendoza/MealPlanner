@file:OptIn(ExperimentalUuidApi::class)

package com.meal.domain.usecase

import com.meal.domain.fakes.FakeMealPlanRepository
import com.meal.domain.fakes.FakeRecipeRepository
import com.meal.domain.fakes.recipe
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.MealSlot
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GetWeeklyMealPlanUseCaseTest {

    private val weekStart = LocalDate(2026, 11, 8)
    private val weekEnd = LocalDate(2026, 11, 14)

    @Test
    fun turnsEachSlotIntoAnEntryWithItsRecipe() = runTest {
        val adobo = recipe(name = "Adobo")
        val tapsilog = recipe(name = "Tapsilog")
        val dinner = MealSlot(Uuid.random(), weekStart, MealType.DINNER, adobo.id)
        val breakfast = MealSlot(Uuid.random(), weekEnd, MealType.BREAKFAST, tapsilog.id)
        val useCase = GetWeeklyMealPlanUseCase(
            FakeMealPlanRepository(listOf(dinner, breakfast)),
            FakeRecipeRepository(listOf(adobo, tapsilog))
        )

        val entries = useCase.execute(weekStart, weekEnd).first()

        assertEquals(
            listOf(
                MealPlanEntry(dinner.id, weekStart, MealType.DINNER, adobo),
                MealPlanEntry(breakfast.id, weekEnd, MealType.BREAKFAST, tapsilog)
            ),
            entries
        )
    }

    @Test
    fun skipsSlotsWhoseRecipeNoLongerExists() = runTest {
        val adobo = recipe()
        val useCase = GetWeeklyMealPlanUseCase(
            FakeMealPlanRepository(
                listOf(
                    MealSlot(Uuid.random(), weekStart, MealType.DINNER, adobo.id),
                    MealSlot(Uuid.random(), weekStart, MealType.LUNCH, Uuid.random())
                )
            ),
            FakeRecipeRepository(listOf(adobo))
        )

        val entries = useCase.execute(weekStart, weekEnd).first()

        assertEquals(listOf(MealType.DINNER), entries.map { it.mealType })
    }

    @Test
    fun onlyIncludesSlotsInsideTheWeek() = runTest {
        val adobo = recipe()
        val useCase = GetWeeklyMealPlanUseCase(
            FakeMealPlanRepository(listOf(MealSlot(Uuid.random(), LocalDate(2026, 11, 15), MealType.DINNER, adobo.id))),
            FakeRecipeRepository(listOf(adobo))
        )

        assertTrue(useCase.execute(weekStart, weekEnd).first().isEmpty())
    }
}
