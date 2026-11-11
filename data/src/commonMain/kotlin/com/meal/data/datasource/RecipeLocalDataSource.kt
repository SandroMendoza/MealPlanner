package com.meal.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.db.RecipeEntity
import com.meal.data.model.RecipeDto
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeLocalDataSource(
    private val database: MealPlannerDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val queries = database.recipeQueries
    private val ingredientQueries = database.ingredientQueries

    val recipes: Flow<List<RecipeDto>> =
        queries.selectActiveRecipes()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> rows.map { toDto(it) } }
            .onStart { seedIfEmpty() }

    private suspend fun seedIfEmpty() = withContext(ioDispatcher) {
        if (queries.selectActiveRecipes().executeAsList().isEmpty()) {
            RecipeSeedData.recipes().forEach { insertRecipeBlocking(it) }
        }
    }

    suspend fun getRecipeById(id: Uuid): RecipeDto? = withContext(ioDispatcher) {
        queries.selectRecipeById(id.toString()).executeAsOneOrNull()?.let { toDto(it) }
    }

    fun recipeById(id: Uuid): Flow<RecipeDto?> =
        queries.selectRecipeById(id.toString())
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { row -> row?.let { toDto(it) } }

    suspend fun insertRecipe(dto: RecipeDto) = withContext(ioDispatcher) {
        insertRecipeBlocking(dto)
    }

    suspend fun updateRecipe(dto: RecipeDto) = withContext(ioDispatcher) {
        database.transaction {
            queries.updateRecipe(
                name = dto.name,
                description = dto.description,
                category = dto.category.name,
                preparationTimeMinutes = dto.preparationTimeMinutes.toLong(),
                servings = dto.servings.toLong(),
                calories = dto.calories?.toLong(),
                imageUrl = dto.imageUrl,
                id = dto.id.toString()
            )
            replaceChildren(dto)
        }
    }

    suspend fun softDeleteRecipe(id: Uuid) = withContext(ioDispatcher) {
        queries.softDeleteRecipe(id.toString())
    }

    suspend fun setFavorite(id: Uuid, isFavorite: Boolean) = withContext(ioDispatcher) {
        queries.updateFavorite(if (isFavorite) 1 else 0, id.toString())
    }

    private fun insertRecipeBlocking(dto: RecipeDto) {
        database.transaction {
            queries.insertRecipe(
                id = dto.id.toString(),
                name = dto.name,
                description = dto.description,
                category = dto.category.name,
                preparationTimeMinutes = dto.preparationTimeMinutes.toLong(),
                servings = dto.servings.toLong(),
                calories = dto.calories?.toLong(),
                imageUrl = dto.imageUrl,
                isUserCreated = if (dto.isUserCreated) 1 else 0
            )
            replaceChildren(dto)
        }
    }

    private fun replaceChildren(dto: RecipeDto) {
        val recipeId = dto.id.toString()
        queries.deleteMealTypesForRecipe(recipeId)
        dto.mealTypes.forEach { queries.insertMealType(recipeId, it.name) }

        queries.deleteRecipeIngredientsForRecipe(recipeId)
        dto.ingredients.forEach { recipeIngredient ->
            queries.insertRecipeIngredient(
                id = recipeIngredient.id.toString(),
                recipeId = recipeId,
                ingredientId = resolveIngredientId(recipeIngredient.ingredient.name),
                quantity = recipeIngredient.quantity,
                unit = recipeIngredient.unit.name
            )
        }

        queries.deleteInstructionsForRecipe(recipeId)
        dto.instructions.forEach { instruction ->
            queries.insertInstruction(
                recipeId = recipeId,
                stepNumber = instruction.stepNumber.toLong(),
                description = instruction.description
            )
        }
    }

    // Matches case-insensitively; first-write-wins on display casing (typing "chicken" when
    // "Chicken" already exists reuses that row — the typed casing is not preserved). Intentional.
    private fun resolveIngredientId(name: String): String {
        val normalized = name.trim().lowercase()
        return ingredientQueries.selectIngredientByNormalizedName(normalized).executeAsOneOrNull()?.id
            ?: Uuid.random().toString().also { ingredientQueries.insertIngredientDefinition(it, name.trim(), normalized) }
    }

    private fun toDto(row: RecipeEntity): RecipeDto {
        val mealTypes = queries.selectMealTypesForRecipe(row.id)
            .executeAsList()
            .map { MealType.valueOf(it.mealType) }
        val ingredients = queries.selectRecipeIngredientsForRecipe(row.id)
            .executeAsList()
            .map { ri ->
                RecipeIngredient(
                    id = Uuid.parse(ri.id),
                    ingredient = Ingredient(Uuid.parse(ri.ingredientId), ri.ingredientName),
                    quantity = ri.quantity,
                    unit = MeasurementUnit.valueOf(ri.unit)
                )
            }
        val instructions = queries.selectInstructionsForRecipe(row.id)
            .executeAsList()
            .map { Instruction(it.stepNumber.toInt(), it.description) }

        return RecipeDto(
            id = Uuid.parse(row.id),
            name = row.name,
            description = row.description,
            mealTypes = mealTypes,
            category = FoodCategory.valueOf(row.category),
            preparationTimeMinutes = row.preparationTimeMinutes.toInt(),
            servings = row.servings.toInt(),
            calories = row.calories?.toInt(),
            imageUrl = row.imageUrl,
            ingredients = ingredients,
            instructions = instructions,
            isFavorite = row.isFavorite != 0L,
            isUserCreated = row.isUserCreated != 0L
        )
    }
}
