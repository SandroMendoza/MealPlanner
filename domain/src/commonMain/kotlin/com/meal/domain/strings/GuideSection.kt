package com.meal.domain.strings

/** One area of the feature guide: what it offers, and for an assistant, example requests. */
data class GuideSection(
    val title: String,
    val items: List<String>,
    val examples: List<String> = emptyList()
)
