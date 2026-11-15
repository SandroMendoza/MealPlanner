package com.meal.planner.backup

import com.meal.domain.service.BackupService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val backupModule: Module = module {
    single { AndroidBackupService(androidContext(), get(), get(), get()) }
    single<BackupService> { get<AndroidBackupService>() }
}

/** Finishes a restore that restarted the app. Call once at app start, after Koin starts. */
fun completePendingBackupRestore() {
    GlobalContext.get().get<AndroidBackupService>().completePendingRestoreInBackground()
}
