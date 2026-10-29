package com.meal.data.repository

import com.meal.data.datasource.RecipeLocalDataSource
import com.meal.data.mappers.RecipeMapper
import com.meal.domain.model.Recipe
import com.meal.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeRepositoryImpl(private val localDataSource: RecipeLocalDataSource) : RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return localDataSource.recipes.map { RecipeMapper.toDomain(it) }
    }

    override fun getRecipeById(id: Uuid): Flow<Recipe?> =
        localDataSource.recipeById(id).map { it?.let { dto -> RecipeMapper.toDomain(dto) } }

    override suspend fun addRecipe(recipe: Recipe) {
        localDataSource.insertRecipe(RecipeMapper.toDto(recipe))
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        localDataSource.updateRecipe(RecipeMapper.toDto(recipe))
    }

    override suspend fun softDeleteRecipe(id: Uuid) {
        localDataSource.softDeleteRecipe(id)
    }
}
