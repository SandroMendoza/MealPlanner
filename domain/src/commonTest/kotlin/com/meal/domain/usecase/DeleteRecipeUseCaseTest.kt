@file:OptIn(ExperimentalUuidApi::class)

package com.meal.domain.usecase

import com.meal.domain.fakes.FakeMealPlanRepository
import com.meal.domain.fakes.FakeRecipeRepository
import com.meal.domain.fakes.recipe
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

class DeleteRecipeUseCaseTest {

    private val adobo = recipe()
    private val recipes = FakeRecipeRepository(listOf(adobo))
    private val mealPlan = FakeMealPlanRepository()
    private val useCase = DeleteRecipeUseCase(recipes, mealPlan)

    private val futureDates = listOf(LocalDate(2026, 11, 20), LocalDate(2026, 11, 27))

    @Test
    fun deletesARecipeThatIsNotPlannedAhead() = runTest {
        val result = useCase.execute(adobo.id)

        assertEquals(DeleteRecipeResult.Deleted, result)
        assertEquals(listOf(adobo.id), recipes.softDeleted)
        assertTrue(mealPlan.removedFutureSlotsFor.isEmpty())
    }

    @Test
    fun asksForConfirmationWhenTheRecipeIsPlannedAhead() = runTest {
        mealPlan.futureDates = mapOf(adobo.id to futureDates)

        val result = useCase.execute(adobo.id)

        assertEquals(DeleteRecipeResult.RequiresConfirmation(futureDates), result)
        assertTrue(recipes.softDeleted.isEmpty())
        assertTrue(mealPlan.removedFutureSlotsFor.isEmpty())
    }

    @Test
    fun forcedDeleteRemovesTheFuturePlansAndTheRecipe() = runTest {
        mealPlan.futureDates = mapOf(adobo.id to futureDates)

        val result = useCase.execute(adobo.id, force = true)

        assertEquals(DeleteRecipeResult.Deleted, result)
        assertEquals(listOf(adobo.id), mealPlan.removedFutureSlotsFor)
        assertEquals(listOf(adobo.id), recipes.softDeleted)
    }

    @Test
    fun forcedDeleteOfAnUnplannedRecipeLeavesThePlanAlone() = runTest {
        val result = useCase.execute(adobo.id, force = true)

        assertEquals(DeleteRecipeResult.Deleted, result)
        assertTrue(mealPlan.removedFutureSlotsFor.isEmpty())
        assertEquals(listOf(adobo.id), recipes.softDeleted)
    }
}
