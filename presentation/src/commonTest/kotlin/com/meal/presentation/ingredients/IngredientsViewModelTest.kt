package com.meal.presentation.ingredients

import com.meal.domain.model.Ingredient
import com.meal.domain.model.enums.IngredientCategory
import com.meal.domain.usecase.AddIngredientsUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.SetIngredientImageUseCase
import com.meal.domain.usecase.SetIngredientStapleUseCase
import com.meal.presentation.fakes.FakeIngredientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class IngredientsViewModelTest {

    private val salt = Ingredient(Uuid.random(), "Salt", IngredientCategory.SPICE_AND_HERB)
    private val garlic = Ingredient(Uuid.random(), "Garlic", IngredientCategory.VEGETABLE)
    private val spiceMix = Ingredient(Uuid.random(), "Grandma's Spice Mix")
    private val repository = FakeIngredientRepository(listOf(salt, garlic, spiceMix))

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = IngredientsViewModel(
        GetAllIngredientsUseCase(repository),
        AddIngredientsUseCase(repository),
        SetIngredientImageUseCase(repository),
        SetIngredientStapleUseCase(repository),
        computeDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun groupsByCategoryWithTheUsersOwnIngredientsFirst() {
        val sections = viewModel().state.value.sections

        assertEquals(listOf(null, IngredientCategory.VEGETABLE, IngredientCategory.SPICE_AND_HERB), sections.map { it.category })
        assertEquals(listOf("Grandma's Spice Mix"), sections.first().ingredients.map { it.name })
    }

    @Test
    fun staplesFilterFollowsDefaultsAndUserChoices() {
        val vm = viewModel()
        vm.setFilter(IngredientsFilter.STAPLES)
        assertEquals(listOf("Salt"), vm.state.value.sections.flatMap { it.ingredients }.map { it.name })

        vm.setStaple(salt.id, false)
        vm.setStaple(garlic.id, true)

        assertEquals(listOf("Garlic"), vm.state.value.sections.flatMap { it.ingredients }.map { it.name })
    }

    @Test
    fun searchShowsOneRankedListOfMatches() {
        val vm = viewModel()
        vm.updateQuery("gar")

        val sections = vm.state.value.sections
        assertEquals(1, sections.size)
        assertTrue(sections.single().isSearchResult)
        assertEquals(listOf("Garlic"), sections.single().ingredients.map { it.name })
    }

    @Test
    fun addsNewIngredientsAndChangesAndResetsAPicture() {
        val vm = viewModel()
        vm.addIngredients(listOf("Lola's Adobo Mix", "salt"))
        assertTrue(vm.state.value.exists("lola's adobo mix"))
        assertEquals(4, vm.state.value.ingredients.size)

        vm.changeImage(garlic.id, "file:///photo.jpg")
        assertEquals("file:///photo.jpg", vm.state.value.ingredients.single { it.id == garlic.id }.imageUri)
        vm.resetImage(garlic.id)
        assertNull(vm.state.value.ingredients.single { it.id == garlic.id }.imageUri)
    }

    @Test
    fun aCatalogueAliasCountsAsItsIngredient() {
        val state = IngredientsUiState(ingredients = listOf(Ingredient(Uuid.random(), "Ube Halaya", IngredientCategory.BAKING_AND_SWEETS)))

        assertTrue(state.exists("ube jam"))
    }
}
