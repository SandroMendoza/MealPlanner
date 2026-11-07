package com.meal.domain.repository

import com.meal.domain.model.MealReminder
import kotlinx.coroutines.flow.Flow

interface MealReminderRepository {
    fun getReminders(): Flow<List<MealReminder>>
    suspend fun updateReminder(reminder: MealReminder)
}
