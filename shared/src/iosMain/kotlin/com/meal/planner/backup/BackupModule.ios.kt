package com.meal.planner.backup

import com.meal.domain.service.BackupService
import org.koin.core.module.Module
import org.koin.dsl.module

private class UnsupportedBackupService : BackupService {
    override suspend fun export(destinationUri: String) =
        throw UnsupportedOperationException("Backup files are not available on iOS")

    override suspend fun import(sourceUri: String) =
        throw UnsupportedOperationException("Backup files are not available on iOS")
}

actual val backupModule: Module = module {
    single<BackupService> { UnsupportedBackupService() }
}
