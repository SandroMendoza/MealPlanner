@file:OptIn(ExperimentalUuidApi::class)

package com.meal.domain.fakes

import com.meal.domain.model.Ingredient
import com.meal.domain.model.Recipe
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MeasurementUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun recipe(
    name: String = "Chicken Adobo",
    ingredients: List<RecipeIngredient> = emptyList(),
    id: Uuid = Uuid.random()
): Recipe = Recipe(
    id = id,
    name = name,
    description = null,
    category = FoodCategory.MAIN_DISH,
    preparationTimeMinutes = 30,
    servings = 4,
    imageUrl = null,
    ingredients = ingredients,
    instructions = emptyList()
)

fun ingredient(name: String, quantity: Double, unit: MeasurementUnit): RecipeIngredient =
    RecipeIngredient(
        id = Uuid.random(),
        ingredient = Ingredient(id = Uuid.random(), name = name),
        quantity = quantity,
        unit = unit
    )
