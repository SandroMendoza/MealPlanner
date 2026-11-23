package com.meal.domain.model

enum class Language {
    ENGLISH, SPANISH, FRENCH;

    companion object {
        /**
         * Languages the app offers. Every [Language] has a full set of strings, but only these can be
         * chosen, and a saved language outside this list is read as English.
         */
        val available: List<Language> = listOf(ENGLISH)
    }
}
