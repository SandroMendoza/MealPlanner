package com.meal.planner.common

import com.meal.domain.model.MealReminder
import com.meal.domain.model.enums.MealType
import com.meal.domain.service.ReminderScheduler
import org.koin.core.module.Module
import org.koin.dsl.module

// Not implemented: this machine has no Xcode toolchain to build/verify a
// UNUserNotificationCenter-based implementation. All operations are no-ops
// until an iOS actual is implemented and tested.
private class NoopReminderScheduler : ReminderScheduler {
    override fun schedule(reminder: MealReminder) {}
    override fun cancel(mealType: MealType) {}
    override fun rescheduleAll(reminders: List<MealReminder>) {}
}

actual val mealReminderSchedulerModule: Module = module {
    single<ReminderScheduler> { NoopReminderScheduler() }
}
