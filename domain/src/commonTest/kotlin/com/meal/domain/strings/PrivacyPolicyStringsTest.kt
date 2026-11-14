package com.meal.domain.strings

import com.meal.domain.model.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrivacyPolicyStringsTest {

    private val policyByLanguage = Language.entries.associateWith { language ->
        language.strings().privacyPolicyParagraphs.joinToString("\n")
    }

    @Test
    fun everyLanguageHasTheSameNumberOfParagraphs() {
        val counts = Language.entries.associateWith { it.strings().privacyPolicyParagraphs.size }
        assertEquals(1, counts.values.toSet().size, "Privacy policy paragraph counts differ: $counts")
    }

    @Test
    fun everyLanguageDisclosesAdMobAndLinksGooglesPolicy() {
        policyByLanguage.forEach { (language, policy) ->
            assertTrue("AdMob" in policy, "$language privacy policy does not mention AdMob")
            assertTrue(
                "https://policies.google.com/privacy" in policy,
                "$language privacy policy does not link Google's privacy policy"
            )
        }
    }

    @Test
    fun everyLanguageNamesTheAdPrivacyOptionsSettingAsLabelled() {
        policyByLanguage.forEach { (language, policy) ->
            val settingLabel = language.strings().settingsAdPrivacyOptions
            assertTrue(
                settingLabel in policy,
                "$language privacy policy does not name the \"$settingLabel\" setting"
            )
        }
    }
}
