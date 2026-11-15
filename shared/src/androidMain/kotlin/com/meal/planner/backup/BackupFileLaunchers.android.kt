package com.meal.planner.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

actual fun isBackupSupported(): Boolean = true

@Composable
actual fun rememberBackupFileLaunchers(
    onExportDestination: (uri: String) -> Unit,
    onImportSource: (uri: String) -> Unit
): BackupFileLaunchers {
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        uri?.let { onExportDestination(it.toString()) }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { onImportSource(it.toString()) }
    }
    return BackupFileLaunchers(
        launchExport = { fileName -> exportLauncher.launch(fileName) },
        // Some file managers report zip files as a generic binary type.
        launchImport = { importLauncher.launch(arrayOf("application/zip", "application/octet-stream")) }
    )
}
