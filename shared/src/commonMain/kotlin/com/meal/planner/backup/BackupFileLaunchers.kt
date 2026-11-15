package com.meal.planner.backup

import androidx.compose.runtime.Composable

class BackupFileLaunchers(
    val launchExport: (suggestedFileName: String) -> Unit,
    val launchImport: () -> Unit
)

/** True where the app can save and restore backup files. */
expect fun isBackupSupported(): Boolean

/** Opens the system file picker to choose where to save a backup, or which backup to restore. */
@Composable
expect fun rememberBackupFileLaunchers(
    onExportDestination: (uri: String) -> Unit,
    onImportSource: (uri: String) -> Unit
): BackupFileLaunchers
