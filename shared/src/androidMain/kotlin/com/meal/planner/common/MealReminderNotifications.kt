package com.meal.planner.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object MealReminderNotifications {
    const val CHANNEL_ID = "meal_reminders"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "Meal reminders", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }
}
