package com.meal.data.datasource

import com.meal.data.model.RecipeDto
import com.meal.domain.model.Ingredient
import com.meal.domain.model.Instruction
import com.meal.domain.model.RecipeIngredient
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object RecipeSeedData {

    fun recipes(): List<RecipeDto> = baseRecipes().map { it.copy(isUserCreated = false) }

    private fun baseRecipes(): List<RecipeDto> = listOf(
        RecipeDto(
            id = Uuid.random(),
            name = "Chicken Adobo",
            description = "Classic Filipino chicken adobo.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.MAIN_DISH,
            preparationTimeMinutes = 60,
            servings = 4,
            calories = 450,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Chicken"), 1.0, MeasurementUnit.KILOGRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Soy Sauce"), 0.5, MeasurementUnit.CUP),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Vinegar"), 0.33, MeasurementUnit.CUP),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Garlic"), 6.0, MeasurementUnit.PIECE)
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
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Flour"), 200.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Milk"), 250.0, MeasurementUnit.MILLILITER),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Egg"), 1.0, MeasurementUnit.PIECE)
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
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Romaine Lettuce"), 300.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Parmesan Cheese"), 50.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Caesar Dressing"), 3.0, MeasurementUnit.TABLESPOON)
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
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Dark Chocolate"), 200.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Butter"), 100.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Sugar"), 150.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Egg"), 2.0, MeasurementUnit.PIECE)
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
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Mango"), 2.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Milk"), 250.0, MeasurementUnit.MILLILITER),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Ice"), 1.0, MeasurementUnit.CUP)
            ),
            instructions = listOf(
                Instruction(1, "Add all ingredients to a blender."),
                Instruction(2, "Blend until smooth."),
                Instruction(3, "Serve immediately.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Sinigang na Baboy sa Sampalok",
            description = "Sour pork soup with tamarind and vegetables.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.SOUP,
            preparationTimeMinutes = 80,
            servings = 6,
            calories = 380,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Pork Belly"), 1.0, MeasurementUnit.KILOGRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Tamarind Mix"), 50.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Kangkong"), 200.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Radish"), 1.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Tomato"), 2.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Boil pork belly with tomatoes and onions until tender."),
                Instruction(2, "Add radish and tamarind mix, simmer until vegetables soften."),
                Instruction(3, "Add kangkong and season with fish sauce before serving.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Kare-Kare",
            description = "Oxtail stew in a rich peanut sauce.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.MAIN_DISH,
            preparationTimeMinutes = 150,
            servings = 6,
            calories = 520,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Oxtail"), 1.0, MeasurementUnit.KILOGRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Peanut Butter"), 1.0, MeasurementUnit.CUP),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Banana Blossom"), 200.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Eggplant"), 2.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "String Beans"), 150.0, MeasurementUnit.GRAMS)
            ),
            instructions = listOf(
                Instruction(1, "Simmer oxtail until fork-tender, reserving the broth."),
                Instruction(2, "Stir in peanut butter and ground rice to thicken the sauce."),
                Instruction(3, "Add vegetables and cook until just tender, then serve with bagoong.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Chicken Tinola",
            description = "Ginger-infused chicken soup with green papaya.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.SOUP,
            preparationTimeMinutes = 50,
            servings = 4,
            calories = 280,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Chicken"), 1.0, MeasurementUnit.KILOGRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Ginger"), 50.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Green Papaya"), 300.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Chili Leaves"), 100.0, MeasurementUnit.GRAMS)
            ),
            instructions = listOf(
                Instruction(1, "Saute ginger, onion, and garlic, then add chicken."),
                Instruction(2, "Pour in water and simmer until chicken is cooked through."),
                Instruction(3, "Add papaya and chili leaves, simmer briefly, and season with fish sauce.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Lumpiang Shanghai",
            description = "Crispy fried spring rolls with seasoned ground pork.",
            mealTypes = listOf(MealType.LUNCH, MealType.SNACK),
            category = FoodCategory.SIDE_DISH,
            preparationTimeMinutes = 55,
            servings = 6,
            calories = 220,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Ground Pork"), 500.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Carrot"), 100.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Onion"), 1.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Spring Roll Wrapper"), 20.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Egg"), 1.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Mix ground pork with minced carrot, onion, and egg."),
                Instruction(2, "Wrap the mixture in spring roll wrappers."),
                Instruction(3, "Deep-fry until golden brown and crisp.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Tortang Talong",
            description = "Grilled eggplant omelet, a Filipino breakfast favorite.",
            mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH),
            category = FoodCategory.MAIN_DISH,
            preparationTimeMinutes = 40,
            servings = 2,
            calories = 210,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Eggplant"), 2.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Egg"), 2.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Ground Pork"), 100.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Onion"), 1.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Grill eggplants until charred, then peel and flatten."),
                Instruction(2, "Dip in beaten egg, optionally topped with sauteed ground pork."),
                Instruction(3, "Pan-fry until the egg is set and golden.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Ginisang Ampalaya",
            description = "Sauteed bitter gourd with egg and tomato.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.SIDE_DISH,
            preparationTimeMinutes = 25,
            servings = 4,
            calories = 150,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Bitter Gourd"), 2.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Egg"), 2.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Tomato"), 1.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Onion"), 1.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Slice bitter gourd thinly and set aside."),
                Instruction(2, "Saute onion, garlic, and tomato, then add bitter gourd."),
                Instruction(3, "Pour in beaten egg and cook until set.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Chicken Sopas",
            description = "Creamy chicken and macaroni soup.",
            mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH),
            category = FoodCategory.SOUP,
            preparationTimeMinutes = 45,
            servings = 4,
            calories = 260,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Chicken Breast"), 300.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Elbow Macaroni"), 200.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Evaporated Milk"), 1.0, MeasurementUnit.CUP),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Carrot"), 1.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Boil chicken breast until cooked, then shred."),
                Instruction(2, "Add macaroni and carrots to the broth and simmer."),
                Instruction(3, "Stir in evaporated milk and shredded chicken before serving.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Fried Chicken",
            description = "Crispy golden fried chicken.",
            mealTypes = listOf(MealType.LUNCH, MealType.DINNER),
            category = FoodCategory.MAIN_DISH,
            preparationTimeMinutes = 25,
            servings = 4,
            calories = 420,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Chicken"), 1.0, MeasurementUnit.KILOGRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Flour"), 200.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Egg"), 1.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Garlic Powder"), 1.0, MeasurementUnit.TABLESPOON)
            ),
            instructions = listOf(
                Instruction(1, "Marinate chicken with garlic powder and salt."),
                Instruction(2, "Dredge in flour and beaten egg."),
                Instruction(3, "Deep-fry until golden brown and cooked through.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Sinangag",
            description = "Classic Filipino garlic fried rice.",
            mealTypes = listOf(MealType.BREAKFAST),
            category = FoodCategory.SIDE_DISH,
            preparationTimeMinutes = 20,
            servings = 4,
            calories = 190,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Cooked Rice"), 4.0, MeasurementUnit.CUP),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Garlic"), 8.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Cooking Oil"), 2.0, MeasurementUnit.TABLESPOON)
            ),
            instructions = listOf(
                Instruction(1, "Fry minced garlic in oil until golden and crisp."),
                Instruction(2, "Add day-old rice and toss until heated through."),
                Instruction(3, "Season with salt and top with the fried garlic bits.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Ginisang Corned Beef",
            description = "Sauteed corned beef with potatoes and onions.",
            mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH),
            category = FoodCategory.MAIN_DISH,
            preparationTimeMinutes = 20,
            servings = 3,
            calories = 310,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Corned Beef"), 340.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Potato"), 1.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Onion"), 1.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Garlic"), 4.0, MeasurementUnit.PIECE)
            ),
            instructions = listOf(
                Instruction(1, "Saute garlic and onion, then add diced potato until tender."),
                Instruction(2, "Stir in corned beef and cook until heated through."),
                Instruction(3, "Serve hot with steamed rice.")
            )
        ),
        RecipeDto(
            id = Uuid.random(),
            name = "Upside-down Mango Cheesecake",
            description = "No-bake cheesecake topped with fresh mango.",
            mealTypes = listOf(MealType.SNACK),
            category = FoodCategory.DESSERT,
            preparationTimeMinutes = 410,
            servings = 8,
            calories = 350,
            imageUrl = null,
            ingredients = listOf(
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Cream Cheese"), 250.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Mango"), 3.0, MeasurementUnit.PIECE),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Graham Crackers"), 200.0, MeasurementUnit.GRAMS),
                RecipeIngredient(Uuid.random(), Ingredient(Uuid.random(), "Condensed Milk"), 1.0, MeasurementUnit.CUP)
            ),
            instructions = listOf(
                Instruction(1, "Line a pan with sliced mangoes and crushed graham crackers."),
                Instruction(2, "Beat cream cheese with condensed milk until smooth, then pour over the base."),
                Instruction(3, "Chill for at least 6 hours before unmolding and serving.")
            )
        )
    )
}
