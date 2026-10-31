package com.meal.domain.repository

import com.meal.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    fun getAllIngredients(): Flow<List<Ingredient>>
}
