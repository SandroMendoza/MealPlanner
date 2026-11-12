package com.meal.planner.common

import com.meal.domain.service.TtsSpeaker
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val ttsModule: Module = module {
    single<TtsSpeaker> { AndroidTtsSpeaker(androidContext()) }
}
