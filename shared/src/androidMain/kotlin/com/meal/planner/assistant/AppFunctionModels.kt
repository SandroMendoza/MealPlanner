package com.meal.planner.assistant

import androidx.appfunctions.AppFunctionSerializable

/** Generic outcome of an action-style AppFunction that doesn't return data of its own. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class ActionResult(
    /** Whether the action completed successfully. */
    val success: Boolean,
    /** A human-readable summary of what happened, suitable for the agent to relay to the user. */
    val message: String
)

/** A single item currently on the shopping list. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class ShoppingListItemResult(
    /** Opaque identifier for this item, required to toggle it checked/unchecked later. */
    val itemKey: String,
    /** The ingredient or product name. */
    val name: String,
    /** The total quantity needed. */
    val quantity: Double,
    /** The unit the quantity is measured in (e.g. "g", "cup", "piece"). */
    val unit: String,
    /** Whether the item has already been checked off. */
    val checked: Boolean
)

/** The current shopping list for this week. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class ShoppingListResult(
    /** The items currently on the list. */
    val items: List<ShoppingListItemResult>
)

/** A short summary of a recipe, enough to identify it for a follow-up lookup. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RecipeSummaryResult(
    /** The recipe's unique identifier, used to fetch its full details. */
    val id: String,
    /** The recipe's name. */
    val name: String,
    /** Preparation time in minutes. */
    val preparationTimeMinutes: Int,
    /** Number of servings the recipe makes. */
    val servings: Int
)

/** The recipes matching a search query. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RecipeSearchResult(
    /** The matching recipes, if any. */
    val recipes: List<RecipeSummaryResult>
)

/** A single ingredient line within a recipe. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RecipeIngredientResult(
    /** The ingredient name. */
    val name: String,
    /** The quantity needed. */
    val quantity: Double,
    /** The unit the quantity is measured in (e.g. "g", "cup", "piece"). */
    val unit: String
)

/** The full details of a single recipe. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class RecipeDetailsResult(
    /** The recipe's name. */
    val name: String,
    /** A short description of the recipe, if one was provided. */
    val description: String?,
    /** Preparation time in minutes. */
    val preparationTimeMinutes: Int,
    /** Number of servings the recipe makes. */
    val servings: Int,
    /** Calories per serving, if known. */
    val calories: Int?,
    /** The recipe's ingredients. */
    val ingredients: List<RecipeIngredientResult>,
    /** The recipe's instructions, already formatted as ready-to-read step text. */
    val instructions: List<String>
)

/** A single ingredient to add when creating a new recipe. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AddRecipeIngredientParams(
    /** The ingredient name. */
    val name: String,
    /** The quantity needed. */
    val quantity: Double,
    /** The unit the quantity is measured in (e.g. "g", "cup", "piece"). */
    val unit: String
)

/** The details needed to create a new recipe. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AddRecipeParams(
    /** The recipe's name. */
    val name: String,
    /** A short description of the recipe. */
    val description: String?,
    /** The meals this recipe is suitable for (e.g. "BREAKFAST", "LUNCH", "DINNER", "SNACK"). */
    val mealTypes: List<String>,
    /** The recipe's category (e.g. "MAIN_DISH", "SIDE_DISH", "SOUP", "SALAD", "DESSERT", "BEVERAGE"). */
    val category: String,
    /** Preparation time in minutes. */
    val preparationTimeMinutes: Int,
    /** Number of servings the recipe makes. */
    val servings: Int,
    /** Calories per serving, if known. */
    val calories: Int?,
    /** The recipe's ingredients. */
    val ingredients: List<AddRecipeIngredientParams>,
    /** The recipe's instructions, one step per entry, in order. */
    val instructions: List<String>
)

/** A single recipe assigned to a meal plan slot. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class MealPlanEntryResult(
    /** The ISO-8601 date of the slot (e.g. "2026-11-13"). */
    val date: String,
    /** The meal type of the slot (e.g. "BREAKFAST", "LUNCH", "DINNER", "SNACK"). */
    val mealType: String,
    /** The name of the recipe assigned to this slot. */
    val recipeName: String
)

/** The meal plan entries for a requested date range. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class MealPlanResult(
    /** The scheduled entries within the requested range. */
    val entries: List<MealPlanEntryResult>
)

/** A spending summary for a requested date range. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class SpendingReportResult(
    /** The ISO-8601 start date of the reported range. */
    val rangeStart: String,
    /** The ISO-8601 end date of the reported range. */
    val rangeEnd: String,
    /** The total amount spent in this range. */
    val totalSpent: Double,
    /** The currency symbol to display alongside totalSpent (e.g. "$", "€"). */
    val currencySymbol: String
)
