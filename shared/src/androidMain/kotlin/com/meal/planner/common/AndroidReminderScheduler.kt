package com.meal.planner.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.meal.domain.model.MealReminder
import com.meal.domain.model.enums.MealType
import com.meal.domain.service.ReminderScheduler
import java.util.Calendar

class AndroidReminderScheduler(private val context: Context) : ReminderScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(reminder: MealReminder) {
        if (!reminder.enabled) {
            cancel(reminder.mealType)
            return
        }
        val pendingIntent = pendingIntentFor(reminder.mealType)
        val triggerAt = nextTriggerMillis(reminder.time.hour, reminder.time.minute)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    override fun cancel(mealType: MealType) {
        alarmManager.cancel(pendingIntentFor(mealType))
    }

    override fun rescheduleAll(reminders: List<MealReminder>) {
        reminders.forEach { schedule(it) }
    }

    private fun pendingIntentFor(mealType: MealType): PendingIntent {
        val intent = Intent(context, MealReminderReceiver::class.java).apply {
            putExtra(MealReminderReceiver.EXTRA_MEAL_TYPE, mealType.name)
        }
        return PendingIntent.getBroadcast(
            context,
            mealType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (trigger.timeInMillis <= now.timeInMillis) {
            trigger.add(Calendar.DAY_OF_YEAR, 1)
        }
        return trigger.timeInMillis
    }
}
