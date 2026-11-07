package com.meal.planner

import android.app.Application
import com.meal.planner.common.MealReminderNotifications
import com.meal.planner.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools

class MealPlannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            startKoin {
                androidLogger()
                androidContext(this@MealPlannerApplication)
                modules(appModule)
            }
        }
        MealReminderNotifications.createChannel(this)
    }
}
