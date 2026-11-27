package com.meal.data.datasource

import com.meal.data.inMemoryDatabase
import com.meal.data.model.RecipeDto
import com.meal.data.repository.RecipeRepositoryImpl
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BuiltInRecipeUpdateTest {

    private var adoboMinutes = 60
    private var adoboSteps = listOf("Marinate", "Simmer")

    private fun builtIns(): List<RecipeDto> = listOf(
        builtIn("Chicken Adobo", "adobo", adoboMinutes, adoboSteps),
        builtIn("Pancit Canton", "pancit", 30, listOf("Boil", "Fry")),
    )

    private val database = inMemoryDatabase()
    private val repository = RecipeRepositoryImpl(RecipeLocalDataSource(database, Dispatchers.Unconfined, ::builtIns))

    private fun builtIn(name: String, image: String, minutes: Int, steps: List<String>) = RecipeDto(
        id = Uuid.random(),
        name = name,
        description = "$name description",
        categories = setOf(FoodCategory.MAIN_DISH),
        preparationTimeMinutes = minutes,
        servings = 4,
        imageUrl = "seed:$image.webp",
        ingredients = listOf(RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Garlic"), 3.0, MeasurementUnit.CLOVE)),
        instructions = steps.mapIndexed { index, step -> Instruction(index + 1, step) },
        isUserCreated = false,
        cuisines = setOf(Cuisine.FILIPINO),
        mealTypes = setOf(MealType.DINNER)
    )

    private suspend fun recipeNamed(name: String) =
        assertNotNull(repository.getAllRecipes().first().firstOrNull { it.name == name })

    @Test
    fun uneditedBuiltInRecipeGetsTheNewBuiltInContentAndKeepsItsIdAndFavourite() = runTest {
        val before = recipeNamed("Chicken Adobo")
        repository.setFavorite(before.id, true)

        adoboMinutes = 75
        adoboSteps = listOf("Marinate", "Sear", "Simmer")
        val after = recipeNamed("Chicken Adobo")

        assertEquals(before.id, after.id)
        assertTrue(after.isFavorite)
        assertEquals(75, after.preparationTimeMinutes)
        assertEquals(listOf("Marinate", "Sear", "Simmer"), after.instructions.map { it.description })
        assertEquals(listOf("Garlic"), after.ingredients.map { it.ingredient.name })
    }

    @Test
    fun editedBuiltInRecipeIsLeftAlone() = runTest {
        val seeded = recipeNamed("Chicken Adobo")
        repository.updateRecipe(seeded.copy(servings = 8))

        adoboMinutes = 75
        val after = recipeNamed("Chicken Adobo")

        assertEquals(60, after.preparationTimeMinutes)
        assertEquals(8, after.servings)
    }

    @Test
    fun deletedBuiltInRecipeStaysDeletedAndUnchanged() = runTest {
        val seeded = recipeNamed("Chicken Adobo")
        repository.softDeleteRecipe(seeded.id)

        adoboMinutes = 75
        val names = repository.getAllRecipes().first().map { it.name }

        assertEquals(listOf("Pancit Canton"), names)
        assertEquals(60, assertNotNull(repository.getRecipeById(seeded.id).first()).preparationTimeMinutes)
    }

    @Test
    fun updatePassRunsOnlyWhenTheBuiltInRecipesChange() = runTest {
        repository.getAllRecipes().first()
        val stored = database.recipeQueries.selectBuiltInRecipesFingerprint().executeAsOne()

        repository.getAllRecipes().first()
        assertEquals(stored, database.recipeQueries.selectBuiltInRecipesFingerprint().executeAsOne())

        adoboMinutes = 75
        repository.getAllRecipes().first()
        assertTrue(stored != database.recipeQueries.selectBuiltInRecipesFingerprint().executeAsOne())
    }
}
