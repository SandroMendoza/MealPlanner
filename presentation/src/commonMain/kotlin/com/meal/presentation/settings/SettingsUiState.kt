package com.meal.presentation.settings

import com.meal.domain.model.Currency
import com.meal.domain.model.Language

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val language: Language = Language.ENGLISH,
    val currency: Currency = Currency.USD,
    val isLoading: Boolean = true
)
