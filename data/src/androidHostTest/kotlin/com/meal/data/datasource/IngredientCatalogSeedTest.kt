package com.meal.data.datasource

import com.meal.data.inMemoryDatabase
import com.meal.domain.model.IngredientCatalog
import com.meal.domain.model.enums.IngredientCategory
import com.meal.domain.model.normalizedIngredientName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class IngredientCatalogSeedTest {

    private val database = inMemoryDatabase()
    private val queries = database.ingredientQueries

    @Test
    fun catalogIsSeededOnceWithCategories() = runTest {
        IngredientLocalDataSource(database, Dispatchers.Unconfined).seedCatalog()
        IngredientLocalDataSource(database, Dispatchers.Unconfined).seedCatalog()

        val rows = queries.selectAllIngredients().executeAsList()
        assertEquals(IngredientCatalog.entries.size, rows.size)
        assertTrue(rows.all { it.category != null })
    }

    @Test
    fun existingRowKeepsItsCasingAndGetsACategory() = runTest {
        queries.insertIngredientDefinition(Uuid.random().toString(), "garlic", "garlic")

        val ingredients = IngredientLocalDataSource(database, Dispatchers.Unconfined).ingredients.first()

        val garlic = ingredients.single { normalizedIngredientName(it.name) == "garlic" }
        assertEquals("garlic", garlic.name)
        assertEquals(IngredientCategory.VEGETABLE, garlic.category)
    }

    @Test
    fun typedIngredientOutsideTheCatalogHasNoCategory() = runTest {
        queries.insertIngredientDefinition(Uuid.random().toString(), "Grandma's Spice Mix", "grandma's spice mix")

        val ingredients = IngredientLocalDataSource(database, Dispatchers.Unconfined).ingredients.first()

        assertNull(ingredients.single { it.name == "Grandma's Spice Mix" }.category)
    }

    @Test
    fun everyStarterRecipeIngredientIsInTheCatalog() {
        val missing = RecipeSeedData.recipes()
            .flatMap { recipe -> recipe.ingredients.map { it.ingredient.name } }
            .distinct()
            .filter { IngredientCatalog.find(it) == null }

        assertEquals(emptyList(), missing)
    }

    @Test
    fun namesAndAliasesNeverPointAtTwoEntries() {
        val keys = IngredientCatalog.entries.flatMap { entry ->
            (listOf(entry.name) + entry.aliases).map { normalizedIngredientName(it) }
        }
        assertEquals(emptyList(), keys.groupBy { it }.filterValues { it.size > 1 }.keys.toList())
    }

    @Test
    fun eachSheetFillsItsTilesInOrder() {
        IngredientCatalog.entries.groupBy { it.sheet }.forEach { (sheet, entries) ->
            assertEquals(entries.indices.toList(), entries.map { it.tile }.sorted(), sheet)
        }
    }

    @Test
    fun aliasFindsTheSameEntryAsTheName() {
        assertEquals("String Beans", IngredientCatalog.find("  sitaw ")?.name)
        assertEquals("Pork Belly", IngredientCatalog.find("Liempo")?.name)
    }
}
