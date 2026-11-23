package com.meal.presentation

import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.ShoppingGroup
import com.meal.domain.model.SpendingReport
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.presentation.calendar.MealCalendarUiState
import com.meal.presentation.common.today
import com.meal.presentation.fakes.ingredientLine
import com.meal.presentation.fakes.recipe
import com.meal.presentation.fakes.shoppingItem
import com.meal.presentation.recipes.IngredientFormRow
import com.meal.presentation.recipes.InstructionFormRow
import com.meal.presentation.recipes.RecipeDetailUiState
import com.meal.presentation.recipes.RecipeEmptyState
import com.meal.presentation.recipes.RecipeFormUiState
import com.meal.presentation.recipes.RecipeUiState
import com.meal.presentation.shopping.ShoppingListRangeMode
import com.meal.presentation.shopping.ShoppingListUiState
import com.meal.presentation.spending.SpendingReportsUiState
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UiStateTest {

    private val adobo = recipe(
        name = "Chicken Adobo",
        description = "Braised in vinegar",
        category = FoodCategory.MAIN_DISH,
        cuisine = Cuisine.FILIPINO,
        preparationTimeMinutes = 60,
        ingredients = listOf(ingredientLine("Soy sauce", 2.0)),
        isFavorite = true,
        isUserCreated = false
    )
    private val salad = recipe(
        name = "Garden Salad",
        category = FoodCategory.SALAD,
        cuisine = Cuisine.AMERICAN,
        preparationTimeMinutes = 10,
        isUserCreated = true
    )

    @Test
    fun recipeFiltersCombine() {
        val state = RecipeUiState(recipes = listOf(adobo, salad))
        assertEquals(listOf(adobo, salad), state.filteredRecipes)
        assertEquals(listOf(adobo), state.copy(showFavoritesOnly = true).filteredRecipes)
        assertEquals(listOf(salad), state.copy(showCreatedOnly = true).filteredRecipes)
        assertEquals(listOf(salad), state.copy(selectedCategory = FoodCategory.SALAD).filteredRecipes)
        assertEquals(listOf(adobo), state.copy(selectedCuisine = Cuisine.FILIPINO).filteredRecipes)
        assertEquals(listOf(salad), state.copy(prepTimeRange = 0f..30f).filteredRecipes)
    }

    @Test
    fun recipeSearchMatchesNameDescriptionAndIngredientsIgnoringCase() {
        val state = RecipeUiState(recipes = listOf(adobo, salad))
        assertEquals(listOf(salad), state.copy(searchQuery = "  garden ").filteredRecipes)
        assertEquals(listOf(adobo), state.copy(searchQuery = "VINEGAR").filteredRecipes)
        assertEquals(listOf(adobo), state.copy(searchQuery = "soy").filteredRecipes)
        assertEquals(listOf(adobo, salad), state.copy(searchQuery = "   ").filteredRecipes)
    }

    @Test
    fun recipeEmptyStateExplainsWhyNothingShows() {
        assertEquals(RecipeEmptyState.NO_RECIPES, RecipeUiState().emptyState)
        val withRecipes = RecipeUiState(recipes = listOf(salad))
        assertEquals(RecipeEmptyState.NO_FAVORITES, withRecipes.copy(showFavoritesOnly = true).emptyState)
        assertEquals(RecipeEmptyState.NO_CREATED, withRecipes.copy(showCreatedOnly = true).emptyState)
        assertEquals(RecipeEmptyState.NO_MATCHES, withRecipes.copy(searchQuery = "zzz").emptyState)
    }

    @Test
    fun recipeEmptyStateHiddenWhileLoadingOrOnError() {
        assertTrue(RecipeUiState().showEmptyState)
        assertFalse(RecipeUiState(isLoading = true).showEmptyState)
        assertFalse(RecipeUiState(errorMessage = "boom").showEmptyState)
        assertFalse(RecipeUiState(recipes = listOf(salad)).showEmptyState)
    }

    private val validForm = RecipeFormUiState(
        name = "Pancit",
        ingredients = listOf(IngredientFormRow(name = "Noodles", quantity = "1")),
        instructions = listOf(InstructionFormRow("Boil"))
    )

    @Test
    fun recipeFormCanSaveOnlyWhenRequiredFieldsAreFilled() {
        assertTrue(validForm.canSave)
        assertFalse(validForm.copy(name = " ").canSave)
        assertFalse(validForm.copy(ingredients = listOf(IngredientFormRow(name = " "))).canSave)
        assertFalse(validForm.copy(instructions = listOf(InstructionFormRow(""))).canSave)
    }

    @Test
    fun recipeFormRejectsNonNumericTimeAndServingsButAllowsBlank() {
        assertTrue(validForm.copy(preparationTimeMinutes = "", servings = "").canSave)
        assertTrue(validForm.copy(preparationTimeMinutes = "45", servings = "2").canSave)
        assertFalse(validForm.copy(preparationTimeMinutes = "abc").canSave)
        assertFalse(validForm.copy(servings = "1.5").canSave)
    }

    @Test
    fun recipeFormDuplicateNameIgnoresCaseAndSurroundingSpaces() {
        val form = validForm.copy(existingRecipeNames = listOf("  PANCIT "))
        assertTrue(form.isDuplicateName)
        assertFalse(form.canSave)
        assertFalse(form.copy(name = "Pancit Canton").isDuplicateName)
        assertFalse(validForm.copy(name = "", existingRecipeNames = listOf("")).isDuplicateName)
    }

    @Test
    fun recipeDetailNotFoundOnlyAfterLoadingWithoutError() {
        assertTrue(RecipeDetailUiState().notFound)
        assertFalse(RecipeDetailUiState(isLoading = true).notFound)
        assertFalse(RecipeDetailUiState(errorMessage = "boom").notFound)
        assertFalse(RecipeDetailUiState(recipe = salad).notFound)
    }

    @Test
    fun calendarEntriesForFiltersByDateAndSortsByMealType() {
        val day = LocalDate(2026, 11, 10)
        val lunch = MealPlanEntry(Uuid.random(), day, MealType.LUNCH, salad)
        val dinner = MealPlanEntry(Uuid.random(), day, MealType.DINNER, adobo)
        val breakfast = MealPlanEntry(Uuid.random(), day, MealType.BREAKFAST, salad)
        val nextDayLunch = MealPlanEntry(Uuid.random(), day.plus(1, DateTimeUnit.DAY), MealType.LUNCH, salad)
        val state = MealCalendarUiState(weekStart = day, weekEnd = day, entries = listOf(dinner, lunch, nextDayLunch, breakfast))
        assertEquals(listOf(breakfast, lunch, dinner), state.entriesFor(day))
        assertEquals(emptyList(), state.entriesFor(day.plus(2, DateTimeUnit.DAY)))
    }

    @Test
    fun calendarLocksOnlyPastDatesAndOnlyWhenEnabled() {
        val now = today()
        val yesterday = now.minus(1, DateTimeUnit.DAY)
        val locked = MealCalendarUiState(weekStart = now, weekEnd = now, lockPastDates = true)
        assertTrue(locked.isLocked(yesterday))
        assertFalse(locked.isLocked(now))
        assertFalse(locked.isLocked(now.plus(1, DateTimeUnit.DAY)))
        assertFalse(locked.copy(lockPastDates = false).isLocked(yesterday))
    }

    private val start = LocalDate(2026, 11, 8)
    private val end = LocalDate(2026, 11, 14)

    @Test
    fun shoppingSectionsFollowGroupOrderAndPutUngroupedLast() {
        val produce = ShoppingGroup("produce", "Produce", sortOrder = 1)
        val dairy = ShoppingGroup("dairy", "Dairy", sortOrder = 0)
        val empty = ShoppingGroup("empty", "Empty", sortOrder = 2)
        val milk = shoppingItem("milk", groupId = "dairy")
        val onion = shoppingItem("onion", groupId = "produce")
        val salt = shoppingItem("salt")
        val state = ShoppingListUiState(
            mode = ShoppingListRangeMode.WEEK,
            rangeStart = start,
            rangeEnd = end,
            items = listOf(salt, onion, milk),
            groups = listOf(produce, empty, dairy)
        )
        val sections = state.sections
        assertEquals(listOf(dairy, produce, null), sections.map { it.group })
        assertEquals(listOf(listOf(milk), listOf(onion), listOf(salt)), sections.map { it.items })
    }

    @Test
    fun shoppingItemsAreOrderedByCategoryThenNameWithUnknownItemsLast() {
        val state = ShoppingListUiState(
            mode = ShoppingListRangeMode.WEEK,
            rangeStart = start,
            rangeEnd = end,
            items = listOf("Soap", "Garlic", "Fish Sauce", "Chicken", "Gabi", "Pork Belly", "Carrot").map { shoppingItem(it) }
        )
        assertEquals(
            listOf("Pork Belly", "Chicken", "Carrot", "Gabi", "Garlic", "Fish Sauce", "Soap"),
            state.sections.single().items.map { it.name }
        )
    }

    @Test
    fun shoppingSectionsOmitUngroupedWhenEverythingIsGrouped() {
        val dairy = ShoppingGroup("dairy", "Dairy", sortOrder = 0)
        val state = ShoppingListUiState(
            mode = ShoppingListRangeMode.WEEK,
            rangeStart = start,
            rangeEnd = end,
            items = listOf(shoppingItem("milk", groupId = "dairy")),
            groups = listOf(dairy)
        )
        assertEquals(listOf(dairy), state.sections.map { it.group })
    }

    @Test
    fun shoppingTotalSpentIgnoresUnpricedItems() {
        val state = ShoppingListUiState(
            mode = ShoppingListRangeMode.WEEK,
            rangeStart = start,
            rangeEnd = end,
            items = listOf(shoppingItem("a", price = 2.5), shoppingItem("b"), shoppingItem("c", price = 1.25))
        )
        assertEquals(3.75, state.totalSpent)
    }

    @Test
    fun shoppingIsEmptyOnlyAfterLoadingWithoutError() {
        val base = ShoppingListUiState(mode = ShoppingListRangeMode.WEEK, rangeStart = start, rangeEnd = end)
        assertTrue(base.isEmpty)
        assertFalse(base.copy(isLoading = true).isEmpty)
        assertFalse(base.copy(errorMessage = "boom").isEmpty)
        assertFalse(base.copy(items = listOf(shoppingItem("a"))).isEmpty)
    }

    @Test
    fun spendingIsEmptyWithoutLineItems() {
        val base = SpendingReportsUiState(mode = ShoppingListRangeMode.WEEK, rangeStart = start, rangeEnd = end)
        assertTrue(base.isEmpty)
        assertTrue(base.copy(report = SpendingReport(start, end, 0.0, emptyList(), emptyList())).isEmpty)
        assertFalse(base.copy(isLoading = true).isEmpty)
        assertFalse(base.copy(errorMessage = "boom").isEmpty)
    }
}
