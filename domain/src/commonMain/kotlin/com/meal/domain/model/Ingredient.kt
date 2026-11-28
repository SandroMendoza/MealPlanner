package com.meal.domain.model

import com.meal.domain.model.enums.IngredientCategory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * [category] is null for ingredients a user typed that are not in [IngredientCatalog].
 * [imageUri] is a picture the user chose, shown instead of the built-in one. [isStaple] is null
 * until the user marks or unmarks the ingredient as a pantry staple; until then [staple]
 * follows [PantryStaples].
 */
@OptIn(ExperimentalUuidApi::class)
data class Ingredient(
    val id: Uuid,
    val name: String,
    val category: IngredientCategory? = null,
    val imageUri: String? = null,
    val isStaple: Boolean? = null
) {
    val staple: Boolean get() = isStaple ?: PantryStaples.isStaple(name)
}
