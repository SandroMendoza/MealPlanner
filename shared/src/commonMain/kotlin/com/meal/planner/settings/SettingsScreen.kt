package com.meal.planner.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.CalendarViewMode
import com.meal.domain.model.RecipeViewMode
import com.meal.domain.model.ThemeMode
import com.meal.planner.backup.isBackupSupported
import com.meal.planner.backup.rememberBackupFileLaunchers
import com.meal.planner.components.CurrencyDropdownField
import com.meal.planner.components.LanguageDropdownField
import com.meal.planner.components.SettingsRow
import com.meal.planner.components.SettingsSwitchRow
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.common.today
import com.meal.presentation.settings.BackupStatus
import com.meal.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun CalendarViewMode.label(): String = when (this) {
    CalendarViewMode.DAY -> LocalStrings.current.day
    CalendarViewMode.WEEK -> LocalStrings.current.week
}

@Composable
private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> LocalStrings.current.themeSystem
    ThemeMode.LIGHT -> LocalStrings.current.themeLight
    ThemeMode.DARK -> LocalStrings.current.themeDark
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val backupFiles = rememberBackupFileLaunchers(
        onExportDestination = viewModel::exportBackup,
        onImportSource = viewModel::importBackup
    )
    var showRestoreConfirmation by remember { mutableStateOf(false) }

    if (showRestoreConfirmation) {
        RestoreConfirmationDialog(
            onConfirm = {
                showRestoreConfirmation = false
                backupFiles.launchImport()
            },
            onDismiss = { showRestoreConfirmation = false }
        )
    }
    BackupStatusDialog(status = uiState.backupStatus, onDismiss = viewModel::clearBackupStatus)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.settings, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = LocalStrings.current.back)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.screenVertical)
        ) {
            SectionLabel(LocalStrings.current.settingsPreferences)
            SettingsRow(label = LocalStrings.current.settingsNotifications, icon = Icons.Default.Notifications, onClick = onNotificationSettingsClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsTheme, icon = Icons.Default.DarkMode) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.width(216.dp)) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = uiState.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = ThemeMode.entries.size),
                            icon = {}
                        ) {
                            Text(mode.label(), style = MaterialTheme.typography.labelMedium, maxLines = 1)
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsSwitchRow(
                label = LocalStrings.current.settingsLockPastDates,
                icon = Icons.Default.Lock,
                checked = uiState.lockPastDates,
                onCheckedChange = viewModel::setLockPastDates
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsSwitchRow(
                label = LocalStrings.current.settingsReadAloud,
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                checked = uiState.readAloudEnabled,
                onCheckedChange = viewModel::setReadAloudEnabled
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsDefaultCalendarView, icon = Icons.Default.CalendarMonth) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.width(120.dp)) {
                    CalendarViewMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = uiState.defaultCalendarView == mode,
                            onClick = { viewModel.setDefaultCalendarView(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = CalendarViewMode.entries.size),
                            icon = {}
                        ) {
                            Text(mode.label(), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsDefaultRecipeView, icon = Icons.AutoMirrored.Filled.MenuBook) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.width(120.dp)) {
                    RecipeViewMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = uiState.defaultRecipeView == mode,
                            onClick = { viewModel.setDefaultRecipeView(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = RecipeViewMode.entries.size),
                            icon = {}
                        ) {
                            when (mode) {
                                RecipeViewMode.GRID -> Icon(
                                    Icons.Default.GridView,
                                    contentDescription = LocalStrings.current.recipesShowAsGridCd
                                )
                                RecipeViewMode.LIST -> Icon(
                                    Icons.AutoMirrored.Filled.ViewList,
                                    contentDescription = LocalStrings.current.recipesShowAsListCd
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(Modifier.height(spacing.sectionGap))
            SectionLabel(LocalStrings.current.settingsLocalization)
            LanguageDropdownField(
                value = uiState.language,
                onValueChange = viewModel::setLanguage,
                modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm)
            )
            CurrencyDropdownField(
                value = uiState.currency,
                onValueChange = viewModel::setCurrency,
                modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm)
            )

            if (isBackupSupported()) {
                Spacer(Modifier.height(spacing.sectionGap))
                SectionLabel(LocalStrings.current.settingsYourData)
                SettingsRow(
                    label = LocalStrings.current.settingsExportBackup,
                    icon = Icons.Default.Save,
                    onClick = { backupFiles.launchExport("meal-planner-backup-${today()}.zip") }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsRow(
                    label = LocalStrings.current.settingsImportBackup,
                    icon = Icons.Default.Restore,
                    onClick = { showRestoreConfirmation = true }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            Spacer(Modifier.height(spacing.sectionGap))
            SectionLabel(LocalStrings.current.settingsAbout)
            SettingsRow(label = LocalStrings.current.settingsAboutMealPlanner, icon = Icons.Default.Info, onClick = onAboutClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsPrivacyPolicy, icon = Icons.Default.PrivacyTip, onClick = onPrivacyPolicyClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val spacing = MealPlannerTokens.spacing
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = spacing.xs)
    )
}

@Composable
private fun RestoreConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val strings = LocalStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.backupImportConfirmTitle) },
        text = { Text(strings.backupImportConfirmBody) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(strings.backupImportConfirmAction) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } }
    )
}

@Composable
private fun BackupStatusDialog(status: BackupStatus, onDismiss: () -> Unit) {
    val strings = LocalStrings.current
    val message = when (status) {
        BackupStatus.IDLE -> return
        BackupStatus.WORKING -> null
        BackupStatus.EXPORTED -> strings.backupExported
        BackupStatus.FAILED -> strings.backupFailed
        BackupStatus.INVALID_FILE -> strings.backupInvalidFile
    }
    if (message == null) {
        // Not dismissable: the restore replaces the database and then restarts the app.
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(MealPlannerTokens.spacing.md))
                    Text(strings.backupWorking)
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = { Text(message) },
            confirmButton = { TextButton(onClick = onDismiss) { Text(strings.ok) } }
        )
    }
}
