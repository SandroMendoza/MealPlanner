package com.meal.domain.service

import com.meal.domain.model.MealReminder
import com.meal.domain.model.enums.MealType

interface ReminderScheduler {
    /** Schedules a daily reminder, or cancels it when [MealReminder.enabled] is false. */
    fun schedule(reminder: MealReminder)
    fun cancel(mealType: MealType)
    fun rescheduleAll(reminders: List<MealReminder>)
}
