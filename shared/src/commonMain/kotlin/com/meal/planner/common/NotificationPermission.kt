package com.meal.planner.common

import androidx.compose.runtime.Composable

/**
 * Returns a callback that requests the platform's notification permission.
 * [onResult] receives whether the permission ended up granted.
 */
@Composable
expect fun rememberNotificationPermissionRequester(onResult: (granted: Boolean) -> Unit): () -> Unit

/** Whether this app is currently allowed to post notifications. */
@Composable
expect fun canPostNotifications(): Boolean

/** Whether the OS currently allows this app to schedule exact-time alarms. */
@Composable
expect fun canScheduleExactAlarms(): Boolean

/** Returns a callback that opens this app's notification settings in the OS settings app. */
@Composable
expect fun rememberOpenNotificationSettings(): () -> Unit

/** Returns a callback that opens the OS's "allow exact alarms" setting for this app, if the platform has one. */
@Composable
expect fun rememberOpenExactAlarmSettings(): () -> Unit
