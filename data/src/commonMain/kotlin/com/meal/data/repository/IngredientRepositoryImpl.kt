package com.meal.data.repository

import com.meal.data.datasource.IngredientLocalDataSource
import com.meal.domain.model.Ingredient
import com.meal.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class IngredientRepositoryImpl(private val localDataSource: IngredientLocalDataSource) : IngredientRepository {

    override fun getAllIngredients(): Flow<List<Ingredient>> = localDataSource.ingredients

    override suspend fun addIngredients(names: List<String>): Int = localDataSource.addIngredients(names)

    override suspend fun setImage(id: Uuid, imageUri: String?) = localDataSource.setImage(id, imageUri)

    override suspend fun setStaple(id: Uuid, isStaple: Boolean) = localDataSource.setStaple(id, isStaple)
}
