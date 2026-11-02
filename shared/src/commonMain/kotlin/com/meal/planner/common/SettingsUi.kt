package com.meal.planner.common

import com.meal.domain.model.Currency
import com.meal.domain.model.Language

fun Language.displayName(): String = when (this) {
    Language.ENGLISH -> "English"
    Language.SPANISH -> "Español"
    Language.FRENCH -> "Français"
}

fun Currency.displayName(): String = when (this) {
    Currency.USD -> "USD ($)"
    Currency.EUR -> "EUR (€)"
    Currency.GBP -> "GBP (£)"
    Currency.PHP -> "PHP (₱)"
}
