@file:OptIn(ExperimentalUuidApi::class)

package com.meal.domain.usecase

import com.meal.domain.fakes.FakeMealPlanRepository
import com.meal.domain.fakes.FakeRecipeRepository
import com.meal.domain.fakes.FakeShoppingListRepository
import com.meal.domain.fakes.ingredient
import com.meal.domain.fakes.recipe
import com.meal.domain.model.MealSlot
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GenerateShoppingListUseCaseTest {

    private val weekStart = LocalDate(2026, 11, 8)
    private val weekEnd = LocalDate(2026, 11, 14)

    private val recipes = FakeRecipeRepository()
    private val mealPlan = FakeMealPlanRepository()
    private val shoppingList = FakeShoppingListRepository()
    private val useCase = GenerateShoppingListUseCase(
        GetWeeklyMealPlanUseCase(mealPlan, recipes),
        shoppingList
    )

    private fun plan(vararg planned: Pair<LocalDate, com.meal.domain.model.Recipe>) {
        recipes.recipes.value = planned.map { it.second }.distinctBy { it.id }
        mealPlan.slots.value = planned.map { (date, recipe) -> MealSlot(Uuid.random(), date, MealType.DINNER, recipe.id) }
    }

    private suspend fun syncedItems(): List<ShoppingListItem> {
        useCase.execute(weekStart, weekEnd).first()
        return shoppingList.syncCalls.last().required
    }

    @Test
    fun sumsTheSameIngredientAcrossRecipesIgnoringCaseAndSurroundingSpaces() = runTest {
        plan(
            weekStart to recipe(name = "Adobo", ingredients = listOf(ingredient("Garlic", 3.0, MeasurementUnit.PIECE))),
            weekEnd to recipe(name = "Sinigang", ingredients = listOf(ingredient("  garlic ", 2.0, MeasurementUnit.PIECE)))
        )

        val items = syncedItems()

        assertEquals(1, items.size)
        assertEquals("garlic|PIECE", items.single().itemKey)
        assertEquals("Garlic", items.single().name)
        assertEquals(5.0, items.single().totalQuantity)
        assertEquals(MeasurementUnit.PIECE, items.single().unit)
        assertEquals(false, items.single().checked)
    }

    @Test
    fun keepsTheSameIngredientInDifferentUnitsAsSeparateItems() = runTest {
        plan(
            weekStart to recipe(
                ingredients = listOf(
                    ingredient("Soy Sauce", 2.0, MeasurementUnit.TABLESPOON),
                    ingredient("Soy Sauce", 100.0, MeasurementUnit.MILLILITER)
                )
            )
        )

        val items = syncedItems()

        assertEquals(listOf("soy sauce|TABLESPOON", "soy sauce|MILLILITER"), items.map { it.itemKey })
    }

    @Test
    fun countsARecipePlannedTwiceTwice() = runTest {
        val adobo = recipe(ingredients = listOf(ingredient("Chicken", 500.0, MeasurementUnit.GRAMS)))
        plan(weekStart to adobo, weekEnd to adobo)

        assertEquals(1000.0, syncedItems().single().totalQuantity)
    }

    @Test
    fun keepsIngredientsInFirstSeenOrder() = runTest {
        plan(
            weekStart to recipe(
                ingredients = listOf(
                    ingredient("Rice", 1.0, MeasurementUnit.CUP),
                    ingredient("Egg", 2.0, MeasurementUnit.PIECE)
                )
            ),
            weekEnd to recipe(
                ingredients = listOf(
                    ingredient("Onion", 1.0, MeasurementUnit.PIECE),
                    ingredient("Rice", 2.0, MeasurementUnit.CUP)
                )
            )
        )

        assertEquals(listOf("Rice", "Egg", "Onion"), syncedItems().map { it.name })
    }

    @Test
    fun syncsAnEmptyListWhenNothingIsPlanned() = runTest {
        assertTrue(syncedItems().isEmpty())
    }

    @Test
    fun syncsForTheRequestedRangeOnly() = runTest {
        plan(
            weekStart to recipe(ingredients = listOf(ingredient("Garlic", 1.0, MeasurementUnit.PIECE))),
            LocalDate(2026, 11, 15) to recipe(ingredients = listOf(ingredient("Ginger", 1.0, MeasurementUnit.PIECE)))
        )

        val items = syncedItems()

        assertEquals(weekStart, shoppingList.syncCalls.last().rangeStart)
        assertEquals(weekEnd, shoppingList.syncCalls.last().rangeEnd)
        assertEquals(listOf("Garlic"), items.map { it.name })
    }

    @Test
    fun emitsTheStoredShoppingListItems() = runTest {
        val stored = listOf(
            ShoppingListItem("garlic|PIECE", "Garlic", 3.0, MeasurementUnit.PIECE, checked = true, price = 1.5)
        )
        shoppingList.items.value = stored

        assertEquals(stored, useCase.execute(weekStart, weekEnd).first())
    }
}
