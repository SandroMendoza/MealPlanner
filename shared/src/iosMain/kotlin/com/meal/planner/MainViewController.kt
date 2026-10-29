package com.meal.planner

import androidx.compose.ui.window.ComposeUIViewController
import com.meal.planner.di.appModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools

fun MainViewController() = ComposeUIViewController (configure = {
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        startKoin { modules(appModule) }
    }
}) { App() }
