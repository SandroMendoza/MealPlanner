package com.meal.domain.model

import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalDate

data class MealPlanEntry(
    val date: LocalDate,
    val mealType: MealType,
    val recipe: Recipe
)
