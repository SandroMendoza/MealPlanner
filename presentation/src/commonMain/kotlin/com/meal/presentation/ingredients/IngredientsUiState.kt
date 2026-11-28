package com.meal.presentation.ingredients

import com.meal.domain.model.Ingredient
import com.meal.domain.model.IngredientCatalog
import com.meal.domain.model.enums.IngredientCategory
import com.meal.domain.model.normalizedIngredientName
import com.meal.presentation.recipes.searchIngredients

enum class IngredientsFilter { ALL, STAPLES, CUSTOM }

/**
 * A group of ingredients on the Ingredients screen. [category] is null for ingredients the
 * user added that are not in the catalogue, and for the single flat list of search results
 * ([isSearchResult]).
 */
data class IngredientSection(
    val category: IngredientCategory?,
    val ingredients: List<Ingredient>,
    val isSearchResult: Boolean = false
)

/**
 * [sections] are built from [ingredients], [query] and [filter] by [buildIngredientSections]
 * off the main thread, so they can briefly lag behind a query the user is still typing.
 */
data class IngredientsUiState(
    val ingredients: List<Ingredient> = emptyList(),
    val query: String = "",
    val filter: IngredientsFilter = IngredientsFilter.ALL,
    val sections: List<IngredientSection> = emptyList(),
    val isLoading: Boolean = true
) {
    /**
     * Whether an ingredient with this name already exists, ignoring case and surrounding spaces.
     * A catalogue alias counts as its catalogue ingredient.
     */
    fun exists(name: String): Boolean {
        val normalized = normalizedIngredientName(IngredientCatalog.find(name)?.name ?: name)
        return ingredients.any { normalizedIngredientName(it.name) == normalized }
    }
}

/**
 * Without a search, the [filter]ed ingredients grouped by category, the user's own ingredients
 * first. With a search, one section of matches ranked like the ingredient picker ranks them.
 */
fun buildIngredientSections(
    ingredients: List<Ingredient>,
    query: String,
    filter: IngredientsFilter
): List<IngredientSection> {
    val visible = when (filter) {
        IngredientsFilter.ALL -> ingredients
        IngredientsFilter.STAPLES -> ingredients.filter { it.staple }
        IngredientsFilter.CUSTOM -> ingredients.filter { it.category == null }
    }
    if (query.isBlank()) {
        val byCategory = visible.groupBy { it.category }
        return (listOf(null) + IngredientCategory.entries).mapNotNull { category ->
            byCategory[category]?.sortedBy { it.name.lowercase() }?.let { IngredientSection(category, it) }
        }
    }
    val byName = visible.associateBy { normalizedIngredientName(it.name) }
    val matches = searchIngredients(query, visible.map { it.name }, limit = Int.MAX_VALUE).matches
        .mapNotNull { byName[normalizedIngredientName(it.name)] }
    return if (matches.isEmpty()) emptyList() else listOf(IngredientSection(null, matches, isSearchResult = true))
}
