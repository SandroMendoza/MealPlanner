package com.meal.data.repository

import com.meal.data.datasource.IngredientLocalDataSource
import com.meal.data.datasource.RecipeLocalDataSource
import com.meal.data.datasource.RecipeSeedData
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.inMemoryDatabase
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.Recipe
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class RecipeRepositoryImplTest {

    private val database: MealPlannerDatabase = inMemoryDatabase()
    private val repository = RecipeRepositoryImpl(RecipeLocalDataSource(database, Dispatchers.Unconfined))
    private val ingredients = IngredientRepositoryImpl(IngredientLocalDataSource(database, Dispatchers.Unconfined))

    private val seedCount = RecipeSeedData.recipes().size

    private fun recipe(
        name: String = "Pancit",
        ingredientNames: List<String> = listOf("Noodles", "Carrot")
    ) = Recipe(
        id = Uuid.random(),
        name = name,
        description = "Stir-fried noodles",
        category = FoodCategory.MAIN_DISH,
        preparationTimeMinutes = 30,
        servings = 4,
        imageUrl = null,
        ingredients = ingredientNames.mapIndexed { index, ingredientName ->
            RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), ingredientName), index + 1.5, MeasurementUnit.GRAMS)
        },
        instructions = listOf(Instruction(1, "Boil"), Instruction(2, "Fry")),
        isFavorite = false,
        isUserCreated = true,
        cuisine = Cuisine.CHINESE
    )

    @Test
    fun freshDatabaseIsSeededWithTheBuiltInRecipes() = runTest {
        val recipes = repository.getAllRecipes().first()

        assertEquals(seedCount, recipes.size)
        assertTrue(recipes.all { !it.isUserCreated && it.cuisine != null })
        assertTrue(recipes.all { it.ingredients.isNotEmpty() && it.instructions.isNotEmpty() })
    }

    @Test
    fun everyCuisineHasAtLeastTwentyBuiltInRecipes() {
        val perCuisine = RecipeSeedData.recipes().groupingBy { it.cuisine }.eachCount()

        Cuisine.entries.filter { it != Cuisine.OTHER }.forEach { cuisine ->
            assertTrue((perCuisine[cuisine] ?: 0) >= 20, "$cuisine has ${perCuisine[cuisine] ?: 0}")
        }
    }

    @Test
    fun builtInRecipesHaveUniqueNamesAndPhotos() {
        val seed = RecipeSeedData.recipes()

        assertEquals(seed.size, seed.map { it.name.lowercase() }.toSet().size)
        assertEquals(seed.size, seed.map { it.imageUrl }.toSet().size)
    }

    @Test
    fun databaseWithOtherRecipesGetsTheMissingBuiltInRecipes() = runTest {
        repository.addRecipe(recipe())

        assertEquals(seedCount + 1, repository.getAllRecipes().first().size)
    }

    @Test
    fun renamedBuiltInRecipeIsNotSeededAgain() = runTest {
        val seeded = repository.getAllRecipes().first().first()

        repository.updateRecipe(seeded.copy(name = "Renamed"))

        assertEquals(seedCount, repository.getAllRecipes().first().size)
    }

    @Test
    fun seedRunsOnlyOnce() = runTest {
        repository.getAllRecipes().first()
        repository.getAllRecipes().first()

        assertEquals(seedCount, repository.getAllRecipes().first().size)
    }

    @Test
    fun deletedSeedRecipesAreNotSeededAgain() = runTest {
        val seeded = repository.getAllRecipes().first()

        repository.softDeleteRecipe(seeded.first().id)

        assertEquals(seedCount - 1, repository.getAllRecipes().first().size)
    }

    @Test
    fun addedRecipeReadsBackWithAllItsDetails() = runTest {
        val added = recipe()

        repository.addRecipe(added)
        val stored = assertNotNull(repository.getRecipeById(added.id).first())

        assertEquals(added.name, stored.name)
        assertEquals(added.description, stored.description)
        assertEquals(added.category, stored.category)
        assertEquals(added.preparationTimeMinutes, stored.preparationTimeMinutes)
        assertEquals(added.servings, stored.servings)
        assertEquals(added.cuisine, stored.cuisine)
        assertTrue(stored.isUserCreated)
        assertEquals(added.instructions, stored.instructions)
        assertEquals(
            added.ingredients.map { Triple(it.id, it.ingredient.name, it.quantity to it.unit) }.toSet(),
            stored.ingredients.map { Triple(it.id, it.ingredient.name, it.quantity to it.unit) }.toSet()
        )
    }

    @Test
    fun addedRecipeAppearsInTheListAlongsideTheSeed() = runTest {
        repository.getAllRecipes().first()
        val added = recipe()

        repository.addRecipe(added)
        val recipes = repository.getAllRecipes().first()

        assertEquals(seedCount + 1, recipes.size)
        assertTrue(recipes.any { it.id == added.id })
    }

    @Test
    fun unknownIdReadsAsNull() = runTest {
        assertNull(repository.getRecipeById(Uuid.random()).first())
    }

    @Test
    fun updateReplacesFieldsAndChildRows() = runTest {
        val original = recipe()
        repository.addRecipe(original)
        val updated = original.copy(
            name = "Pancit Canton",
            description = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Cabbage"), 2.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(Instruction(1, "Toss everything")),
            cuisine = null
        )

        repository.updateRecipe(updated)
        val stored = assertNotNull(repository.getRecipeById(original.id).first())

        assertEquals("Pancit Canton", stored.name)
        assertNull(stored.description)
        assertNull(stored.cuisine)
        assertEquals(listOf("Cabbage"), stored.ingredients.map { it.ingredient.name })
        assertEquals(listOf(Instruction(1, "Toss everything")), stored.instructions)
    }

    @Test
    fun softDeletedRecipeLeavesTheListButCanStillBeOpenedById() = runTest {
        val added = recipe()
        repository.addRecipe(added)

        repository.softDeleteRecipe(added.id)

        assertFalse(repository.getAllRecipes().first().any { it.id == added.id })
        assertNotNull(repository.getRecipeById(added.id).first())
    }

    @Test
    fun favoriteFlagIsStored() = runTest {
        val added = recipe()
        repository.addRecipe(added)

        repository.setFavorite(added.id, true)
        assertTrue(assertNotNull(repository.getRecipeById(added.id).first()).isFavorite)

        repository.setFavorite(added.id, false)
        assertFalse(assertNotNull(repository.getRecipeById(added.id).first()).isFavorite)
    }

    @Test
    fun ingredientsAreSharedAcrossRecipesIgnoringCaseAndSpaces() = runTest {
        repository.addRecipe(recipe(name = "A", ingredientNames = listOf("Chicken")))
        repository.addRecipe(recipe(name = "B", ingredientNames = listOf("  chicken ")))

        val chicken = ingredients.getAllIngredients().first().filter { it.name.equals("chicken", ignoreCase = true) }

        assertEquals(listOf("Chicken"), chicken.map { it.name })
    }

    @Test
    fun ingredientListIsSortedByName() = runTest {
        repository.addRecipe(recipe(ingredientNames = listOf("Zucchini", "Apple", "Mango")))

        val names = ingredients.getAllIngredients().first().map { it.name }

        assertEquals(names.sorted(), names)
        assertTrue(names.containsAll(listOf("Apple", "Mango", "Zucchini")))
    }

    @Test
    fun recipeListEmitsAgainAfterAWrite() = runTest {
        val emissions = mutableListOf<List<Recipe>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getAllRecipes().toList(emissions)
        }
        runCurrent()
        val added = recipe()

        repository.addRecipe(added)
        runCurrent()

        assertTrue(emissions.last().any { it.id == added.id })
    }
}
