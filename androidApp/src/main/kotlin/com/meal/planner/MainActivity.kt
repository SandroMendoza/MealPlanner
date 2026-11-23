package com.meal.planner

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
        hideNavigationBar()
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            startKoin {
                androidLogger(if (isDebuggable) Level.INFO else Level.ERROR)
                androidContext(this@MainActivity)
                modules(appModule)
            }
        }

        val deepLinkUri = intent?.data
            ?.takeIf { it.scheme == "mealplanner" && it.host == "open" }
        val startRoute = deepLinkUri?.let { Routes.routeForDeepLinkPath(it.pathSegments.firstOrNull()) }
        val quickAddItemName = deepLinkUri
            ?.takeIf { it.pathSegments.firstOrNull() == Routes.SHOPPING_LIST }
            ?.getQueryParameter("addItem")
            ?.takeIf { it.isNotBlank() }

        setContent {
            App(startRoute = startRoute, quickAddItemName = quickAddItemName)
        }
    }

    // The system shows the navigation bar again when the window regains focus, for example after
    // a dialog, the photo picker or a return from the background.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideNavigationBar()
    }

    /** Hides the system navigation bar; a swipe up from the bottom edge shows it for a moment. */
    private fun hideNavigationBar() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.navigationBars())
        }
    }
}
