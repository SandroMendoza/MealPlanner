package com.meal.data.repository

import com.meal.data.datasource.MealReminderLocalDataSource
import com.meal.domain.model.MealReminder
import com.meal.domain.repository.MealReminderRepository
import kotlinx.coroutines.flow.Flow

class MealReminderRepositoryImpl(
    private val localDataSource: MealReminderLocalDataSource
) : MealReminderRepository {

    override fun getReminders(): Flow<List<MealReminder>> = localDataSource.reminders()

    override suspend fun updateReminder(reminder: MealReminder) {
        localDataSource.update(reminder)
    }
}
