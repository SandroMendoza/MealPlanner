package com.meal.domain.service

/**
 * Saves all app data (database and photos) to a single backup file, and restores it.
 * Locations are platform URIs chosen by the user through the system file picker.
 */
interface BackupService {
    suspend fun export(destinationUri: String)

    /**
     * Replaces all app data with the backup at [sourceUri] and restarts the app.
     * Throws [InvalidBackupException], leaving current data untouched, when the file is
     * not a backup this version of the app can read.
     */
    suspend fun import(sourceUri: String)
}

class InvalidBackupException(message: String) : Exception(message)
