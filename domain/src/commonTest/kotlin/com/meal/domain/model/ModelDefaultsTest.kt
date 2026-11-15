package com.meal.domain.model

import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ModelDefaultsTest {

    @Test
    fun eachMealTypeHasItsDefaultReminderTime() {
        assertEquals(
            mapOf(
                MealType.BREAKFAST to LocalTime(8, 0),
                MealType.LUNCH to LocalTime(12, 0),
                MealType.SNACK to LocalTime(15, 0),
                MealType.DINNER to LocalTime(18, 0)
            ),
            MealType.entries.associateWith { it.defaultReminderTime() }
        )
    }

    @Test
    fun aNewProfileIsCalledChef() {
        assertEquals("Chef", UserProfile().name)
    }

    @Test
    fun defaultSettings() {
        assertEquals(
            AppSettings(
                themeMode = ThemeMode.SYSTEM,
                language = Language.ENGLISH,
                currency = Currency.USD,
                lockPastDates = false,
                defaultCalendarView = CalendarViewMode.DAY,
                readAloudEnabled = false,
                defaultRecipeView = RecipeViewMode.GRID
            ),
            AppSettings()
        )
    }
}
