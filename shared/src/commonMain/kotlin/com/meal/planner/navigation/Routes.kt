package com.meal.planner.navigation

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val RECIPES = "recipes"
    const val RECIPES_FAVORITES = "recipes/favorites"
    const val RECIPES_CREATED = "recipes/created"
    const val RECIPE_NEW = "recipes/new"
    const val RECIPE_DETAIL_PATTERN = "recipes/{recipeId}"
    const val RECIPE_EDIT_PATTERN = "recipes/{recipeId}/edit"
    const val CALENDAR = "calendar"
    const val CALENDAR_SLOT_PICK_PATTERN = "calendar/{date}/{mealType}/pick?replaceSlotId={replaceSlotId}"
    const val SHOPPING_LIST = "shoppingList"
    const val SPENDING_REPORTS = "spendingReports"
    const val SETTINGS = "settings"
    const val NOTIFICATION_SETTINGS = "settings/notifications"
    const val ABOUT = "settings/about"
    const val PRIVACY_POLICY = "settings/privacy"
    const val FEATURE_GUIDE = "settings/guide"
    const val WALKTHROUGH = "settings/guide/walkthrough"

    /** Sentinel path segment meaning "ask which meal type after a recipe is picked". */
    const val ANY_MEAL_TYPE = "any"

    fun recipeDetail(recipeId: String) = "recipes/$recipeId"
    fun recipeEdit(recipeId: String) = "recipes/$recipeId/edit"
    fun calendarSlotPick(date: String, mealType: String?, replaceSlotId: String? = null): String {
        val base = "calendar/$date/${mealType ?: ANY_MEAL_TYPE}/pick"
        return if (replaceSlotId != null) "$base?replaceSlotId=$replaceSlotId" else base
    }

    private val DEEP_LINK_ROUTES = setOf(HOME, CALENDAR, RECIPES, SHOPPING_LIST, SPENDING_REPORTS)

    /**
     * Maps the path of a `mealplanner://open/<path>` link to its nav route, or null if unknown.
     * Besides the tab screens, `recipes/<recipe id>` opens that recipe.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun routeForDeepLinkPath(path: String?): String? {
        if (path == null) return null
        if (path in DEEP_LINK_ROUTES) return path
        val recipeId = path.removePrefix("$RECIPES/").takeIf { it != path } ?: return null
        return recipeId.takeIf { runCatching { Uuid.parse(it) }.isSuccess }?.let(::recipeDetail)
    }
}
