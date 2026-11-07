package com.meal.domain.usecase

import com.meal.domain.model.MealReminder
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.service.ReminderScheduler

class UpdateMealReminderUseCase(
    private val repository: MealReminderRepository,
    private val scheduler: ReminderScheduler
) {
    suspend fun execute(reminder: MealReminder) {
        repository.updateReminder(reminder)
        scheduler.schedule(reminder)
    }
}
