package com.meal.domain.usecase

import com.meal.domain.service.BackupService

class ImportBackupUseCase(
    private val backupService: BackupService
) {
    suspend fun execute(sourceUri: String) = backupService.import(sourceUri)
}
