package com.meal.presentation.fakes

import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.MealSlot
import com.meal.domain.model.Recipe
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun ingredientLine(name: String, quantity: Double, unit: MeasurementUnit = MeasurementUnit.PIECE) =
    RecipeIngredient(id = Uuid.random(), ingredient = Ingredient(Uuid.random(), name), quantity = quantity, unit = unit)

@OptIn(ExperimentalUuidApi::class)
fun recipe(
    name: String = "Chicken Adobo",
    description: String? = null,
    categories: Set<FoodCategory> = setOf(FoodCategory.MAIN_DISH),
    cuisines: Set<Cuisine> = emptySet(),
    mealTypes: Set<MealType> = emptySet(),
    preparationTimeMinutes: Int = 30,
    servings: Int = 4,
    imageUrl: String? = null,
    ingredients: List<RecipeIngredient> = listOf(ingredientLine("Chicken", 1.0, MeasurementUnit.KILOGRAMS)),
    instructions: List<Instruction> = listOf(Instruction(1, "Cook it")),
    isFavorite: Boolean = false,
    isUserCreated: Boolean = true,
    id: Uuid = Uuid.random()
) = Recipe(
    id = id,
    name = name,
    description = description,
    categories = categories,
    preparationTimeMinutes = preparationTimeMinutes,
    servings = servings,
    imageUrl = imageUrl,
    ingredients = ingredients,
    instructions = instructions,
    isFavorite = isFavorite,
    isUserCreated = isUserCreated,
    cuisines = cuisines,
    mealTypes = mealTypes
)

@OptIn(ExperimentalUuidApi::class)
fun slot(date: LocalDate, mealType: MealType, recipe: Recipe, id: Uuid = Uuid.random()) =
    MealSlot(id = id, date = date, mealType = mealType, recipeId = recipe.id)

fun shoppingItem(
    key: String,
    name: String = key,
    quantity: Double = 1.0,
    unit: MeasurementUnit = MeasurementUnit.PIECE,
    checked: Boolean = false,
    price: Double? = null,
    groupId: String? = null,
    atHome: Boolean = false
) = ShoppingListItem(
    itemKey = key,
    name = name,
    totalQuantity = quantity,
    unit = unit,
    checked = checked,
    price = price,
    groupId = groupId,
    atHome = atHome
)
