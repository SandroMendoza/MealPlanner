package com.meal.domain.model

/**
 * Pantry staples most kitchens already have. On the shopping list they start in the
 * "At home" section until the user moves them to the items to buy.
 */
object PantryStaples {
    private val names: Set<String> = listOf(
        "Water",
        "Ice",
        "Salt",
        "Sugar",
        "Brown Sugar",
        "Ground Black Pepper",
        "Whole Peppercorns",
        "Cooking Oil",
        "Olive Oil",
        "Soy Sauce",
        "Fish Sauce",
        "Vinegar",
        "Cornstarch",
        "All-Purpose Flour",
        "Bay Leaves",
    ).mapTo(HashSet()) { normalizedIngredientName(it) }

    fun isStaple(name: String): Boolean = normalizedIngredientName(name) in names
}
