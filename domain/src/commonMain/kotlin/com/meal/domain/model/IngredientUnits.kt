package com.meal.domain.model

import com.meal.domain.model.enums.IngredientCategory
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.model.enums.MeasurementUnit.BAG
import com.meal.domain.model.enums.MeasurementUnit.BOTTLE
import com.meal.domain.model.enums.MeasurementUnit.BOX
import com.meal.domain.model.enums.MeasurementUnit.BUNCH
import com.meal.domain.model.enums.MeasurementUnit.CAN
import com.meal.domain.model.enums.MeasurementUnit.CARTON
import com.meal.domain.model.enums.MeasurementUnit.CENTIMETER
import com.meal.domain.model.enums.MeasurementUnit.CLOVE
import com.meal.domain.model.enums.MeasurementUnit.CUBE
import com.meal.domain.model.enums.MeasurementUnit.CUP
import com.meal.domain.model.enums.MeasurementUnit.DASH
import com.meal.domain.model.enums.MeasurementUnit.DOZEN
import com.meal.domain.model.enums.MeasurementUnit.DROP
import com.meal.domain.model.enums.MeasurementUnit.FILLET
import com.meal.domain.model.enums.MeasurementUnit.FLUID_OUNCE
import com.meal.domain.model.enums.MeasurementUnit.GRAMS
import com.meal.domain.model.enums.MeasurementUnit.HANDFUL
import com.meal.domain.model.enums.MeasurementUnit.HEAD
import com.meal.domain.model.enums.MeasurementUnit.INCH
import com.meal.domain.model.enums.MeasurementUnit.JAR
import com.meal.domain.model.enums.MeasurementUnit.KILOGRAMS
import com.meal.domain.model.enums.MeasurementUnit.LEAF
import com.meal.domain.model.enums.MeasurementUnit.LITER
import com.meal.domain.model.enums.MeasurementUnit.LOAF
import com.meal.domain.model.enums.MeasurementUnit.MILLILITER
import com.meal.domain.model.enums.MeasurementUnit.OUNCE
import com.meal.domain.model.enums.MeasurementUnit.PACK
import com.meal.domain.model.enums.MeasurementUnit.PIECE
import com.meal.domain.model.enums.MeasurementUnit.PINCH
import com.meal.domain.model.enums.MeasurementUnit.POUND
import com.meal.domain.model.enums.MeasurementUnit.SACHET
import com.meal.domain.model.enums.MeasurementUnit.SLICE
import com.meal.domain.model.enums.MeasurementUnit.SPRIG
import com.meal.domain.model.enums.MeasurementUnit.STALK
import com.meal.domain.model.enums.MeasurementUnit.STICK
import com.meal.domain.model.enums.MeasurementUnit.TABLESPOON
import com.meal.domain.model.enums.MeasurementUnit.TEASPOON
import com.meal.domain.model.enums.MeasurementUnit.TO_TASTE
import com.meal.domain.model.enums.MeasurementUnit.TRAY

/**
 * Which measurement units make sense for an ingredient, so a unit picker can offer a short list.
 * Catalogue ingredients get their category's units unless an exception below names them;
 * ingredients outside the catalogue have no rule and may use any unit.
 */
object IngredientUnits {

    private val driedSpice = listOf(TEASPOON, TABLESPOON, GRAMS, PINCH, DASH, PIECE, SACHET, PACK, JAR, BOTTLE, TO_TASTE)

    private val byCategory: Map<IngredientCategory, List<MeasurementUnit>> = mapOf(
        IngredientCategory.MEAT to listOf(GRAMS, KILOGRAMS, OUNCE, POUND, PIECE, SLICE, PACK, TRAY, CAN),
        IngredientCategory.POULTRY to listOf(GRAMS, KILOGRAMS, OUNCE, POUND, PIECE, FILLET, PACK, TRAY),
        IngredientCategory.SEAFOOD to listOf(GRAMS, KILOGRAMS, OUNCE, POUND, PIECE, FILLET, PACK, TRAY, CAN),
        IngredientCategory.VEGETABLE to listOf(
            PIECE, GRAMS, KILOGRAMS, OUNCE, POUND, CUP, SLICE, HEAD, BUNCH, STALK, LEAF, HANDFUL, PACK, BAG, CAN
        ),
        IngredientCategory.FRUIT to listOf(PIECE, GRAMS, KILOGRAMS, OUNCE, POUND, CUP, SLICE, DOZEN, BUNCH, PACK, BAG, CAN),
        IngredientCategory.GRAIN_AND_NOODLES to listOf(GRAMS, KILOGRAMS, CUP, OUNCE, POUND, PIECE, SLICE, PACK, BAG, BOX),
        IngredientCategory.LEGUMES_AND_TOFU to listOf(GRAMS, KILOGRAMS, CUP, OUNCE, POUND, PIECE, PACK, BAG, CAN),
        IngredientCategory.NUTS_AND_SEEDS to listOf(GRAMS, CUP, TABLESPOON, TEASPOON, OUNCE, HANDFUL, PACK, BAG),
        IngredientCategory.DAIRY_AND_EGGS to listOf(
            MILLILITER, LITER, CUP, TABLESPOON, TEASPOON, FLUID_OUNCE, GRAMS, KILOGRAMS, OUNCE, POUND,
            PIECE, SLICE, PACK, CAN, CARTON, BOTTLE
        ),
        IngredientCategory.CONDIMENT_AND_SAUCE to listOf(
            TABLESPOON, TEASPOON, CUP, MILLILITER, LITER, FLUID_OUNCE, GRAMS, DASH, DROP,
            BOTTLE, JAR, CAN, SACHET, PACK, TO_TASTE
        ),
        IngredientCategory.SPICE_AND_HERB to driedSpice,
        IngredientCategory.OILS_AND_FATS to listOf(TABLESPOON, TEASPOON, CUP, MILLILITER, LITER, FLUID_OUNCE, GRAMS, BOTTLE, CAN, PACK),
        IngredientCategory.BAKING_AND_SWEETS to listOf(
            GRAMS, KILOGRAMS, CUP, TABLESPOON, TEASPOON, OUNCE, POUND, MILLILITER, PINCH, DROP,
            PIECE, PACK, BAG, BOX, JAR, BOTTLE, SACHET
        ),
        IngredientCategory.OTHER to listOf(MILLILITER, LITER, CUP, FLUID_OUNCE, GRAMS, PIECE, PACK, BOTTLE, CAN, BOX, BAG, CARTON)
    )

    /** Ingredients whose category's units don't fit them, keyed by catalogue name. */
    internal val exceptions: Map<String, List<MeasurementUnit>> = buildMap {
        val freshHerb = listOf(BUNCH, SPRIG, LEAF, GRAMS, TABLESPOON, CUP, HANDFUL)
        listOf(
            "Fresh Basil", "Fresh Mint", "Fresh Dill", "Fresh Parsley", "Fresh Rosemary", "Fresh Thyme",
            "Fresh Cilantro", "Fresh Chives", "Thai Basil", "Fresh Oregano", "Fresh Sage",
            "Curry Leaves", "Pandan Leaves", "Kaffir Lime Leaves", "Perilla Leaves"
        ).forEach { put(it, freshHerb) }
        put("Bay Leaves", listOf(LEAF, PIECE, GRAMS, PACK))
        put("Garlic", listOf(CLOVE, HEAD, GRAMS, TEASPOON, TABLESPOON, PIECE))
        listOf("Ginger", "Galangal", "Turmeric Root").forEach {
            put(it, listOf(GRAMS, PIECE, CENTIMETER, INCH, TEASPOON, TABLESPOON))
        }
        listOf("Lemongrass", "Celery", "Chinese Celery").forEach { put(it, listOf(STALK, PIECE, BUNCH, GRAMS)) }
        listOf("Egg", "Duck Eggs", "Quail Eggs", "Salted Egg", "Century Egg", "Balut").forEach {
            put(it, listOf(PIECE, DOZEN, TRAY))
        }
        listOf("White Bread", "Whole Wheat Bread").forEach { put(it, listOf(LOAF, SLICE, PIECE, PACK)) }
        put("Pandesal", listOf(PIECE, DOZEN, PACK, BAG))
        listOf("Butter", "Unsalted Butter", "Margarine").forEach {
            put(it, listOf(GRAMS, TABLESPOON, TEASPOON, CUP, STICK, PACK))
        }
        put("Bouillon Cubes", listOf(CUBE, PIECE, PACK))
        listOf("Salt", "Ground Black Pepper", "White Pepper").forEach {
            put(it, listOf(TEASPOON, TO_TASTE) + (driedSpice - TEASPOON - TO_TASTE))
        }
    }

    private val canned = listOf(CAN, GRAMS, OUNCE, PIECE)

    /** Units that fit [name], the usual one first; null when [name] isn't in the catalogue. */
    fun unitsFor(name: String): List<MeasurementUnit>? {
        val entry = IngredientCatalog.find(name) ?: return null
        return exceptions[entry.name]
            ?: canned.takeIf { entry.name.startsWith("Canned ") }
            ?: byCategory.getValue(entry.category)
    }

    /**
     * The unit to select after the ingredient name changes from [oldName] to [newName]: the new
     * ingredient's usual unit when the name now points at a different catalogue ingredient,
     * otherwise [current], so edits that keep the same ingredient keep the chosen unit.
     */
    fun unitAfterRename(oldName: String, newName: String, current: MeasurementUnit): MeasurementUnit {
        val newEntry = IngredientCatalog.find(newName) ?: return current
        if (newEntry == IngredientCatalog.find(oldName)) return current
        return unitsFor(newName)?.first() ?: current
    }
}
