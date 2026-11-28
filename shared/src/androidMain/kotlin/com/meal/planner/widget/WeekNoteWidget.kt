package com.meal.planner.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.meal.domain.strings.strings
import com.meal.domain.usecase.GetWeekNoteUseCase
import com.meal.planner.navigation.Routes
import com.meal.presentation.common.currentWeekSunday
import kotlinx.coroutines.flow.first

/** The notes written for the current week on the meal plan. */
class WeekNoteWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val settingsFlow = settingsFlow()
        val noteFlow = widgetKoin.get<GetWeekNoteUseCase>().execute(currentWeekSunday())
        val initialSettings = settingsFlow.first()
        val initialNote = noteFlow.first()

        provideContent {
            val settings by settingsFlow.collectAsState(initialSettings)
            val note by noteFlow.collectAsState(initialNote)
            val strings = settings.language.strings()
            val colors = WidgetColors(settings.themeMode)
            WidgetCard(colors = colors, onClick = openAppAction(LocalContext.current, Routes.CALENDAR)) {
                WidgetHeader(title = strings.widget.mealNotes, colors = colors)
                if (note.isBlank()) {
                    WidgetMessage(strings.widget.noWeekNote, colors)
                } else {
                    LazyColumn {
                        item {
                            Text(text = note, style = TextStyle(color = colors.onBackground, fontSize = WidgetDimens.bodySize))
                        }
                    }
                }
            }
        }
    }
}

class WeekNoteWidgetReceiver : MealPlannerWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeekNoteWidget()
}
