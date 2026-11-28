package com.meal.planner.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import com.meal.domain.strings.strings
import com.meal.planner.shared.R
import kotlinx.coroutines.flow.first

/** A one-tap button that opens a small dialog to add an item to this week's shopping list. */
class QuickAddWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val settingsFlow = settingsFlow()
        val initialSettings = settingsFlow.first()

        provideContent {
            val settings by settingsFlow.collectAsState(initialSettings)
            val colors = WidgetColors(settings.themeMode)
            val openDialog = actionStartActivity(Intent(LocalContext.current, QuickAddActivity::class.java))
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(WidgetDimens.corner)
                    .background(colors.primaryContainer)
                    .clickable(openDialog),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_widget_add_shopping),
                    contentDescription = settings.language.strings().widget.quickAddTitle,
                    colorFilter = ColorFilter.tint(colors.onPrimaryContainer),
                    modifier = GlanceModifier.size(32.dp),
                )
            }
        }
    }
}

class QuickAddWidgetReceiver : MealPlannerWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickAddWidget()
}
