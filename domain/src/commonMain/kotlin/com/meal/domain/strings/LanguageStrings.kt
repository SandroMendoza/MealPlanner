package com.meal.domain.strings

import com.meal.domain.model.Language

fun Language.strings(): Strings = when (this) {
    Language.ENGLISH -> englishStrings
    Language.SPANISH -> spanishStrings
    Language.FRENCH -> frenchStrings
}
