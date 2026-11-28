package com.meal.planner.common

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.mealReminderMessage
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.repository.SettingsRepository
import com.meal.domain.service.ReminderScheduler
import com.meal.domain.strings.strings
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.planner.navigation.Routes
import com.meal.planner.shared.R
import com.meal.presentation.common.today
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
                val date = today()
                val planned = koin.get<GetWeeklyMealPlanUseCase>().execute(date, date).first()
                    .filter { it.mealType == mealType }
                    .map { it.recipe }
                val message = mealReminderMessage(language.strings(), mealType, planned.map { it.name })
                val photo = planned.firstNotNullOfOrNull { recipe -> recipe.imageUrl?.let { loadRecipePhoto(context, it, PHOTO_SIZE_PX) } }
                showNotification(context, mealType, message.title, message.body, photo)

                val reminder = mealReminderRepository.getReminders().first().first { it.mealType == mealType }
                scheduler.schedule(reminder)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, mealType: MealType, title: String, body: String, photo: Bitmap?) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val notification = NotificationCompat.Builder(context, MealReminderNotifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_meal_reminder)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(body))
            .setLargeIcon(photo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openMealPlanIntent(context, mealType))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(mealType.ordinal, notification)
    }

    // Reminders fire for today's meals, and the meal plan opens on today.
    private fun openMealPlanIntent(context: Context, mealType: MealType): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mealplanner://open/${Routes.CALENDAR}"))
            .setPackage(context.packageName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            context,
            mealType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val EXTRA_MEAL_TYPE = "meal_type"
        private const val PHOTO_SIZE_PX = 256
    }
}
