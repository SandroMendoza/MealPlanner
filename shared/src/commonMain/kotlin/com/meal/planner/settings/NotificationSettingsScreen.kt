package com.meal.planner.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.canPostNotifications
import com.meal.planner.common.canScheduleExactAlarms
import com.meal.planner.common.displayName
import com.meal.planner.common.formatTime
import com.meal.planner.common.icon
import com.meal.planner.common.rememberNotificationPermissionRequester
import com.meal.planner.common.rememberOpenExactAlarmSettings
import com.meal.planner.common.rememberOpenNotificationSettings
import com.meal.planner.components.SettingsRow
import com.meal.planner.components.TimePickerDialog
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.notifications.NotificationSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    var timePickerMealType by remember { mutableStateOf<MealType?>(null) }
    var pendingMealType by remember { mutableStateOf<MealType?>(null) }
    var permissionDenied by remember { mutableStateOf(false) }

    val hasPermission = canPostNotifications()
    val hasExactAlarms = canScheduleExactAlarms()
    val requestPermission = rememberNotificationPermissionRequester { granted ->
        val mealType = pendingMealType
        pendingMealType = null
        if (mealType != null) {
            if (granted) {
                viewModel.setEnabled(mealType, true)
                permissionDenied = false
            } else {
                permissionDenied = true
            }
        }
    }
    val openNotificationSettings = rememberOpenNotificationSettings()
    val openExactAlarmSettings = rememberOpenExactAlarmSettings()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(strings.notificationSettingsTitle, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.screenVertical)
        ) {
            Text(
                text = strings.notificationSettingsDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(spacing.sectionGap))

            uiState.reminders.forEach { reminder ->
                SettingsRow(label = reminder.mealType.displayName(), icon = reminder.mealType.icon()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (reminder.enabled) {
                            Text(
                                text = formatTime(reminder.time),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { timePickerMealType = reminder.mealType }
                            )
                            Spacer(Modifier.width(spacing.md))
                        }
                        Switch(
                            checked = reminder.enabled,
                            onCheckedChange = { enabled ->
                                when {
                                    !enabled -> viewModel.setEnabled(reminder.mealType, false)
                                    hasPermission -> {
                                        viewModel.setEnabled(reminder.mealType, true)
                                        permissionDenied = false
                                    }
                                    else -> {
                                        pendingMealType = reminder.mealType
                                        requestPermission()
                                    }
                                }
                            }
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            if (permissionDenied) {
                Spacer(Modifier.height(spacing.sectionGap))
                Text(
                    text = strings.settingsNotificationsPermissionDenied,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = openNotificationSettings) {
                    Text(strings.settingsNotificationsOpenSettings)
                }
            }

            if (!hasExactAlarms) {
                Spacer(Modifier.height(spacing.sectionGap))
                Text(
                    text = strings.settingsNotificationsExactAlarmHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = openExactAlarmSettings) {
                    Text(strings.settingsNotificationsEnableExactAlarms)
                }
            }
        }
    }

    timePickerMealType?.let { mealType ->
        val current = uiState.reminders.first { it.mealType == mealType }
        TimePickerDialog(
            initialTime = current.time,
            onTimeSelected = { time -> viewModel.setTime(mealType, time) },
            onDismiss = { timePickerMealType = null }
        )
    }
}
