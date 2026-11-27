@file:OptIn(ExperimentalUuidApi::class)

package com.meal.domain.usecase

import com.meal.domain.fakes.FakeIngredientRepository
import com.meal.domain.fakes.FakeMealPlanRepository
import com.meal.domain.fakes.FakeRecipeRepository
import com.meal.domain.fakes.FakeSettingsRepository
import com.meal.domain.fakes.recipe
import com.meal.domain.model.AppSettings
import com.meal.domain.model.Ingredient
import com.meal.domain.model.MealSlot
import com.meal.domain.model.enums.MealType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class RecipeAndMealPlanUseCasesTest {

    private val date = LocalDate(2026, 11, 14)

    @Test
    fun addingARecipeStoresIt() = runTest {
        val recipes = FakeRecipeRepository()
        val adobo = recipe()

        AddRecipeUseCase(recipes).execute(adobo)

        assertEquals(listOf(adobo), recipes.recipes.value)
    }

    @Test
    fun updatingARecipeReplacesTheStoredVersion() = runTest {
        val adobo = recipe(name = "Adobo")
        val recipes = FakeRecipeRepository(listOf(adobo))

        UpdateRecipeUseCase(recipes).execute(adobo.copy(name = "Adobong Manok"))

        assertEquals("Adobong Manok", recipes.recipes.value.single().name)
    }

    @Test
    fun gettingAllRecipesReturnsTheStoredRecipes() = runTest {
        val stored = listOf(recipe(name = "Adobo"), recipe(name = "Sinigang"))

        assertEquals(stored, GetAllRecipesUseCase(FakeRecipeRepository(stored)).execute().first())
    }

    @Test
    fun gettingARecipeByIdFindsItOrReturnsNull() = runTest {
        val adobo = recipe()
        val useCase = GetRecipeByIdUseCase(FakeRecipeRepository(listOf(adobo)))

        assertEquals(adobo, useCase.execute(adobo.id).first())
        assertNull(useCase.execute(Uuid.random()).first())
    }

    @Test
    fun togglingAFavoriteSetsTheRequestedState() = runTest {
        val adobo = recipe()
        val recipes = FakeRecipeRepository(listOf(adobo))
        val useCase = ToggleFavoriteUseCase(recipes)

        useCase.execute(adobo.id, isFavorite = true)
        assertTrue(recipes.recipes.value.single().isFavorite)

        useCase.execute(adobo.id, isFavorite = false)
        assertEquals(false, recipes.recipes.value.single().isFavorite)
    }

    @Test
    fun gettingAllIngredientsReturnsTheStoredIngredients() = runTest {
        val stored = listOf(Ingredient(Uuid.random(), "Garlic"))

        assertEquals(stored, GetAllIngredientsUseCase(FakeIngredientRepository(stored)).execute().first())
    }

    @Test
    fun addingARecipeToASlotPlansItForThatDateAndMeal() = runTest {
        val mealPlan = FakeMealPlanRepository()
        val recipeId = Uuid.random()

        AddRecipeToSlotUseCase(mealPlan, FakeSettingsRepository()).execute(date, MealType.LUNCH, recipeId)

        val slot = mealPlan.slots.value.single()
        assertEquals(date, slot.date)
        assertEquals(MealType.LUNCH, slot.mealType)
        assertEquals(recipeId, slot.recipeId)
    }

    @Test
    fun lockedPastDatesRejectAddingToAPastDayButNotToToday() = runTest {
        val mealPlan = FakeMealPlanRepository()
        val locked = FakeSettingsRepository(AppSettings(lockPastDates = true))
        val useCase = AddRecipeToSlotUseCase(mealPlan, locked, today = { date })

        assertFalse(useCase.execute(LocalDate(2026, 11, 13), MealType.LUNCH, Uuid.random()))
        assertTrue(useCase.execute(date, MealType.LUNCH, Uuid.random()))

        assertEquals(listOf(date), mealPlan.slots.value.map { it.date })
    }

    @Test
    fun unlockedPastDatesAllowAddingToAPastDay() = runTest {
        val mealPlan = FakeMealPlanRepository()
        val useCase = AddRecipeToSlotUseCase(mealPlan, FakeSettingsRepository(), today = { date })

        assertTrue(useCase.execute(LocalDate(2026, 11, 13), MealType.LUNCH, Uuid.random()))
    }

    @Test
    fun replacingTheRecipeInASlotKeepsTheSlot() = runTest {
        val slot = MealSlot(Uuid.random(), date, MealType.DINNER, Uuid.random())
        val mealPlan = FakeMealPlanRepository(listOf(slot))
        val newRecipeId = Uuid.random()

        ReplaceRecipeInSlotUseCase(mealPlan).execute(slot.id, newRecipeId)

        assertEquals(listOf(slot.copy(recipeId = newRecipeId)), mealPlan.slots.value)
    }

    @Test
    fun removingASlotLeavesTheOthers() = runTest {
        val dinner = MealSlot(Uuid.random(), date, MealType.DINNER, Uuid.random())
        val lunch = MealSlot(Uuid.random(), date, MealType.LUNCH, Uuid.random())
        val mealPlan = FakeMealPlanRepository(listOf(dinner, lunch))

        RemoveMealSlotUseCase(mealPlan).execute(dinner.id)

        assertEquals(listOf(lunch), mealPlan.slots.value)
    }
}
