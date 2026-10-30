package com.meal.planner.navigation

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val RECIPES = "recipes"
    const val RECIPE_NEW = "recipes/new"
    const val RECIPE_DETAIL_PATTERN = "recipes/{recipeId}"
    const val RECIPE_EDIT_PATTERN = "recipes/{recipeId}/edit"
    const val CALENDAR = "calendar"
    const val CALENDAR_SLOT_PICK_PATTERN = "calendar/{date}/{mealType}/pick?replaceSlotId={replaceSlotId}"
    const val SHOPPING_LIST = "shoppingList"

    fun recipeDetail(recipeId: String) = "recipes/$recipeId"
    fun recipeEdit(recipeId: String) = "recipes/$recipeId/edit"
    fun calendarSlotPick(date: String, mealType: String, replaceSlotId: String? = null): String {
        val base = "calendar/$date/$mealType/pick"
        return if (replaceSlotId != null) "$base?replaceSlotId=$replaceSlotId" else base
    }
}
