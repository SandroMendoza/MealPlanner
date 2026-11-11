package com.meal.planner

import android.app.Application
import android.content.pm.ApplicationInfo
import com.meal.planner.common.MealReminderNotifications
import com.meal.planner.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatformTools

class MealPlannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            startKoin {
                androidLogger(if (isDebuggable) Level.INFO else Level.ERROR)
                androidContext(this@MealPlannerApplication)
                modules(appModule)
            }
        }
        MealReminderNotifications.createChannel(this)
    }
}
