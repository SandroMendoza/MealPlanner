package com.meal.planner.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/** A widget receiver that starts [WidgetRefresher] when the first widget of its kind is placed. */
abstract class MealPlannerWidgetReceiver : GlanceAppWidgetReceiver() {
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetRefresher.start(context)
    }
}
