package com.meal.data.repository

import com.meal.data.datasource.IngredientLocalDataSource
import com.meal.domain.model.Ingredient
import com.meal.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow

class IngredientRepositoryImpl(private val localDataSource: IngredientLocalDataSource) : IngredientRepository {

    override fun getAllIngredients(): Flow<List<Ingredient>> = localDataSource.ingredients
}
