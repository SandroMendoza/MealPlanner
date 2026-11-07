package com.meal.planner.common

import com.meal.domain.service.ReminderScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val mealReminderSchedulerModule: Module = module {
    single<ReminderScheduler> { AndroidReminderScheduler(androidContext()) }
}
