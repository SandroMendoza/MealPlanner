package com.meal.data.db

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databaseDriverModule: Module = module {
    single { DatabaseDriverFactory(androidContext()) }
}
