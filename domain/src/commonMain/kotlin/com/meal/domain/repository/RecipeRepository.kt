package com.meal.domain.repository

import com.meal.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RecipeRepository {

    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: Uuid): Flow<Recipe?>
    suspend fun addRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun softDeleteRecipe(id: Uuid)
    suspend fun setFavorite(id: Uuid, isFavorite: Boolean)
}
