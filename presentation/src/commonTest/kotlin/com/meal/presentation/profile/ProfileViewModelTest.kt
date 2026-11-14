package com.meal.presentation.profile

import com.meal.domain.model.UserProfile
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetUserProfileUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.UpdateUserProfileUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.fakes.FakeMealPlanRepository
import com.meal.presentation.fakes.FakeRecipeRepository
import com.meal.presentation.fakes.FakeUserProfileRepository
import com.meal.presentation.fakes.recipe
import com.meal.presentation.fakes.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class ProfileViewModelTest {

    private val profile = FakeUserProfileRepository(UserProfile(name = "Ana", bio = "Home cook", avatarUrl = "file:a.jpg"))
    private val recipes = FakeRecipeRepository()
    private val mealPlan = FakeMealPlanRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ProfileViewModel(
        GetUserProfileUseCase(profile),
        UpdateUserProfileUseCase(profile),
        GetAllRecipesUseCase(recipes),
        GetWeeklyMealPlanUseCase(mealPlan, recipes)
    )

    @Test
    fun showsProfileDetails() {
        val state = viewModel().state.value

        assertEquals("Ana", state.name)
        assertEquals("Home cook", state.bio)
        assertEquals("file:a.jpg", state.avatarUrl)
    }

    @Test
    fun countsAllRecipesAndUserCreatedOnes() {
        recipes.recipes.value = listOf(
            recipe(isUserCreated = true),
            recipe(isUserCreated = false),
            recipe(isUserCreated = false)
        )

        val state = viewModel().state.value

        assertEquals(3, state.recipeCount)
        assertEquals(1, state.createdCount)
        assertFalse(state.isLoading)
    }

    @Test
    fun countsOnlyThisWeeksPlannedMeals() {
        val dish = recipe()
        recipes.recipes.value = listOf(dish)
        val sunday = currentWeekSunday()
        mealPlan.slots.value = listOf(
            slot(sunday, MealType.LUNCH, dish),
            slot(sunday.plus(6, DateTimeUnit.DAY), MealType.DINNER, dish),
            slot(sunday.plus(7, DateTimeUnit.DAY), MealType.DINNER, dish)
        )

        assertEquals(2, viewModel().state.value.thisWeekCount)
    }

    @Test
    fun editDialogOpensAndCloses() {
        val vm = viewModel()

        vm.openEditDialog()
        assertTrue(vm.state.value.showEditDialog)

        vm.dismissEditDialog()
        assertFalse(vm.state.value.showEditDialog)
    }

    @Test
    fun savingStoresTheProfileAndClosesTheDialog() {
        val vm = viewModel()
        vm.openEditDialog()

        vm.saveProfile("Ben", "Baker", "file:b.jpg")

        assertEquals(UserProfile("Ben", "Baker", "file:b.jpg"), profile.updates.single())
        assertFalse(vm.state.value.showEditDialog)
        assertEquals("Ben", vm.state.value.name)
    }
}
