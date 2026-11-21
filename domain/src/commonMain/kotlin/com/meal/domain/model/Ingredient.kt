package com.meal.domain.model

import com.meal.domain.model.enums.IngredientCategory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** [category] is null for ingredients a user typed that are not in [IngredientCatalog]. */
@OptIn(ExperimentalUuidApi::class)
data class Ingredient(
    val id: Uuid,
    val name: String,
    val category: IngredientCategory? = null
)
