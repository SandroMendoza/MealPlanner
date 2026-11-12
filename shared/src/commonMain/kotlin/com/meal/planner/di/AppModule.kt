package com.meal.planner.di

import com.meal.data.db.databaseDriverModule
import com.meal.data.di.dataModule
import com.meal.domain.di.domainModule
import com.meal.planner.common.mealReminderSchedulerModule
import com.meal.planner.common.ttsModule
import com.meal.presentation.di.presentationModule

val appModule = listOf(
    presentationModule,
    domainModule,
    dataModule,
    databaseDriverModule,
    mealReminderSchedulerModule,
    ttsModule
)
