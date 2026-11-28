package com.meal.domain.repository

import com.meal.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface IngredientRepository {
    fun getAllIngredients(): Flow<List<Ingredient>>

    /** Adds an ingredient for every name that doesn't exist yet and returns how many were added. */
    suspend fun addIngredients(names: List<String>): Int

    /** Sets the ingredient's picture; null goes back to the built-in one. */
    suspend fun setImage(id: Uuid, imageUri: String?)

    suspend fun setStaple(id: Uuid, isStaple: Boolean)
}
