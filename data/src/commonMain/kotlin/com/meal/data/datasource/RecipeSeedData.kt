package com.meal.data.datasource

import com.meal.data.model.RecipeDto
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object RecipeSeedData {

    fun recipes(): List<RecipeDto> = baseRecipes().map { it.copy(isUserCreated = false) }

    private fun baseRecipes(): List<RecipeDto> = emptyList()
}
