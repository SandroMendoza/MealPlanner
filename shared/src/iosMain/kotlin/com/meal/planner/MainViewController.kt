package com.meal.planner

import androidx.compose.ui.window.ComposeUIViewController
import com.meal.planner.di.appModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController (configure = {
    if (GlobalContext.getOrNull() == null) {
        startKoin { modules(appModule) }
    }
}) { App() }
