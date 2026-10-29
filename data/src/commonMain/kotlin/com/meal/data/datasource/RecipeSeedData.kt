package com.meal.data.datasource

import com.meal.data.model.RecipeDto
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object RecipeSeedData {

    fun recipes(): List<RecipeDto> = listOf(
        RecipeDto(
            id = Uuid.random(),
            name = "Chicken Adobo",
            description = "Classic Filipino chicken adobo.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.MAIN_DISH,
            preparationTimeMinutes = 60,
            servings = 4,
            calories = 450,
            imageUrl = "https://media.istockphoto.com/id/2247075317/photo/tender-chicken-adobo-served-on-a-plate-with-savory-sauce.jpg?s=2048x2048&w=is&k=20&c=8YKANaLPMtYF2e7Q1tSpmsTgMPG1dADN382rZkovpLk=",
            ingredients = listOf(
                Ingredient(Uuid.random(), "Chicken", 1.0, MeasurementUnit.KILOGRAMS),
                Ingredient(Uuid.random(), "Soy Sauce", 0.5, MeasurementUnit.CUP),
                Ingredient(Uuid.random(), "Vinegar", 0.33, MeasurementUnit.CUP),
                Ingredient(Uuid.random(), "Garlic", 6.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Marinate chicken in soy sauce and garlic."),
                Instruction(2, "Add vinegar and simmer until chicken is tender."),
                Instruction(3, "Serve hot with rice.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Pancakes",
            description = "Fluffy breakfast pancakes.",
            mealTypes = listOf(MealType.BREAKFAST),
            category = FoodCategory.MAIN_DISH,
            preparationTimeMinutes = 20,
            servings = 2,
            calories = 320,
            imageUrl = "https://media.istockphoto.com/id/172249723/photo/pancakes.jpg?s=2048x2048&w=is&k=20&c=nYyX6mYLwG-lWfpfdWT37H7MB8CIvrNF5MEhJNODE6s=",
            ingredients = listOf(
                Ingredient(Uuid.random(), "Flour", 200.0, MeasurementUnit.GRAMS),
                Ingredient(Uuid.random(), "Milk", 250.0, MeasurementUnit.MILLILITER),
                Ingredient(Uuid.random(), "Egg", 1.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Mix all ingredients until smooth."),
                Instruction(2, "Pour batter onto a hot pan."),
                Instruction(3, "Cook both sides until golden brown.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Caesar Salad",
            description = "Fresh salad with creamy dressing.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.SALAD,
            preparationTimeMinutes = 15,
            servings = 2,
            calories = 180,
            imageUrl = "https://media.istockphoto.com/id/184938052/photo/a-bowl-of-caesar-salad-with-croutons-and-cheese-on-table.jpg?s=2048x2048&w=is&k=20&c=Z17A5nxY403O5X0b-nRHbI6AusWFdI6VpH9hISBLxEU=",
            ingredients = listOf(
                Ingredient(Uuid.random(), "Romaine Lettuce", 300.0, MeasurementUnit.GRAMS),
                Ingredient(Uuid.random(), "Parmesan Cheese", 50.0, MeasurementUnit.GRAMS),
                Ingredient(Uuid.random(), "Caesar Dressing", 3.0, MeasurementUnit.TABLESPOON)
            ),
            instructions = listOf(
                Instruction(1, "Wash and chop the lettuce."),
                Instruction(2, "Mix with dressing."),
                Instruction(3, "Top with parmesan cheese and serve.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Chocolate Brownies",
            description = "Rich and fudgy brownies.",
            mealTypes = listOf(MealType.SNACK),
            category = FoodCategory.DESSERT,
            preparationTimeMinutes = 45,
            servings = 8,
            calories = 280,
            imageUrl = "https://media.istockphoto.com/id/168731372/photo/fresh-homemade-chocolate-brownie.jpg?s=2048x2048&w=is&k=20&c=zXOgoCTKbPM22kI-P0xjRowgwRfvmwPg9Tjyi3mQkso=",
            ingredients = listOf(
                Ingredient(Uuid.random(), "Dark Chocolate", 200.0, MeasurementUnit.GRAMS),
                Ingredient(Uuid.random(), "Butter", 100.0, MeasurementUnit.GRAMS),
                Ingredient(Uuid.random(), "Sugar", 150.0, MeasurementUnit.GRAMS),
                Ingredient(Uuid.random(), "Egg", 2.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Melt chocolate and butter together."),
                Instruction(2, "Mix in sugar and eggs."),
                Instruction(3, "Bake for 25 minutes.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Mango Smoothie",
            description = "Refreshing tropical smoothie.",
            mealTypes = listOf(MealType.BREAKFAST, MealType.SNACK),
            category = FoodCategory.BEVERAGE,
            preparationTimeMinutes = 10,
            servings = 1,
            calories = 220,
            imageUrl = "https://media.istockphoto.com/id/1274658583/photo/delicious-mango-smoothie-in-glass-jar.jpg?s=2048x2048&w=is&k=20&c=lYmTdrvcqKAaJeGFnGc5WjCa9Q4b4At4Q1QShPi8yic=",
            ingredients = listOf(
                Ingredient(Uuid.random(), "Mango", 2.0, MeasurementUnit.PIECE),
                Ingredient(Uuid.random(), "Milk", 250.0, MeasurementUnit.MILLILITER),
                Ingredient(Uuid.random(), "Ice", 1.0, MeasurementUnit.CUP)
            ),
            instructions = listOf(
                Instruction(1, "Add all ingredients to a blender."),
                Instruction(2, "Blend until smooth."),
                Instruction(3, "Serve immediately.")
            )
        )
    )
}
