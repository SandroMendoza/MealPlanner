package com.meal.presentation.widget

import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.MealType
import com.meal.presentation.fakes.recipe
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class WidgetSelectionTest {

    private val today = LocalDate(2026, 11, 28)
    private val tomorrow = LocalDate(2026, 11, 29)

    private fun entry(date: LocalDate, mealType: MealType, recipe: Recipe = recipe()) =
        MealPlanEntry(id = Uuid.random(), date = date, mealType = mealType, recipe = recipe)

    @Test
    fun nextMealIsTheFirstMealStillAheadToday() {
        val breakfast = entry(today, MealType.BREAKFAST)
        val lunch = entry(today, MealType.LUNCH)
        val dinner = entry(today, MealType.DINNER)
        val entries = listOf(dinner, lunch, breakfast)

        assertEquals(breakfast, nextMeal(entries, today, hour = 8))
        assertEquals(lunch, nextMeal(entries, today, hour = 11))
        assertEquals(dinner, nextMeal(entries, today, hour = 15))
    }

    @Test
    fun nextMealMovesToALaterDayOnceTodaysMealsAreOver() {
        val dinner = entry(today, MealType.DINNER)
        val tomorrowsLunch = entry(tomorrow, MealType.LUNCH)
        val tomorrowsBreakfast = entry(tomorrow, MealType.BREAKFAST)

        assertEquals(tomorrowsBreakfast, nextMeal(listOf(dinner, tomorrowsLunch, tomorrowsBreakfast), today, hour = 22))
    }

    @Test
    fun nextMealOrdersASnackBetweenLunchAndDinner() {
        val snack = entry(today, MealType.SNACK)
        val dinner = entry(today, MealType.DINNER)

        assertEquals(snack, nextMeal(listOf(dinner, snack), today, hour = 15))
    }

    @Test
    fun nextMealIsNullWhenNothingIsAhead() {
        assertNull(nextMeal(listOf(entry(today, MealType.BREAKFAST)), today, hour = 12))
        assertNull(nextMeal(emptyList(), today, hour = 12))
    }

    @Test
    fun poolPrefersFavoritesThenBuiltInRecipes() {
        val favorite = recipe(name = "Favorite", isFavorite = true)
        val builtIn = recipe(name = "Built-in", isUserCreated = false)
        val own = recipe(name = "Own")

        assertEquals(listOf(favorite), recipeOfTheDayPool(listOf(own, builtIn, favorite)))
        assertEquals(listOf(builtIn), recipeOfTheDayPool(listOf(own, builtIn)))
        assertEquals(listOf(own), recipeOfTheDayPool(listOf(own)))
    }

    @Test
    fun recipeOfTheDayIsStableForADayAndShuffleMovesIt() {
        val first = recipeOfTheDayIndex(today, shuffles = 0, poolSize = 50)

        assertEquals(first, recipeOfTheDayIndex(today, shuffles = 0, poolSize = 50))
        assertNotEquals(first, recipeOfTheDayIndex(today, shuffles = 1, poolSize = 50))
        assertNotEquals(first, recipeOfTheDayIndex(tomorrow, shuffles = 0, poolSize = 50))
    }

    @Test
    fun recipeOfTheDayIndexStaysInsideThePool() {
        (0 until 400).forEach { day ->
            val date = today.plus(day, DateTimeUnit.DAY)
            val index = recipeOfTheDayIndex(date, shuffles = day, poolSize = 7)
            assertTrue(index in 0 until 7, "index $index for $date")
        }
    }
}
