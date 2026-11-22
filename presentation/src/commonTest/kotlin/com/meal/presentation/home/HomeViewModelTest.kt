package com.meal.presentation.home

import com.meal.domain.model.AppSettings
import com.meal.domain.model.UserProfile
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetUserProfileUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.presentation.common.today
import com.meal.presentation.fakes.FakeMealPlanRepository
import com.meal.presentation.fakes.FakeRecipeRepository
import com.meal.presentation.fakes.FakeSettingsRepository
import com.meal.presentation.fakes.FakeTtsSpeaker
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class HomeViewModelTest {

    private val recipes = FakeRecipeRepository()
    private val mealPlan = FakeMealPlanRepository()
    private val profile = FakeUserProfileRepository(UserProfile(name = "Ana"))
    private val settings = FakeSettingsRepository()
    private val tts = FakeTtsSpeaker()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = HomeViewModel(
        GetWeeklyMealPlanUseCase(mealPlan, recipes),
        GetUserProfileUseCase(profile),
        GetSettingsUseCase(settings),
        tts
    )

    @Test
    fun showsTodaysMealsInMealTypeOrder() {
        val breakfast = recipe(name = "Champorado")
        val dinner = recipe(name = "Adobo")
        val tomorrowsLunch = recipe(name = "Pancit")
        recipes.recipes.value = listOf(breakfast, dinner, tomorrowsLunch)
        mealPlan.slots.value = listOf(
            slot(today(), MealType.DINNER, dinner),
            slot(today(), MealType.BREAKFAST, breakfast),
            slot(today().plus(1, DateTimeUnit.DAY), MealType.LUNCH, tomorrowsLunch)
        )

        val state = viewModel().state.value

        assertEquals(listOf("Champorado", "Adobo"), state.todaysEntries.map { it.recipe.name })
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun updatesWhenTodaysPlanChanges() {
        val dinner = recipe(name = "Adobo")
        recipes.recipes.value = listOf(dinner)
        val vm = viewModel()
        assertTrue(vm.state.value.todaysEntries.isEmpty())

        mealPlan.slots.value = listOf(slot(today(), MealType.DINNER, dinner))

        assertEquals(listOf("Adobo"), vm.state.value.todaysEntries.map { it.recipe.name })
    }

    @Test
    fun showsAnErrorWhenThePlanFailsToLoad() {
        mealPlan.loadFailure = IllegalStateException("disk full")

        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertTrue("disk full" in state.errorMessage)
    }

    @Test
    fun greetsTheProfileNameAndFollowsReadAloudSetting() {
        val vm = viewModel()
        assertEquals("Ana", vm.state.value.greetingName)
        assertFalse(vm.state.value.readAloudAvailable)

        settings.settings.value = AppSettings(readAloudEnabled = true)
        profile.profile.value = UserProfile(name = "Ben")

        assertTrue(vm.state.value.readAloudAvailable)
        assertEquals("Ben", vm.state.value.greetingName)
    }

    @Test
    fun readAloudSpeaksThenStopsOnSecondTap() {
        val vm = viewModel()

        vm.toggleReadAloud("Dinner: Adobo")
        assertEquals(listOf("Dinner: Adobo"), tts.spoken)
        assertTrue(vm.state.value.isSpeaking)

        vm.toggleReadAloud("Dinner: Adobo")
        assertEquals(1, tts.stopCount)
        assertFalse(vm.state.value.isSpeaking)
    }

    @Test
    fun readAloudClearsSpeakingWhenUtteranceFinishes() {
        val vm = viewModel()
        vm.toggleReadAloud("Dinner: Adobo")

        tts.finishCurrent()

        assertFalse(vm.state.value.isSpeaking)
    }

    @Test
    fun readAloudIgnoresBlankText() {
        val vm = viewModel()

        vm.toggleReadAloud("  ")

        assertTrue(tts.spoken.isEmpty())
        assertFalse(vm.state.value.isSpeaking)
    }
}
