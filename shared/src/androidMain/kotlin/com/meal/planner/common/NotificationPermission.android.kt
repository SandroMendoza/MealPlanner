package com.meal.planner.common

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat

@Composable
actual fun rememberNotificationPermissionRequester(onResult: (granted: Boolean) -> Unit): () -> Unit {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return { onResult(true) }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onResult
    )
    return { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
}

@Composable
actual fun canPostNotifications(): Boolean {
    val context = LocalContext.current
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

@Composable
actual fun rememberOpenNotificationSettings(): () -> Unit {
    val context = LocalContext.current
    return {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(intent)
    }
}
