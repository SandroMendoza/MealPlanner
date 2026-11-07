package com.meal.planner.common

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.meal.domain.model.enums.MealType
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.repository.SettingsRepository
import com.meal.domain.service.ReminderScheduler
import com.meal.domain.strings.strings
import com.meal.planner.shared.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class MealReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val mealType = intent.getStringExtra(EXTRA_MEAL_TYPE)?.let { MealType.valueOf(it) } ?: return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val koin = GlobalContext.get()
                val settingsRepository = koin.get<SettingsRepository>()
                val mealReminderRepository = koin.get<MealReminderRepository>()
                val scheduler = koin.get<ReminderScheduler>()

                val language = settingsRepository.getSettings().first().language
                showNotification(context, mealType, language.strings())

                val reminder = mealReminderRepository.getReminders().first().first { it.mealType == mealType }
                scheduler.schedule(reminder)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, mealType: MealType, strings: com.meal.domain.strings.Strings) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val notification = NotificationCompat.Builder(context, MealReminderNotifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_meal_reminder)
            .setContentTitle(strings.mealReminderNotificationTitle(mealType))
            .setContentText(strings.mealReminderNotificationBody)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(mealType.ordinal, notification)
    }

    companion object {
        const val EXTRA_MEAL_TYPE = "meal_type"
    }
}
