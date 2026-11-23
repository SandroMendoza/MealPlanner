package com.meal.presentation.recipes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IngredientSearchTest {

    @Test
    fun blankQueryFindsNothing() {
        val result = searchIngredients("  ", listOf("Garlic"))

        assertTrue(result.matches.isEmpty())
        assertFalse(result.hasExactMatch)
    }

    @Test
    fun matchesFromTheFirstLetterWithShortestPrefixMatchFirst() {
        val result = searchIngredients("g", emptyList(), limit = Int.MAX_VALUE)

        assertEquals(IngredientMatch("Garlic"), result.matches.first { it.name.startsWith("Garl") })
        val names = result.matches.map { it.name }
        assertTrue(names.indexOf("Garlic") < names.indexOf("Garlic Powder"))
    }

    @Test
    fun prefixMatchesComeBeforeWordStartsAndWordStartsBeforeOtherMatches() {
        val result = searchIngredients("salt", listOf("Unsalted Butter", "Sea Salt", "Salted Egg"))

        val names = result.matches.map { it.name }
        assertTrue(names.indexOf("Salted Egg") < names.indexOf("Sea Salt"))
        assertTrue(names.indexOf("Sea Salt") < names.indexOf("Unsalted Butter"))
    }

    @Test
    fun aCatalogueAliasFindsTheIngredientAndCountsAsAnExactMatch() {
        val result = searchIngredients("bawang", emptyList())

        assertEquals(IngredientMatch("Garlic", alias = "Bawang"), result.matches.first())
        assertTrue(result.hasExactMatch)
    }

    @Test
    fun userAddedIngredientsAreSearchedAndMatchedIgnoringCase() {
        val result = searchIngredients("LOLA'S sauce", listOf("Lola's Sauce"))

        assertEquals(listOf(IngredientMatch("Lola's Sauce")), result.matches)
        assertTrue(result.hasExactMatch)
    }

    @Test
    fun anUnknownNameHasNoExactMatch() {
        val result = searchIngredients("garli", emptyList())

        assertTrue(result.matches.isNotEmpty())
        assertFalse(result.hasExactMatch)
    }

    @Test
    fun namesKnownTwiceAreListedOnce() {
        val result = searchIngredients("garlic", listOf("garlic", "Garlic"))

        assertEquals(1, result.matches.count { it.name.equals("Garlic", ignoreCase = true) })
        assertTrue(result.matches.any { it.name == "Garlic" })
    }

    @Test
    fun resultsAreCappedAtTheLimit() {
        assertEquals(5, searchIngredients("a", emptyList(), limit = 5).matches.size)
    }
}
