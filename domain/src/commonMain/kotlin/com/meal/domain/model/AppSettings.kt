package com.meal.domain.model

data class AppSettings(
    val darkModeEnabled: Boolean = false,
    val language: Language = Language.ENGLISH,
    val currency: Currency = Currency.USD,
    val lockPastDates: Boolean = false
)
