package com.meal.planner.assistant

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionDeclaration
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.Recipe
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.service.TtsSpeaker
import com.meal.domain.usecase.AddRecipeToSlotUseCase
import com.meal.domain.usecase.AddRecipeUseCase
import com.meal.domain.usecase.AddShoppingListItemUseCase
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetSpendingReportUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.ToggleShoppingListItemUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.core.context.GlobalContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Exposes MealPlanner's shopping list, recipe, meal plan and spending-report actions as
 * Android App Functions, so an on-device agent can discover and invoke them.
 *
 * Instantiated by the system/library via the KSP-generated concrete subclass named by
 * [AppFunctionServiceEntryPoint.serviceName], not by Koin, so use cases are resolved from the
 * global Koin context per call (mirroring [com.meal.planner.common.MealReminderReceiver], the
 * other Android entry point the system instantiates outside of Koin's control).
 */
@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "MealPlannerAppFunctionService",
    appFunctionXmlFileName = "meal_planner_app_function_service"
)
@OptIn(ExperimentalUuidApi::class)
abstract class BaseMealPlannerAppFunctions : AppFunctionService() {

    private val koin get() = GlobalContext.get()

    /**
     * Lists the items currently on this week's shopping list.
     *
     * @return The shopping list for the current week.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun listShoppingListItems(): ShoppingListResult {
        val weekStart = currentWeekSunday()
        val weekEnd = weekEndFor(weekStart)
        val items = koin.get<GenerateShoppingListUseCase>().execute(weekStart, weekEnd).first()
        return ShoppingListResult(
            items.map { item ->
                ShoppingListItemResult(
                    itemKey = item.itemKey,
                    name = item.name,
                    quantity = item.totalQuantity,
                    unit = item.unit.unit,
                    checked = item.checked
                )
            }
        )
    }

    /**
     * Adds an item to this week's shopping list.
     *
     * Use this when the user asks to add, buy, pick up, or get a food item or ingredient,
     * e.g. "add milk to my shopping list" or "I need to buy eggs".
     *
     * @param name The ingredient or product name (e.g. "milk", "eggs").
     * @param quantity The quantity needed. Default to 1 if the user doesn't mention an amount.
     * @param unit The unit the quantity is measured in (e.g. "g", "kg", "ml", "l", "tsp", "tbsp", "cup", "piece"). Default to "piece" if the user doesn't mention a unit.
     * @return Whether the item was added.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun addShoppingListItem(name: String, quantity: Double, unit: String): ActionResult {
        val measurementUnit = parseMeasurementUnit(unit)
        val weekStart = currentWeekSunday()
        val weekEnd = weekEndFor(weekStart)
        koin.get<AddShoppingListItemUseCase>().execute(name, quantity, measurementUnit, weekStart, weekEnd)
        return ActionResult(success = true, message = "Added \"$name\" to the shopping list.")
    }

    /**
     * Checks or unchecks an item on this week's shopping list.
     *
     * @param itemKey The item's identifier, obtained from [listShoppingListItems].
     * @param checked Whether the item should be marked as checked off.
     * @return Whether the item was updated.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun toggleShoppingListItem(itemKey: String, checked: Boolean): ActionResult {
        val weekStart = currentWeekSunday()
        val weekEnd = weekEndFor(weekStart)
        koin.get<ToggleShoppingListItemUseCase>().execute(itemKey, weekStart, weekEnd, checked)
        return ActionResult(success = true, message = if (checked) "Checked off the item." else "Unchecked the item.")
    }

    /**
     * Searches the user's recipes by name.
     *
     * @param query The text to search for in recipe names.
     * @return The matching recipes.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun searchRecipes(query: String): RecipeSearchResult {
        val recipes = koin.get<GetAllRecipesUseCase>().execute().first()
        val matches = recipes.filter { it.name.contains(query, ignoreCase = true) }
        return RecipeSearchResult(
            matches.map { recipe ->
                RecipeSummaryResult(
                    id = recipe.id.toString(),
                    name = recipe.name,
                    preparationTimeMinutes = recipe.preparationTimeMinutes,
                    servings = recipe.servings
                )
            }
        )
    }

    /**
     * Gets a recipe's full details and reads its instructions aloud.
     *
     * @param recipeId The recipe's identifier, obtained from [searchRecipes].
     * @return The recipe's details.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun getRecipeDetails(recipeId: String): RecipeDetailsResult {
        val id = parseUuid(recipeId)
        val recipe = koin.get<GetRecipeByIdUseCase>().execute(id).first()
            ?: throw AppFunctionElementNotFoundException("No recipe found for id = $recipeId")

        val instructionLines = recipe.instructions.map { "Step ${it.stepNumber}. ${it.description}" }
        koin.get<TtsSpeaker>().speak(instructionLines.joinToString(" "))

        return RecipeDetailsResult(
            name = recipe.name,
            description = recipe.description,
            preparationTimeMinutes = recipe.preparationTimeMinutes,
            servings = recipe.servings,
            calories = recipe.calories,
            ingredients = recipe.ingredients.map {
                RecipeIngredientResult(name = it.ingredient.name, quantity = it.quantity, unit = it.unit.unit)
            },
            instructions = instructionLines
        )
    }

    /**
     * Creates a new recipe.
     *
     * @param params The recipe's name, ingredients, instructions and other details.
     * @return Whether the recipe was created.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun addRecipe(params: AddRecipeParams): ActionResult {
        val recipe = Recipe(
            id = Uuid.random(),
            name = params.name,
            description = params.description,
            mealTypes = params.mealTypes.map { parseMealType(it) },
            category = parseFoodCategory(params.category),
            preparationTimeMinutes = params.preparationTimeMinutes,
            servings = params.servings,
            calories = params.calories,
            imageUrl = null,
            ingredients = params.ingredients.map { ingredient ->
                RecipeIngredient(
                    id = Uuid.random(),
                    ingredient = Ingredient(id = Uuid.random(), name = ingredient.name),
                    quantity = ingredient.quantity,
                    unit = parseMeasurementUnit(ingredient.unit)
                )
            },
            instructions = params.instructions.mapIndexed { index, description -> Instruction(index + 1, description) },
            isFavorite = false,
            isUserCreated = true
        )
        koin.get<AddRecipeUseCase>().execute(recipe)
        return ActionResult(success = true, message = "Added recipe \"${params.name}\".")
    }

    /**
     * Gets the meal plan entries for today or this week.
     *
     * Use this when the user asks what's for dinner, what they're eating, or what meals are
     * planned, e.g. "what's for dinner tonight" or "what's on the menu this week".
     *
     * @param range Either "today" or "week". Default to "today" for questions about a single meal.
     * @return The scheduled entries within the requested range.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun getMealPlan(range: String): MealPlanResult {
        val (rangeStart, rangeEnd) = parseTodayOrWeekRange(range)
        val entries = koin.get<GetWeeklyMealPlanUseCase>().execute(rangeStart, rangeEnd).first()
        return MealPlanResult(
            entries
                .filter { it.date in rangeStart..rangeEnd }
                .map { MealPlanEntryResult(date = it.date.toString(), mealType = it.mealType.name, recipeName = it.recipe.name) }
        )
    }

    /**
     * Assigns a recipe to a meal plan slot.
     *
     * @param date The ISO-8601 date of the slot (e.g. "2026-11-13").
     * @param mealType The meal type of the slot (e.g. "BREAKFAST", "LUNCH", "DINNER", "SNACK").
     * @param recipeId The recipe's identifier, obtained from [searchRecipes].
     * @return Whether the recipe was assigned.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun addRecipeToMealPlan(date: String, mealType: String, recipeId: String): ActionResult {
        val parsedDate = parseLocalDate(date)
        val parsedMealType = parseMealType(mealType)
        val parsedRecipeId = parseUuid(recipeId)
        koin.get<AddRecipeToSlotUseCase>().execute(parsedDate, parsedMealType, parsedRecipeId)
        return ActionResult(success = true, message = "Added the recipe to $date ($mealType).")
    }

    /**
     * Gets a spending summary for today, this week, or this month.
     *
     * @param range One of "today", "week", or "month".
     * @return The spending summary for the requested range.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun getSpendingReport(range: String): SpendingReportResult {
        val (rangeStart, rangeEnd) = parseSpendingRange(range)
        val report = koin.get<GetSpendingReportUseCase>().execute(rangeStart, rangeEnd).first()
        val currencySymbol = koin.get<GetSettingsUseCase>().execute().first().currency.symbol
        return SpendingReportResult(
            rangeStart = report.rangeStart.toString(),
            rangeEnd = report.rangeEnd.toString(),
            totalSpent = report.totalSpent,
            currencySymbol = currencySymbol
        )
    }

    private fun parseTodayOrWeekRange(range: String): Pair<LocalDate, LocalDate> = when (range.lowercase()) {
        "today" -> today().let { it to it }
        "week" -> currentWeekSunday().let { it to weekEndFor(it) }
        else -> throw AppFunctionInvalidArgumentException("range must be \"today\" or \"week\", was \"$range\"")
    }

    private fun parseSpendingRange(range: String): Pair<LocalDate, LocalDate> = when (range.lowercase()) {
        "today" -> today().let { it to it }
        "week" -> currentWeekSunday().let { it to weekEndFor(it) }
        "month" -> {
            val firstOfMonth = LocalDate(today().year, today().monthNumber, 1)
            val lastOfMonth = firstOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
            firstOfMonth to lastOfMonth
        }
        else -> throw AppFunctionInvalidArgumentException("range must be \"today\", \"week\", or \"month\", was \"$range\"")
    }

    private fun parseMeasurementUnit(value: String): MeasurementUnit =
        MeasurementUnit.entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.unit.equals(value, ignoreCase = true) }
            ?: throw AppFunctionInvalidArgumentException("Unknown unit \"$value\"")

    private fun parseMealType(value: String): MealType =
        MealType.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
            ?: throw AppFunctionInvalidArgumentException("Unknown meal type \"$value\"")

    private fun parseFoodCategory(value: String): FoodCategory =
        FoodCategory.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
            ?: throw AppFunctionInvalidArgumentException("Unknown recipe category \"$value\"")

    private fun parseUuid(value: String): Uuid =
        try {
            Uuid.parse(value)
        } catch (e: IllegalArgumentException) {
            throw AppFunctionInvalidArgumentException("Invalid id \"$value\"")
        }

    private fun parseLocalDate(value: String): LocalDate =
        try {
            LocalDate.parse(value)
        } catch (e: IllegalArgumentException) {
            throw AppFunctionInvalidArgumentException("Invalid date \"$value\", expected ISO-8601 (e.g. 2026-11-13)")
        }
}
