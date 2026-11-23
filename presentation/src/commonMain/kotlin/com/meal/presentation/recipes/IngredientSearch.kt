package com.meal.presentation.recipes

import com.meal.domain.model.IngredientCatalog
import com.meal.domain.model.normalizedIngredientName

/** An ingredient found for a search. [alias] is set when the query matched another name for it. */
data class IngredientMatch(val name: String, val alias: String? = null)

/**
 * What the ingredient picker shows for [query]: the matching [matches], best first, and whether
 * the query is already the name or alias of one of them. When it isn't, the picker also offers
 * to add the query as an ingredient of its own.
 */
data class IngredientSearchResult(val matches: List<IngredientMatch>, val hasExactMatch: Boolean)

/**
 * Finds ingredients whose name or catalogue alias contains [query], ignoring case. Candidates are
 * every catalogue ingredient plus [names] (the ingredients the app knows about); a name that
 * differs from a catalogue name only in case shows with the catalogue's spelling.
 *
 * Names that start with the query come first, then names with a word that starts with it, then
 * any other match. Within a group a name hit beats an alias hit, then shorter names come first,
 * so "Garlic" ranks above "Garlic Powder" for "gar". At most [limit] matches are returned.
 */
fun searchIngredients(query: String, names: List<String>, limit: Int = 30): IngredientSearchResult {
    val needle = normalizedIngredientName(query)
    if (needle.isEmpty()) return IngredientSearchResult(emptyList(), hasExactMatch = false)

    val candidates = (IngredientCatalog.entries.map { it.name } + names)
        .filter { it.isNotBlank() }
        .distinctBy { normalizedIngredientName(it) }

    var hasExactMatch = false
    val ranked = candidates.mapNotNull { name ->
        val aliases = IngredientCatalog.find(name)
            ?.takeIf { normalizedIngredientName(it.name) == normalizedIngredientName(name) }
            ?.aliases
            .orEmpty()
        if (normalizedIngredientName(name) == needle || aliases.any { normalizedIngredientName(it) == needle }) {
            hasExactMatch = true
        }
        val nameRank = matchRank(name, needle)
        if (nameRank != null) {
            Ranked(IngredientMatch(name), nameRank * 2, name.length)
        } else {
            aliases.mapNotNull { alias -> matchRank(alias, needle)?.let { alias to it } }
                .minByOrNull { it.second }
                ?.let { (alias, rank) -> Ranked(IngredientMatch(name, alias), rank * 2 + 1, name.length) }
        }
    }

    val matches = ranked
        .sortedWith(compareBy<Ranked>({ it.rank }, { it.length }, { it.match.name.lowercase() }))
        .take(limit)
        .map { it.match }
    return IngredientSearchResult(matches, hasExactMatch)
}

private class Ranked(val match: IngredientMatch, val rank: Int, val length: Int)

/** 0 when [text] starts with [needle], 1 when one of its words does, 2 for any other match, null for none. */
private fun matchRank(text: String, needle: String): Int? {
    val haystack = normalizedIngredientName(text)
    return when {
        haystack.startsWith(needle) -> 0
        haystack.split(' ', '-', '(').any { it.startsWith(needle) } -> 1
        haystack.contains(needle) -> 2
        else -> null
    }
}
