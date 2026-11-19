package com.meal.planner.components

import mealplanner.shared.generated.resources.Res
import org.jetbrains.compose.resources.MissingResourceException

private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

/**
 * File name stem of an ingredient's bundled picture: the lowercased name with every run of
 * other characters turned into `_` ("Soy sauce" → `soy_sauce`).
 */
fun ingredientImageSlug(name: String): String =
    name.trim().lowercase().replace(NON_ALPHANUMERIC, "_").trim('_')

/**
 * URI of the ingredient's picture under `composeResources/files/ingredients/`, or null when no
 * picture is bundled for it.
 */
fun ingredientImageModel(name: String): String? =
    try {
        Res.getUri("files/ingredients/${ingredientImageSlug(name)}.webp")
    } catch (_: MissingResourceException) {
        null
    }
