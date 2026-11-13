package com.meal.data.model

import com.meal.domain.model.Instruction
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class RecipeDto(
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
    val isFavorite: Boolean = false,
    val isUserCreated: Boolean = true,
    val cuisine: Cuisine? = null
)
