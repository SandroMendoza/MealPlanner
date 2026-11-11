package com.meal.presentation.settings

import com.meal.domain.model.CalendarViewMode
import com.meal.domain.model.Currency
import com.meal.domain.model.Language

data class SettingsUiState(
    val darkModeEnabled: Boolean = false,
    val language: Language = Language.ENGLISH,
    val currency: Currency = Currency.USD,
    val lockPastDates: Boolean = false,
    val defaultCalendarView: CalendarViewMode = CalendarViewMode.DAY,
    val isLoading: Boolean = true
)
