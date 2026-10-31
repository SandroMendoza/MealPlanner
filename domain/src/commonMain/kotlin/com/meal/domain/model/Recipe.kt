package com.meal.domain.model

import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Recipe(
    val id: Uuid,
    val name: String,
    val description: String?,
    val mealTypes: List<MealType>,
    val category: FoodCategory,
    val preparationTimeMinutes: Int,
    val servings: Int,
    val calories: Int?,
    val imageUrl: String?,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<Instruction>,
    val isFavorite: Boolean = false
)
