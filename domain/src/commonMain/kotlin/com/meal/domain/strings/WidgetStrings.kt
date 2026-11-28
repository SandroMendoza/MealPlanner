package com.meal.domain.strings

/** Text shown in the home-screen widgets and the dialogs they open. */
data class WidgetStrings(
    val nextMeal: String,
    val noUpcomingMeals: String,
    val today: String,
    val tomorrow: String,
    val mealNotes: String,
    val noWeekNote: String,
    val shoppingListEmpty: String,
    val recipeOfTheDay: String,
    val shuffleCd: String,
    val quickAddTitle: String,
    val addedToShoppingList: (name: String) -> String,
    val settingsTitle: String,
    val showWeekNote: String,
)
