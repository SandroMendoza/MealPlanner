package com.meal.data.datasource.seed

import com.meal.data.model.RecipeDto
import com.meal.domain.model.BUNDLED_IMAGE_PREFIX
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** A built-in recipe whose photo is the bundled file `files/recipes/<image>.webp`. */
@OptIn(ExperimentalUuidApi::class)
internal fun recipe(
    name: String,
    image: String,
    mealTypes: Set<MealType>,
    description: String,
    category: FoodCategory,
    minutes: Int,
    servings: Int,
    ingredients: List<RecipeIngredient>,
    steps: List<String>,
) = RecipeDto(
    id = Uuid.random(),
    name = name,
    description = description,
    mealTypes = mealTypes,
    category = category,
    preparationTimeMinutes = minutes,
    servings = servings,
    imageUrl = "$BUNDLED_IMAGE_PREFIX$image.webp",
    ingredients = ingredients,
    instructions = steps.mapIndexed { index, step -> Instruction(index + 1, step) },
)

@OptIn(ExperimentalUuidApi::class)
internal fun ingredient(name: String, quantity: Double, unit: MeasurementUnit) =
    RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), name), quantity, unit)
