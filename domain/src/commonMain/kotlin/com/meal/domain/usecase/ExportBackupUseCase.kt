package com.meal.domain.usecase

import com.meal.domain.service.BackupService

class ExportBackupUseCase(
    private val backupService: BackupService
) {
    suspend fun execute(destinationUri: String) = backupService.export(destinationUri)
}
