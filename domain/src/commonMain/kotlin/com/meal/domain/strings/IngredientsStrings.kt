package com.meal.domain.strings

import com.meal.domain.model.enums.IngredientCategory

/** Text for the Ingredients screen, kept apart so [Strings] stays within the JVM's parameter limit. */
data class IngredientsStrings(
    val categoryName: (IngredientCategory) -> String,
    val title: String,
    val searchPlaceholder: String,
    val filterAll: String,
    val mine: String,
    val noResults: String,
    val add: String,
    val addFieldLabel: String,
    val addHint: String,
    val alreadyExists: String,
    val save: (count: Int) -> String,
    val defaultPicture: String,
    val staple: String,
    val stapleHint: String,
    val removeFromListCd: (name: String) -> String
)
