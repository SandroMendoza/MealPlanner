package com.meal.domain.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: Language = Language.ENGLISH,
    val currency: Currency = Currency.USD,
    val lockPastDates: Boolean = false,
    val defaultCalendarView: CalendarViewMode = CalendarViewMode.DAY,
    val readAloudEnabled: Boolean = false,
    val defaultRecipeView: RecipeViewMode = RecipeViewMode.GRID
)
