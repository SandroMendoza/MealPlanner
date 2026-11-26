package com.meal.domain.strings

import com.meal.domain.model.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureGuideStringsTest {

    private fun shape(language: Language) =
        language.strings().featureGuideSections.map { it.items.size to it.examples.size }

    @Test
    fun everyLanguageHasTheSameSectionsItemsAndExamples() {
        val shapes = Language.entries.associateWith { shape(it) }
        assertEquals(1, shapes.values.toSet().size, "Feature guide shapes differ: $shapes")
    }

    @Test
    fun noSectionIsEmpty() {
        Language.entries.forEach { language ->
            language.strings().featureGuideSections.forEach { section ->
                assertTrue(section.title.isNotBlank() && section.items.all { it.isNotBlank() }, "$language: ${section.title}")
                assertTrue(section.items.isNotEmpty(), "$language: ${section.title} has no items")
            }
        }
    }

    @Test
    fun walkthroughPageCounterIncludesItsNumbers() {
        Language.entries.forEach { language ->
            val text = language.strings().walkthroughPageOf(2, 5)
            assertTrue("2" in text && "5" in text, "$language: $text")
        }
    }
}
