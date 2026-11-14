package com.meal.presentation.recipes

import com.meal.domain.model.AppSettings
import com.meal.domain.model.Instruction
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.usecase.DeleteRecipeUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.ToggleFavoriteUseCase
import com.meal.presentation.fakes.FakeMealPlanRepository
import com.meal.presentation.fakes.FakeRecipeRepository
import com.meal.presentation.fakes.FakeSettingsRepository
import com.meal.presentation.fakes.FakeTtsSpeaker
import com.meal.presentation.fakes.ingredientLine
import com.meal.presentation.fakes.recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class RecipeDetailViewModelTest {

    private val dish = recipe(
        name = "Sinigang",
        ingredients = listOf(
            ingredientLine("Pork", 0.5, MeasurementUnit.KILOGRAMS),
            ingredientLine("Tamarind", 2.0, MeasurementUnit.PIECE)
        ),
        instructions = listOf(
            Instruction(1, "Boil the pork"),
            Instruction(2, "Add tamarind"),
            Instruction(3, "Season")
        )
    )
    private val recipes = FakeRecipeRepository(listOf(dish))
    private val mealPlan = FakeMealPlanRepository()
    private val settings = FakeSettingsRepository()
    private val tts = FakeTtsSpeaker()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(id: Uuid = dish.id) = RecipeDetailViewModel(
        id,
        GetRecipeByIdUseCase(recipes),
        DeleteRecipeUseCase(recipes, mealPlan),
        ToggleFavoriteUseCase(recipes),
        GetSettingsUseCase(settings),
        tts
    )

    @Test
    fun loadsTheRecipe() {
        val state = viewModel().state.value

        assertEquals(dish, state.recipe)
        assertFalse(state.isLoading)
        assertFalse(state.notFound)
    }

    @Test
    fun unknownIdIsNotFound() {
        assertTrue(viewModel(Uuid.random()).state.value.notFound)
    }

    @Test
    fun loadFailureShowsAnError() {
        recipes.loadFailure = IllegalStateException("corrupt row")

        val state = viewModel().state.value

        assertTrue(state.errorMessage!!.contains("corrupt row"))
        assertFalse(state.notFound)
    }

    @Test
    fun followsTheReadAloudSetting() {
        val vm = viewModel()
        assertFalse(vm.state.value.readAloudAvailable)

        settings.settings.value = AppSettings(readAloudEnabled = true)

        assertTrue(vm.state.value.readAloudAvailable)
    }

    @Test
    fun deletingAnUnplannedRecipeDeletesItStraightAway() {
        val vm = viewModel()

        vm.delete()

        assertTrue(vm.state.value.isDeleted)
        assertEquals(listOf(dish.id), recipes.softDeleted)
    }

    @Test
    fun deletingAPlannedRecipeAsksForConfirmationFirst() {
        val dates = listOf(LocalDate(2030, 1, 5), LocalDate(2030, 1, 6))
        mealPlan.futureDatesByRecipe[dish.id] = dates
        val vm = viewModel()

        vm.delete()

        assertEquals(dates, vm.state.value.pendingDeleteConfirmationDates)
        assertFalse(vm.state.value.isDeleted)
        assertTrue(recipes.softDeleted.isEmpty())

        vm.delete(force = true)

        assertTrue(vm.state.value.isDeleted)
        assertNull(vm.state.value.pendingDeleteConfirmationDates)
        assertEquals(listOf(dish.id), mealPlan.slotsRemovedForRecipe)
        assertEquals(listOf(dish.id), recipes.softDeleted)
    }

    @Test
    fun dismissingTheConfirmationKeepsTheRecipe() {
        mealPlan.futureDatesByRecipe[dish.id] = listOf(LocalDate(2030, 1, 5))
        val vm = viewModel()
        vm.delete()

        vm.dismissDeleteConfirmation()

        assertNull(vm.state.value.pendingDeleteConfirmationDates)
        assertFalse(vm.state.value.isDeleted)
    }

    @Test
    fun toggleFavoriteFlipsTheCurrentValue() {
        val vm = viewModel()

        vm.toggleFavorite()
        assertEquals(dish.id to true, recipes.favoriteCalls.last())
        assertTrue(vm.state.value.recipe!!.isFavorite)

        vm.toggleFavorite()
        assertEquals(dish.id to false, recipes.favoriteCalls.last())
    }

    @Test
    fun readsIngredientsAloudWithFormattedQuantities() {
        val vm = viewModel()

        vm.toggleReadIngredientsAloud()

        assertEquals("0.5 kg Pork. 2 piece Tamarind", tts.spoken.single())
        assertTrue(vm.state.value.ingredientsSpeaking)

        tts.finishCurrent()
        assertFalse(vm.state.value.ingredientsSpeaking)
    }

    @Test
    fun secondTapStopsReadingIngredients() {
        val vm = viewModel()
        vm.toggleReadIngredientsAloud()

        vm.toggleReadIngredientsAloud()

        assertEquals(1, tts.stopCount)
        assertFalse(vm.state.value.ingredientsSpeaking)
    }

    @Test
    fun cookModeReadsEachStepAndAdvancesWhenOneFinishes() {
        val vm = viewModel()

        vm.startCookMode()
        assertTrue(vm.state.value.cookModeActive)
        assertEquals("Step 1. Boil the pork", tts.spoken.last())

        tts.finishCurrent()
        assertEquals(1, vm.state.value.cookModeStepIndex)
        assertEquals("Step 2. Add tamarind", tts.spoken.last())

        tts.finishCurrent()
        tts.finishCurrent()
        assertFalse(vm.state.value.cookModeActive)
        assertFalse(vm.state.value.cookModeSpeaking)
    }

    @Test
    fun interruptedStepStopsSpeakingWithoutAdvancing() {
        val vm = viewModel()
        vm.startCookMode()

        tts.finishCurrent(completedNormally = false)

        assertEquals(0, vm.state.value.cookModeStepIndex)
        assertFalse(vm.state.value.cookModeSpeaking)
        assertTrue(vm.state.value.cookModeActive)
    }

    @Test
    fun manualStepNavigationStaysWithinTheSteps() {
        val vm = viewModel()
        vm.startCookMode()

        vm.previousStep()
        assertEquals(0, vm.state.value.cookModeStepIndex)

        vm.nextStep()
        vm.nextStep()
        vm.nextStep()
        assertEquals(2, vm.state.value.cookModeStepIndex)
        assertEquals("Step 3. Season", tts.spoken.last())
    }

    @Test
    fun playPauseStopsAndResumesTheCurrentStep() {
        val vm = viewModel()
        vm.startCookMode()

        vm.togglePlayPause()
        assertFalse(vm.state.value.cookModeSpeaking)

        vm.togglePlayPause()
        assertTrue(vm.state.value.cookModeSpeaking)
        assertEquals("Step 1. Boil the pork", tts.spoken.last())
    }

    @Test
    fun stoppingCookModeStopsSpeech() {
        val vm = viewModel()
        vm.startCookMode()

        vm.stopCookMode()

        assertFalse(vm.state.value.cookModeActive)
        assertFalse(vm.state.value.cookModeSpeaking)
        assertTrue(tts.stopCount >= 1)
    }
}
