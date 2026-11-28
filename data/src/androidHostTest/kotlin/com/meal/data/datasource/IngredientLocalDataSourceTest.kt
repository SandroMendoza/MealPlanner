package com.meal.data.datasource

import com.meal.data.inMemoryDatabase
import com.meal.domain.model.enums.IngredientCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class IngredientLocalDataSourceTest {

    private val database = inMemoryDatabase()
    private val dataSource = IngredientLocalDataSource(database, Dispatchers.Unconfined)

    @Test
    fun addsOnlyNamesThatAreNotIngredientsYet() = runTest {
        database.ingredientQueries.insertIngredientDefinition(Uuid.random().toString(), "Lola's Adobo Mix", "lola's adobo mix")

        val added = dataSource.addIngredients(listOf("lola's adobo mix", "  tita's chili oil ", "Tita's Chili Oil", "", "House Seasoning Blend"))

        assertEquals(2, added)
        val names = database.ingredientQueries.selectAllIngredients().executeAsList().map { it.name }
        assertTrue("Tita's Chili Oil" in names)
        assertTrue("House Seasoning Blend" in names)
        assertEquals(1, names.count { it.equals("lola's adobo mix", ignoreCase = true) })
    }

    @Test
    fun catalogueNameGetsItsSpellingAndCategory() = runTest {
        dataSource.addIngredients(listOf("garlic"))

        val garlic = database.ingredientQueries.selectIngredientByNormalizedName("garlic").executeAsOne()
        assertEquals("Garlic", garlic.name)
        assertEquals(IngredientCategory.VEGETABLE.name, garlic.category)
    }

    @Test
    fun catalogueAliasIsNotAddedAgain() = runTest {
        dataSource.seedCatalog()

        val added = dataSource.addIngredients(listOf("ube jam"))

        assertEquals(0, added)
        assertNull(database.ingredientQueries.selectIngredientByNormalizedName("ube jam").executeAsOneOrNull())
    }

    @Test
    fun imageAndStapleAreStoredOnTheIngredient() = runTest {
        val id = Uuid.random()
        database.ingredientQueries.insertIngredientDefinition(id.toString(), "Salt", "salt")

        dataSource.setImage(id, "file:///x/recipe_images/salt.jpg")
        dataSource.setStaple(id, false)
        val salt = dataSource.ingredients.first().single { it.id == id }
        assertEquals("file:///x/recipe_images/salt.jpg", salt.imageUri)
        assertEquals(false, salt.isStaple)
        assertFalse(salt.staple)

        dataSource.setImage(id, null)
        assertNull(dataSource.ingredients.first().single { it.id == id }.imageUri)
    }
}
