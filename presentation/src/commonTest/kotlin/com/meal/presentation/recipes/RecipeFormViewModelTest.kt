package com.meal.presentation.recipes

import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.usecase.AddRecipeUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.UpdateRecipeUseCase
import com.meal.presentation.fakes.FakeIngredientRepository
import com.meal.presentation.fakes.FakeRecipeRepository
import com.meal.presentation.fakes.ingredientLine
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
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class RecipeFormViewModelTest {

    private val existing = recipe(
        name = "Kare-Kare",
        description = "Oxtail in peanut sauce",
        category = FoodCategory.MAIN_DISH,
        cuisine = Cuisine.FILIPINO,
        preparationTimeMinutes = 120,
        servings = 6,
        imageUrl = "seed:kare_kare.webp",
        ingredients = listOf(ingredientLine("Oxtail", 1.5, MeasurementUnit.KILOGRAMS)),
        instructions = listOf(Instruction(2, "Add peanut sauce"), Instruction(1, "Simmer oxtail"))
    )
    private val other = recipe(name = "Pancit")
    private val recipes = FakeRecipeRepository(listOf(existing, other))
    private val ingredients = FakeIngredientRepository(listOf(Ingredient(Uuid.random(), "Garlic")))

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(recipeId: Uuid? = null) = RecipeFormViewModel(
        recipeId = recipeId,
        getRecipeByIdUseCase = GetRecipeByIdUseCase(recipes),
        addRecipeUseCase = AddRecipeUseCase(recipes),
        updateRecipeUseCase = UpdateRecipeUseCase(recipes),
        getAllIngredientsUseCase = GetAllIngredientsUseCase(ingredients),
        getAllRecipesUseCase = GetAllRecipesUseCase(recipes)
    )

    private fun RecipeFormViewModel.fillValidForm(name: String = "Lumpia") {
        updateName(name)
        addIngredient(IngredientFormRow("Wrapper", "20", MeasurementUnit.PIECE))
        updateInstructionRow(0, InstructionFormRow("Roll and fry"))
    }

    @Test
    fun newFormStartsEmptyWithSuggestionsLoaded() {
        val state = viewModel().state.value

        assertFalse(state.isEditMode)
        assertEquals("", state.name)
        assertEquals(listOf("Garlic"), state.availableIngredients.map { it.name })
        assertEquals(setOf("Kare-Kare", "Pancit"), state.existingRecipeNames.toSet())
        assertEquals(listOf(InstructionFormRow()), state.instructions)
    }

    @Test
    fun editFormLoadsTheRecipeWithStepsInOrder() {
        val state = viewModel(existing.id).state.value

        assertTrue(state.isEditMode)
        assertFalse(state.isLoading)
        assertEquals("Kare-Kare", state.name)
        assertEquals("Oxtail in peanut sauce", state.description)
        assertEquals(Cuisine.FILIPINO, state.cuisine)
        assertEquals("120", state.preparationTimeMinutes)
        assertEquals("6", state.servings)
        assertEquals("seed:kare_kare.webp", state.imageUrl)
        assertEquals(listOf(IngredientFormRow("Oxtail", "1.5", MeasurementUnit.KILOGRAMS)), state.ingredients)
        assertEquals(listOf("Simmer oxtail", "Add peanut sauce"), state.instructions.map { it.description })
    }

    @Test
    fun editFormDoesNotTreatItsOwnNameAsADuplicate() {
        val state = viewModel(existing.id).state.value

        assertEquals(listOf("Pancit"), state.existingRecipeNames)
        assertFalse(state.isDuplicateName)
    }

    @Test
    fun editFormForAMissingRecipeShowsAnError() {
        val state = viewModel(Uuid.random()).state.value

        assertEquals("Recipe not found", state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun cuisineTogglesOnAndOff() {
        val vm = viewModel()

        vm.updateCuisine(Cuisine.KOREAN)
        assertEquals(Cuisine.KOREAN, vm.state.value.cuisine)
        vm.updateCuisine(Cuisine.KOREAN)
        assertNull(vm.state.value.cuisine)
    }

    @Test
    fun simpleFieldUpdatesAreReflected() {
        val vm = viewModel()

        vm.updateDescription("Crispy")
        vm.updateCategory(FoodCategory.SIDE_DISH)
        vm.updatePreparationTime("25")
        vm.updateServings("3")
        vm.updateImageUrl("file:x.jpg")

        val state = vm.state.value
        assertEquals("Crispy", state.description)
        assertEquals(FoodCategory.SIDE_DISH, state.category)
        assertEquals("25", state.preparationTimeMinutes)
        assertEquals("3", state.servings)
        assertEquals("file:x.jpg", state.imageUrl)
    }

    @Test
    fun ingredientAndInstructionRowsCanBeAddedEditedAndRemoved() {
        val vm = viewModel()

        vm.addIngredient(IngredientFormRow("Garlic", "3", MeasurementUnit.CLOVE))
        vm.addIngredient(IngredientFormRow("Pepper", "", MeasurementUnit.PINCH))
        vm.updateIngredientRow(1, IngredientFormRow("Salt", "1", MeasurementUnit.TEASPOON))
        vm.removeIngredientRow(0)
        assertEquals(listOf(IngredientFormRow("Salt", "1", MeasurementUnit.TEASPOON)), vm.state.value.ingredients)

        vm.updateInstructionRow(0, InstructionFormRow("Mix"))
        vm.addInstructionRow()
        vm.removeInstructionRow(1)
        assertEquals(listOf(InstructionFormRow("Mix")), vm.state.value.instructions)
    }

    @Test
    fun aStepCanBeSplitMovedAndAllStepsReplaced() {
        val vm = viewModel()
        vm.updateInstructionRow(0, InstructionFormRow("Boil"))
        vm.addInstructionRow()
        vm.updateInstructionRow(1, InstructionFormRow("Serve"))

        vm.replaceInstructionRow(0, listOf("Boil water", "Add rice"))
        assertEquals(listOf("Boil water", "Add rice", "Serve"), vm.state.value.instructions.map { it.description })

        vm.moveInstructionRow(2, 0)
        assertEquals(listOf("Serve", "Boil water", "Add rice"), vm.state.value.instructions.map { it.description })
        vm.moveInstructionRow(0, 5)
        assertEquals(listOf("Serve", "Boil water", "Add rice"), vm.state.value.instructions.map { it.description })

        vm.replaceInstructions(listOf("Only step"))
        assertEquals(listOf(InstructionFormRow("Only step")), vm.state.value.instructions)
        vm.replaceInstructions(emptyList())
        assertEquals(listOf(InstructionFormRow()), vm.state.value.instructions)
    }

    @Test
    fun saveDoesNothingWhileTheFormIsInvalid() {
        val vm = viewModel()
        vm.updateName("Lumpia")

        vm.save()

        assertTrue(recipes.added.isEmpty())
        assertFalse(vm.state.value.saved)
    }

    @Test
    fun saveDoesNothingForADuplicateName() {
        val vm = viewModel()
        vm.fillValidForm(name = " pancit ")

        vm.save()

        assertTrue(recipes.added.isEmpty())
    }

    @Test
    fun savingANewRecipeAddsItWithBlankRowsDroppedAndDefaultsApplied() {
        val vm = viewModel()
        vm.fillValidForm()
        vm.addIngredient(IngredientFormRow())
        vm.addInstructionRow()
        vm.addInstructionRow()
        vm.updateInstructionRow(2, InstructionFormRow("Serve"))

        vm.save()

        val saved = recipes.added.single()
        assertTrue(vm.state.value.saved)
        assertFalse(vm.state.value.isSaving)
        assertEquals("Lumpia", saved.name)
        assertNull(saved.description)
        assertNull(saved.imageUrl)
        assertEquals(0, saved.preparationTimeMinutes)
        assertEquals(1, saved.servings)
        assertEquals(listOf("Wrapper"), saved.ingredients.map { it.ingredient.name })
        assertEquals(20.0, saved.ingredients.single().quantity)
        assertEquals(listOf(Instruction(1, "Roll and fry"), Instruction(2, "Serve")), saved.instructions)
    }

    @Test
    fun savingAnEditedRecipeUpdatesItInPlace() {
        val vm = viewModel(existing.id)
        vm.updateName("Kare-Kare Special")
        vm.updateServings("8")

        vm.save()

        val saved = recipes.updated.single()
        assertTrue(recipes.added.isEmpty())
        assertEquals(existing.id, saved.id)
        assertEquals("Kare-Kare Special", saved.name)
        assertEquals(8, saved.servings)
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun aFailedSaveShowsTheError() {
        recipes.saveFailure = IllegalStateException("disk full")
        val vm = viewModel()
        vm.fillValidForm()

        vm.save()

        assertFalse(vm.state.value.saved)
        assertFalse(vm.state.value.isSaving)
        assertTrue(vm.state.value.errorMessage!!.contains("disk full"))
    }

    @Test
    fun aFormThatWasNotTouchedHasNoUnsavedChanges() {
        assertFalse(viewModel().state.value.hasUnsavedChanges)
        assertFalse(viewModel(existing.id).state.value.hasUnsavedChanges)
    }

    @Test
    fun typingIntoANewFormCountsAsUnsavedChanges() {
        val vm = viewModel()

        vm.updateName("Lumpia")

        assertTrue(vm.state.value.hasUnsavedChanges)
    }

    @Test
    fun emptyStepsAloneAreNotUnsavedChanges() {
        val vm = viewModel()

        vm.addInstructionRow()

        assertFalse(vm.state.value.hasUnsavedChanges)
    }

    @Test
    fun undoingAnEditLeavesNoUnsavedChanges() {
        val vm = viewModel(existing.id)

        vm.updateServings("8")
        assertTrue(vm.state.value.hasUnsavedChanges)

        vm.updateServings("6")
        assertFalse(vm.state.value.hasUnsavedChanges)
    }

    @Test
    fun aSavedRecipeHasNoUnsavedChanges() {
        val vm = viewModel()
        vm.fillValidForm()

        vm.save()

        assertFalse(vm.state.value.hasUnsavedChanges)
    }
}
