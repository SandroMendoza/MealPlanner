package com.meal.presentation.settings

import com.meal.domain.model.CalendarViewMode
import com.meal.domain.model.Currency
import com.meal.domain.model.Language
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: Language = Language.ENGLISH,
    val currency: Currency = Currency.USD,
    val lockPastDates: Boolean = false,
    val defaultCalendarView: CalendarViewMode = CalendarViewMode.DAY,
    val readAloudEnabled: Boolean = false,
    val defaultRecipeView: RecipeViewMode = RecipeViewMode.GRID,
    val backupStatus: BackupStatus = BackupStatus.IDLE,
    val isLoading: Boolean = true
)

enum class BackupStatus { IDLE, WORKING, EXPORTED, FAILED, INVALID_FILE }
