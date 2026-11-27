package com.meal.domain.model

import com.meal.domain.model.enums.MealType
import com.meal.domain.strings.englishStrings
import kotlin.test.Test
import kotlin.test.assertEquals

class MealReminderMessageTest {

    @Test
    fun plannedRecipesAreNamedInTheTitle() {
        assertEquals(
            MealReminderMessage("Lunch: Chicken Adobo", "Time to start cooking."),
            mealReminderMessage(englishStrings, MealType.LUNCH, listOf("Chicken Adobo"))
        )
        assertEquals(
            "Dinner: Sinigang, Garlic Rice",
            mealReminderMessage(englishStrings, MealType.DINNER, listOf("Sinigang", "Garlic Rice")).title
        )
    }

    @Test
    fun nothingPlannedInvitesPlanningAMeal() {
        assertEquals(
            MealReminderMessage("Breakfast: nothing planned", "Tap to plan a meal."),
            mealReminderMessage(englishStrings, MealType.BREAKFAST, emptyList())
        )
    }
}
