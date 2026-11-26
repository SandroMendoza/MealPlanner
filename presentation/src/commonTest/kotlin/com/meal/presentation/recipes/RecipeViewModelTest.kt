package com.meal.presentation.recipes

import com.meal.domain.model.AppSettings
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.presentation.fakes.FakeRecipeRepository
import com.meal.presentation.fakes.FakeSettingsRepository
import com.meal.presentation.fakes.recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class RecipeViewModelTest {

    private val repository = FakeRecipeRepository()
    private val settings = FakeSettingsRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = RecipeViewModel(GetAllRecipesUseCase(repository), GetSettingsUseCase(settings))

    @Test
    fun loadsEveryRecipe() {
        val all = (1..5).map { recipe(name = "Recipe $it") }
        repository.recipes.value = all

        val state = viewModel().state.value

        assertEquals(all.map { it.id }.toSet(), state.recipes.map { it.id }.toSet())
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun keepsEachRecipesPositionWhenTheListChanges() {
        val all = (1..8).map { recipe(name = "Recipe $it") }
        repository.recipes.value = all
        val vm = viewModel()
        val firstOrder = vm.state.value.recipes.map { it.id }

        repository.recipes.value = all.map { if (it == all[3]) it.copy(isFavorite = true) else it }

        assertEquals(firstOrder, vm.state.value.recipes.map { it.id })
    }

    @Test
    fun reshuffleGivesANewOrderAndKeepsSearchAndFilters() {
        val all = (1..30).map { recipe(name = "Recipe $it") }
        repository.recipes.value = all
        val vm = viewModel()
        vm.onSearchQueryChange("Recipe")
        vm.onCategorySelected(FoodCategory.MAIN_DISH)
        val firstOrder = vm.state.value.recipes.map { it.id }

        vm.reshuffle()
        val shuffledOrder = vm.state.value.recipes.map { it.id }

        assertTrue(vm.state.value.isRefreshing)
        assertNotEquals(firstOrder, shuffledOrder)
        assertEquals(firstOrder.toSet(), shuffledOrder.toSet())
        assertEquals("Recipe", vm.state.value.searchQuery)
        assertEquals(FoodCategory.MAIN_DISH, vm.state.value.selectedCategory)

        repository.recipes.value = all.map { if (it == all[3]) it.copy(isFavorite = true) else it }
        assertEquals(shuffledOrder, vm.state.value.recipes.map { it.id })
    }

    @Test
    fun newRecipesJoinWithoutReorderingExistingOnes() {
        val all = (1..6).map { recipe(name = "Recipe $it") }
        repository.recipes.value = all
        val vm = viewModel()
        val firstOrder = vm.state.value.recipes.map { it.id }

        val added = recipe(name = "New")
        repository.recipes.value = all + added

        val newOrder = vm.state.value.recipes.map { it.id }
        assertEquals(firstOrder, newOrder.filterNot { it == added.id })
        assertTrue(added.id in newOrder)
    }

    @Test
    fun showsAnErrorWhenRecipesFailToLoad() {
        repository.loadFailure = IllegalStateException("db locked")

        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertTrue(state.errorMessage!!.contains("db locked"))
    }

    @Test
    fun startsInTheDefaultViewModeAndToggles() {
        settings.settings.value = AppSettings(defaultRecipeView = RecipeViewMode.LIST)
        val vm = viewModel()
        assertEquals(RecipeViewMode.LIST, vm.state.value.viewMode)

        vm.onViewModeToggle()
        assertEquals(RecipeViewMode.GRID, vm.state.value.viewMode)

        vm.onViewModeToggle()
        assertEquals(RecipeViewMode.LIST, vm.state.value.viewMode)
    }

    @Test
    fun selectingTheSameChipTwiceClearsIt() {
        val vm = viewModel()

        vm.onCategorySelected(FoodCategory.SOUP)
        vm.onCuisineSelected(Cuisine.THAI)
        assertEquals(FoodCategory.SOUP, vm.state.value.selectedCategory)
        assertEquals(Cuisine.THAI, vm.state.value.selectedCuisine)

        vm.onCategorySelected(FoodCategory.SOUP)
        vm.onCuisineSelected(Cuisine.THAI)
        assertNull(vm.state.value.selectedCategory)
        assertNull(vm.state.value.selectedCuisine)
    }

    @Test
    fun selectingADifferentChipReplacesTheSelection() {
        val vm = viewModel()

        vm.onCategorySelected(FoodCategory.SOUP)
        vm.onCategorySelected(FoodCategory.DESSERT)

        assertEquals(FoodCategory.DESSERT, vm.state.value.selectedCategory)
    }

    @Test
    fun searchRangesAndTabsUpdateFilteredRecipes() {
        val quickSoup = recipe(name = "Miso Soup", preparationTimeMinutes = 10, isFavorite = true)
        val slowStew = recipe(name = "Beef Stew", preparationTimeMinutes = 150, isUserCreated = false)
        repository.recipes.value = listOf(quickSoup, slowStew)
        val vm = viewModel()

        vm.onSearchQueryChange("stew")
        assertEquals(listOf(slowStew), vm.state.value.filteredRecipes)
        vm.onSearchQueryChange("")

        vm.onPrepTimeRangeChange(0f..30f)
        assertEquals(listOf(quickSoup), vm.state.value.filteredRecipes)
        vm.onPrepTimeRangeChange(0f..180f)

        vm.onFavoritesTabSelected(true)
        assertEquals(listOf(quickSoup), vm.state.value.filteredRecipes)
        vm.onFavoritesTabSelected(false)

        vm.onCreatedOnlyTabSelected(true)
        assertEquals(listOf(quickSoup), vm.state.value.filteredRecipes)
    }

    @Test
    fun mealTypeFilterShowsOnlyRecipesTaggedWithIt() {
        val silog = recipe(name = "Tapsilog", mealTypes = setOf(MealType.BREAKFAST, MealType.LUNCH))
        val stew = recipe(name = "Beef Stew", mealTypes = setOf(MealType.DINNER))
        val untagged = recipe(name = "Leftovers")
        repository.recipes.value = listOf(silog, stew, untagged)
        val vm = viewModel()

        vm.onMealTypeSelected(MealType.BREAKFAST)
        assertEquals(listOf(silog), vm.state.value.filteredRecipes)
        assertTrue(vm.state.value.hasActiveFilters)

        vm.onSearchQueryChange("stew")
        assertTrue(vm.state.value.filteredRecipes.isEmpty())
        vm.onSearchQueryChange("")

        vm.onMealTypeSelected(MealType.BREAKFAST)
        assertNull(vm.state.value.selectedMealType)
        assertEquals(3, vm.state.value.filteredRecipes.size)
    }

    @Test
    fun setMealTypeFilterDoesNotToggle() {
        val vm = viewModel()

        vm.setMealTypeFilter(MealType.DINNER)
        vm.setMealTypeFilter(MealType.DINNER)

        assertEquals(MealType.DINNER, vm.state.value.selectedMealType)
    }

    @Test
    fun clearFiltersResetsEveryFilter() {
        val vm = viewModel()
        vm.onCategorySelected(FoodCategory.SOUP)
        vm.onCuisineSelected(Cuisine.THAI)
        vm.onMealTypeSelected(MealType.SNACK)
        vm.onFavoritesTabSelected(true)
        vm.onCreatedOnlyTabSelected(true)
        vm.onPrepTimeRangeChange(0f..30f)

        vm.clearFilters()

        assertFalse(vm.state.value.hasActiveFilters)
    }
}
