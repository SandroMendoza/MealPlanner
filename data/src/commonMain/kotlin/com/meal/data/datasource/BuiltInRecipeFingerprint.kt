package com.meal.data.datasource

import com.meal.data.model.RecipeDto

internal const val LEGACY_BUILT_IN_FINGERPRINT = "legacy"

/**
 * A stable fingerprint of the built-in recipe content that updates replace: description,
 * category, time, servings, cuisine, meal types, ingredients and steps. Name and photo are
 * left out because they identify which row a built-in recipe belongs to.
 */
internal fun builtInFingerprint(recipe: RecipeDto): String = fnv1a64(
    buildString {
        append(recipe.description.orEmpty()).append('\u0000')
        append(recipe.category.name).append('\u0000')
        append(recipe.preparationTimeMinutes).append('\u0000')
        append(recipe.servings).append('\u0000')
        append(recipe.cuisine?.name.orEmpty()).append('\u0000')
        recipe.mealTypes.map { it.name }.sorted().forEach { append(it).append(',') }
        append('\u0000')
        recipe.ingredients.forEach {
            append(it.ingredient.name.trim().lowercase()).append('|')
                .append(it.quantity).append('|').append(it.unit.name).append(';')
        }
        append('\u0000')
        recipe.instructions.sortedBy { it.stepNumber }.forEach { append(it.description).append('\n') }
    }
)

internal fun builtInSetFingerprint(fingerprints: Collection<String>): String =
    fnv1a64(fingerprints.joinToString(","))

private fun fnv1a64(text: String): String {
    var hash = -0x340d631b7bdddcdbL // FNV-1a 64-bit offset basis
    for (byte in text.encodeToByteArray()) {
        hash = hash xor (byte.toLong() and 0xff)
        hash *= 0x100000001b3L // FNV-1a 64-bit prime
    }
    return hash.toULong().toString(16)
}
