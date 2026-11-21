package com.meal.domain.model

import com.meal.domain.model.enums.IngredientCategory
import com.meal.domain.model.enums.MeasurementUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IngredientUnitsTest {

    @Test
    fun chickenGetsWeightsAndCountsButNotHerbOrDropUnits() {
        val units = assertNotNull(IngredientUnits.unitsFor("Chicken"))

        assertEquals(MeasurementUnit.GRAMS, units.first())
        assertTrue(MeasurementUnit.KILOGRAMS in units && MeasurementUnit.POUND in units)
        assertFalse(MeasurementUnit.SPRIG in units || MeasurementUnit.DROP in units)
    }

    @Test
    fun aliasUsesTheSameUnitsAsItsCatalogueName() {
        assertEquals(IngredientUnits.unitsFor("Chicken"), IngredientUnits.unitsFor(" manok "))
    }

    @Test
    fun exceptionsOverrideTheirCategory() {
        assertEquals(MeasurementUnit.CLOVE, IngredientUnits.unitsFor("Garlic")?.first())
        assertEquals(MeasurementUnit.BUNCH, IngredientUnits.unitsFor("Fresh Basil")?.first())
        val egg = assertNotNull(IngredientUnits.unitsFor("Egg"))
        assertTrue(MeasurementUnit.DOZEN in egg && MeasurementUnit.TRAY in egg)
    }

    @Test
    fun cannedGoodsStartWithCan() {
        assertEquals(MeasurementUnit.CAN, IngredientUnits.unitsFor("Canned Corn")?.first())
    }

    @Test
    fun ingredientOutsideTheCatalogueHasNoRule() {
        assertNull(IngredientUnits.unitsFor("Grandma's Spice Mix"))
    }

    @Test
    fun namingANewIngredientSelectsItsUsualUnit() {
        assertEquals(MeasurementUnit.GRAMS, IngredientUnits.unitAfterRename("Chicke", "Chicken", MeasurementUnit.PIECE))
        assertEquals(MeasurementUnit.PIECE, IngredientUnits.unitAfterRename("Chicken", "Egg", MeasurementUnit.GRAMS))
    }

    @Test
    fun editingTheNameOfTheSameIngredientKeepsTheChosenUnit() {
        assertEquals(MeasurementUnit.POUND, IngredientUnits.unitAfterRename("Chicken", "chicken ", MeasurementUnit.POUND))
        assertEquals(MeasurementUnit.POUND, IngredientUnits.unitAfterRename("Chicken", "Manok", MeasurementUnit.POUND))
    }

    @Test
    fun unknownNameKeepsTheChosenUnit() {
        assertEquals(MeasurementUnit.SPRIG, IngredientUnits.unitAfterRename("Chicken", "Soap", MeasurementUnit.SPRIG))
    }

    @Test
    fun everyExceptionNamesACatalogueIngredient() {
        val missing = IngredientUnits.exceptions.keys.filter { IngredientCatalog.find(it)?.name != it }
        assertEquals(emptyList(), missing)
    }

    @Test
    fun everyCategoryHasUnits() {
        val entryPerCategory = IngredientCatalog.entries.groupBy { it.category }.mapValues { it.value.first() }
        IngredientCategory.entries.forEach { category ->
            entryPerCategory[category]?.let { entry ->
                assertTrue(IngredientUnits.unitsFor(entry.name)!!.isNotEmpty(), "no units for $category")
            }
        }
    }
}
