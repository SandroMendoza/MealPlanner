package com.meal.planner.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.service.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in RESCHEDULE_ACTIONS) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val koin = GlobalContext.get()
                val reminders = koin.get<MealReminderRepository>().getReminders().first()
                koin.get<ReminderScheduler>().rescheduleAll(reminders)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        // Reminder alarms are absolute times computed from the local clock, so they need
        // recomputing after a reboot or when the device's time zone or clock changes.
        val RESCHEDULE_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
        )
    }
}
