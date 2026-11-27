package com.meal.domain.model

import com.meal.domain.model.enums.MealType
import com.meal.domain.strings.Strings

data class MealReminderMessage(val title: String, val body: String)

/** The reminder text for [mealType]: the planned recipes, or an invitation to plan one. */
fun mealReminderMessage(strings: Strings, mealType: MealType, plannedRecipeNames: List<String>): MealReminderMessage =
    if (plannedRecipeNames.isEmpty()) {
        MealReminderMessage(strings.mealReminderNothingPlannedTitle(mealType), strings.mealReminderNothingPlannedBody)
    } else {
        MealReminderMessage(
            strings.mealReminderPlannedTitle(mealType, plannedRecipeNames.joinToString(", ")),
            strings.mealReminderPlannedBody
        )
    }
