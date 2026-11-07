package com.meal.planner.common

import androidx.compose.runtime.Composable

// Not implemented: this machine has no Xcode toolchain to build/verify a
// UNUserNotificationCenter-based implementation. Reminders cannot fire on iOS yet,
// so permission is reported as denied rather than claiming a capability that doesn't work.
@Composable
actual fun rememberNotificationPermissionRequester(onResult: (granted: Boolean) -> Unit): () -> Unit {
    return { onResult(false) }
}

@Composable
actual fun canPostNotifications(): Boolean = false

@Composable
actual fun canScheduleExactAlarms(): Boolean = true

@Composable
actual fun rememberOpenNotificationSettings(): () -> Unit = {}

@Composable
actual fun rememberOpenExactAlarmSettings(): () -> Unit = {}
