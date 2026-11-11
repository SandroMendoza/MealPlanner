package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.meal.data.db.MealPlannerDatabase
import com.meal.domain.model.Ingredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class IngredientLocalDataSource(
    database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database.ingredientQueries

    val ingredients: Flow<List<Ingredient>> =
        queries.selectAllIngredients()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> rows.map { Ingredient(Uuid.parse(it.id), it.name) } }
}
