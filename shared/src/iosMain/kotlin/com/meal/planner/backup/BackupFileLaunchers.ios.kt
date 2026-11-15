package com.meal.planner.backup

import androidx.compose.runtime.Composable

actual fun isBackupSupported(): Boolean = false

@Composable
actual fun rememberBackupFileLaunchers(
    onExportDestination: (uri: String) -> Unit,
    onImportSource: (uri: String) -> Unit
): BackupFileLaunchers = BackupFileLaunchers(launchExport = {}, launchImport = {})
