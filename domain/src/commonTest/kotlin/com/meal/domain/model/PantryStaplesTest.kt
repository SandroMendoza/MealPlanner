package com.meal.domain.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PantryStaplesTest {

    @Test
    fun staplesMatchIgnoringCaseAndSpaces() {
        assertTrue(PantryStaples.isStaple("Salt"))
        assertTrue(PantryStaples.isStaple(" soy sauce "))
        assertFalse(PantryStaples.isStaple("Rice"))
        assertFalse(PantryStaples.isStaple("Garlic"))
    }

    @Test
    fun everyStapleIsACatalogueIngredient() {
        val staples = listOf("Water", "Ice", "Salt", "Sugar", "Brown Sugar", "Ground Black Pepper", "Whole Peppercorns",
            "Cooking Oil", "Olive Oil", "Soy Sauce", "Fish Sauce", "Vinegar", "Cornstarch", "All-Purpose Flour", "Bay Leaves")
        staples.forEach { name ->
            assertTrue(PantryStaples.isStaple(name), name)
            assertTrue(IngredientCatalog.find(name) != null, "$name is not in the catalogue")
        }
    }
}
