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
import com.meal.domain.model.ingredientDisplayName
import com.meal.domain.model.enums.Cuisine
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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val builtInRecipes: () -> List<RecipeDto> = RecipeSeedData::recipes
) {
    private val queries = database.recipeQueries
    private val ingredientQueries = database.ingredientQueries

    val recipes: Flow<List<RecipeDto>> =
        queries.selectActiveRecipes()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { rows -> withContext(ioDispatcher) { toDtos(rows) } }
            .onStart { syncBuiltInRecipes() }

    private suspend fun syncBuiltInRecipes() = withContext(ioDispatcher) {
        val seeds = builtInRecipes().map { it to builtInFingerprint(it) }
        seedMissingRecipes(seeds)
        val setFingerprint = builtInSetFingerprint(seeds.map { it.second })
        if (queries.selectBuiltInRecipesFingerprint().executeAsOneOrNull() != setFingerprint) {
            database.transaction {
                updateUneditedBuiltInRecipes(seeds)
                queries.setBuiltInRecipesFingerprint(setFingerprint)
            }
        }
    }

    // Adds every built-in recipe the database has never held. Recipes are only ever
    // soft-deleted, so deleted built-in recipes still count as held and stay deleted. A
    // built-in recipe counts as held when a row has its photo or its name, so renaming it or
    // changing its photo alone doesn't bring the original back.
    private fun seedMissingRecipes(seeds: List<Pair<RecipeDto, String>>) {
        val held = queries.selectAllRecipeNamesAndImages().executeAsList()
        val heldNames = held.mapTo(HashSet()) { normalizedName(it.name) }
        val heldImages = held.mapNotNullTo(HashSet()) { it.imageUrl }
        seeds
            .filter { (seed, _) -> seed.imageUrl !in heldImages && normalizedName(seed.name) !in heldNames }
            .forEach { (seed, fingerprint) -> insertRecipeBlocking(seed, fingerprint) }
    }

    // Brings built-in recipes the user hasn't edited or deleted up to the current built-in
    // content, keeping each row's id so favourites and meal plan entries still point at it.
    // Rows from before fingerprints were kept are only updated while their name and photo
    // both still match the built-in recipe.
    private fun updateUneditedBuiltInRecipes(seeds: List<Pair<RecipeDto, String>>) {
        val byImage = seeds.associateBy { it.first.imageUrl }
        val byName = seeds.associateBy { normalizedName(it.first.name) }
        queries.selectUneditedBuiltInRecipes().executeAsList().forEach { row ->
            val (seed, fingerprint) = byImage[row.imageUrl] ?: byName[normalizedName(row.name)] ?: return@forEach
            if (row.builtInFingerprint == fingerprint) return@forEach
            if (row.builtInFingerprint == LEGACY_BUILT_IN_FINGERPRINT &&
                (seed.imageUrl != row.imageUrl || normalizedName(seed.name) != normalizedName(row.name))
            ) return@forEach
            queries.updateBuiltInRecipe(
                description = seed.description,
                category = encodeCategories(seed.categories),
                preparationTimeMinutes = seed.preparationTimeMinutes.toLong(),
                servings = seed.servings.toLong(),
                cuisine = encodeCuisines(seed.cuisines),
                builtInFingerprint = fingerprint,
                id = row.id
            )
            replaceChildren(seed.copy(id = Uuid.parse(row.id)))
        }
    }

    private fun normalizedName(name: String) = name.trim().lowercase()

    // A recipe's categories and cuisines are stored comma-separated in the single category and
    // cuisine columns, in enum order, so rows written with one value read back as a set of one.
    private fun encodeCategories(categories: Set<FoodCategory>): String =
        categories.sortedBy { it.ordinal }.joinToString(",") { it.name }

    private fun encodeCuisines(cuisines: Set<Cuisine>): String? =
        cuisines.sortedBy { it.ordinal }.joinToString(",") { it.name }.ifEmpty { null }

    private fun decodeCategories(value: String): Set<FoodCategory> =
        value.split(',').mapNotNullTo(LinkedHashSet()) { name -> FoodCategory.entries.firstOrNull { it.name == name.trim() } }

    private fun decodeCuisines(value: String?): Set<Cuisine> =
        value.orEmpty().split(',').mapNotNullTo(LinkedHashSet()) { name -> Cuisine.entries.firstOrNull { it.name == name.trim() } }

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
                category = encodeCategories(dto.categories),
                preparationTimeMinutes = dto.preparationTimeMinutes.toLong(),
                servings = dto.servings.toLong(),
                imageUrl = dto.imageUrl,
                cuisine = encodeCuisines(dto.cuisines),
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

    private fun insertRecipeBlocking(dto: RecipeDto, builtInFingerprint: String? = null) {
        database.transaction {
            queries.insertRecipe(
                id = dto.id.toString(),
                name = dto.name,
                description = dto.description,
                category = encodeCategories(dto.categories),
                preparationTimeMinutes = dto.preparationTimeMinutes.toLong(),
                servings = dto.servings.toLong(),
                imageUrl = dto.imageUrl,
                isUserCreated = if (dto.isUserCreated) 1 else 0,
                cuisine = encodeCuisines(dto.cuisines),
                builtInFingerprint = builtInFingerprint
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
            ?: Uuid.random().toString().also { ingredientQueries.insertIngredientDefinition(it, ingredientDisplayName(name), normalized) }
    }

    // Loads the child rows of every active recipe in one query per table, so building the
    // list costs the same four queries however many recipes there are.
    private fun toDtos(rows: List<RecipeEntity>): List<RecipeDto> {
        val mealTypes = queries.selectMealTypesForActiveRecipes { recipeId, mealType ->
            recipeId to MealType.valueOf(mealType)
        }.executeAsList().groupBy({ it.first }, { it.second })
        val ingredients = queries.selectRecipeIngredientsForActiveRecipes {
                recipeId, id, quantity, unit, ingredientId, ingredientName ->
            recipeId to recipeIngredient(id, quantity, unit, ingredientId, ingredientName)
        }.executeAsList().groupBy({ it.first }, { it.second })
        val instructions = queries.selectInstructionsForActiveRecipes { recipeId, stepNumber, description ->
            recipeId to Instruction(stepNumber.toInt(), description)
        }.executeAsList().groupBy({ it.first }, { it.second })

        return rows.map { row ->
            toDto(
                row = row,
                mealTypes = mealTypes[row.id].orEmpty(),
                ingredients = ingredients[row.id].orEmpty(),
                instructions = instructions[row.id].orEmpty()
            )
        }
    }

    private fun toDto(row: RecipeEntity): RecipeDto = toDto(
        row = row,
        mealTypes = queries.selectMealTypesForRecipe(row.id)
            .executeAsList()
            .map { MealType.valueOf(it.mealType) },
        ingredients = queries.selectRecipeIngredientsForRecipe(row.id)
            .executeAsList()
            .map { recipeIngredient(it.id, it.quantity, it.unit, it.ingredientId, it.ingredientName) },
        instructions = queries.selectInstructionsForRecipe(row.id)
            .executeAsList()
            .map { Instruction(it.stepNumber.toInt(), it.description) }
    )

    private fun recipeIngredient(
        id: String,
        quantity: Double,
        unit: String,
        ingredientId: String,
        ingredientName: String
    ) = RecipeIngredient(
        id = Uuid.parse(id),
        ingredient = Ingredient(Uuid.parse(ingredientId), ingredientName),
        quantity = quantity,
        unit = MeasurementUnit.valueOf(unit)
    )

    private fun toDto(
        row: RecipeEntity,
        mealTypes: List<MealType>,
        ingredients: List<RecipeIngredient>,
        instructions: List<Instruction>
    ): RecipeDto {
        return RecipeDto(
            id = Uuid.parse(row.id),
            name = row.name,
            description = row.description,
            mealTypes = mealTypes.sorted().toSet(),
            categories = decodeCategories(row.category),
            preparationTimeMinutes = row.preparationTimeMinutes.toInt(),
            servings = row.servings.toInt(),
            imageUrl = row.imageUrl,
            ingredients = ingredients,
            instructions = instructions,
            isFavorite = row.isFavorite != 0L,
            isUserCreated = row.isUserCreated != 0L,
            cuisines = decodeCuisines(row.cuisine)
        )
    }
}
