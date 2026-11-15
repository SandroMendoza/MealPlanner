package com.meal.domain.usecase

import com.meal.domain.fakes.FakeBackupService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BackupUseCasesTest {

    @Test
    fun exportPassesTheChosenLocation() = runTest {
        val service = FakeBackupService()
        ExportBackupUseCase(service).execute("content://downloads/backup.zip")
        assertEquals(listOf("content://downloads/backup.zip"), service.exported)
    }

    @Test
    fun importPassesTheChosenFile() = runTest {
        val service = FakeBackupService()
        ImportBackupUseCase(service).execute("content://downloads/backup.zip")
        assertEquals(listOf("content://downloads/backup.zip"), service.imported)
    }
}
