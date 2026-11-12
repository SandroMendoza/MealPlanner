package com.meal.planner

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meal.planner.di.appModule
import com.meal.planner.navigation.Routes
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatformTools

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            startKoin {
                androidLogger(if (isDebuggable) Level.INFO else Level.ERROR)
                androidContext(this@MainActivity)
                modules(appModule)
            }
        }

        val startRoute = intent?.data
            ?.takeIf { it.scheme == "mealplanner" && it.host == "open" }
            ?.let { Routes.routeForDeepLinkPath(it.pathSegments.firstOrNull()) }

        setContent {
            App(startRoute = startRoute)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}