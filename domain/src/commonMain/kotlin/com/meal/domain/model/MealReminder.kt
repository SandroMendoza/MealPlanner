package com.meal.domain.model

import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalTime

data class MealReminder(
    val mealType: MealType,
    val enabled: Boolean,
    val time: LocalTime
)

fun MealType.defaultReminderTime(): LocalTime = when (this) {
    MealType.BREAKFAST -> LocalTime(8, 0)
    MealType.LUNCH -> LocalTime(12, 0)
    MealType.DINNER -> LocalTime(18, 0)
    MealType.SNACK -> LocalTime(15, 0)
}
