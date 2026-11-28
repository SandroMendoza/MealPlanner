package com.meal.presentation.widget

import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi

/** The hour of day by which a meal is usually over, so the meal after it becomes the next one. */
fun mealEndHour(mealType: MealType): Int = when (mealType) {
    MealType.BREAKFAST -> 10
    MealType.LUNCH -> 14
    MealType.SNACK -> 17
    MealType.DINNER -> 21
}

/**
 * The first meal in [entries] that is still ahead at [hour] on [today]: a meal later today whose
 * end hour hasn't passed, or else the earliest meal on a later day.
 */
fun nextMeal(entries: List<MealPlanEntry>, today: LocalDate, hour: Int): MealPlanEntry? =
    entries
        .filter { it.date > today || (it.date == today && hour < mealEndHour(it.mealType)) }
        .minWithOrNull(compareBy<MealPlanEntry> { it.date }.thenBy { mealEndHour(it.mealType) })

/**
 * The recipes the recipe of the day picks from: the favorites, or the built-in recipes when there
 * are no favorites, or every recipe when neither exists. Sorted by id so the order doesn't depend
 * on how the database returns them.
 */
@OptIn(ExperimentalUuidApi::class)
fun recipeOfTheDayPool(recipes: List<Recipe>): List<Recipe> =
    recipes.filter { it.isFavorite }
        .ifEmpty { recipes.filterNot { it.isUserCreated } }
        .ifEmpty { recipes }
        .sortedBy { it.id.toString() }

/**
 * The position in a pool of [poolSize] recipes for [date], moved on by [shuffles]. The same date
 * always gives the same position, and consecutive days land far apart in the pool.
 */
fun recipeOfTheDayIndex(date: LocalDate, shuffles: Int, poolSize: Int): Int {
    require(poolSize > 0)
    val dayHash = date.toEpochDays().toLong() * KNUTH_MULTIPLIER
    return (dayHash + shuffles).mod(poolSize)
}

private const val KNUTH_MULTIPLIER = 2_654_435_761L
