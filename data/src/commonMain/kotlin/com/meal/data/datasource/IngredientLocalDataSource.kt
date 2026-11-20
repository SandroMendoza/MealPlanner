package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import com.meal.domain.model.Ingredient
import com.meal.domain.model.IngredientCatalog
import com.meal.domain.model.enums.IngredientCategory
import com.meal.domain.model.normalizedIngredientName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class IngredientLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database.ingredientQueries
    private val seedLock = Mutex()
    private var catalogSeeded = false

    val ingredients: Flow<List<Ingredient>> =
        queries.selectAllIngredients()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows ->
                rows.map { row ->
                    Ingredient(
                        id = Uuid.parse(row.id),
                        name = row.name,
                        category = IngredientCategory.entries.firstOrNull { it.name == row.category }
                    )
                }
            }
            .onStart { seedCatalog() }

    /**
     * Adds every [IngredientCatalog] entry that is missing and sets the category on matching
     * rows that have none, such as rows created by the recipe seed or restored from a backup
     * made before ingredients had categories. Names already present keep their casing.
     */
    suspend fun seedCatalog() = seedLock.withLock {
        if (catalogSeeded) return@withLock
        withContext(ioDispatcher) {
            database.transaction {
                IngredientCatalog.entries.forEach { entry ->
                    val normalized = normalizedIngredientName(entry.name)
                    queries.insertCatalogIngredient(Uuid.random().toString(), entry.name, normalized, entry.category.name)
                    queries.fillMissingCategory(category = entry.category.name, normalizedName = normalized)
                }
            }
        }
        catalogSeeded = true
    }
}
