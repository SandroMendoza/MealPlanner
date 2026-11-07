package com.meal.domain.usecase

import com.meal.domain.repository.MealReminderRepository

class GetMealRemindersUseCase(
    private val repository: MealReminderRepository
) {
    fun execute() = repository.getReminders()
}
