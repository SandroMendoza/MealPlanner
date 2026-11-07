package com.meal.presentation.notifications

import com.meal.domain.model.defaultReminderTime
import com.meal.domain.model.enums.MealType
import kotlinx.datetime.LocalTime

data class MealReminderUiState(
    val mealType: MealType,
    val enabled: Boolean,
    val time: LocalTime
)

data class NotificationSettingsUiState(
    val reminders: List<MealReminderUiState> = MealType.entries.map {
        MealReminderUiState(mealType = it, enabled = false, time = it.defaultReminderTime())
    },
    val isLoading: Boolean = true
)
