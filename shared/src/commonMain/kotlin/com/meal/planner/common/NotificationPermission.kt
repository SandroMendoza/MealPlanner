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

/** Returns a callback that opens this app's notification settings in the OS settings app. */
@Composable
expect fun rememberOpenNotificationSettings(): () -> Unit
