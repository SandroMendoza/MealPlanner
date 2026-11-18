package com.meal.domain.strings

import com.meal.domain.model.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class PrivacyPolicyStringsTest {

    @Test
    fun everyLanguageHasTheSameNumberOfParagraphs() {
        val counts = Language.entries.associateWith { it.strings().privacyPolicyParagraphs.size }
        assertEquals(1, counts.values.toSet().size, "Privacy policy paragraph counts differ: $counts")
    }
}
