@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.meal.planner.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.strings.Strings
import com.meal.domain.strings.strings
import com.meal.domain.usecase.GetWeekNoteUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.planner.navigation.Routes
import com.meal.presentation.common.sundayOf
import com.meal.presentation.common.today
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** Today's meals in meal order, with the week's notes underneath when the widget is set to show them. */
class TodayPlanWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val date = today()
        val settingsFlow = settingsFlow()
        val mealsFlow = widgetKoin.get<GetWeeklyMealPlanUseCase>().execute(date, date)
            .map { entries -> entries.sortedBy { it.mealType.ordinal } }
        val noteFlow = widgetKoin.get<GetWeekNoteUseCase>().execute(sundayOf(date))
        val initialSettings = settingsFlow.first()
        val initialMeals = mealsFlow.first()
        val initialNote = noteFlow.first()

        provideContent {
            val settings by settingsFlow.collectAsState(initialSettings)
            val meals by mealsFlow.collectAsState(initialMeals)
            val note by noteFlow.collectAsState(initialNote)
            val showNote = currentState(SHOW_WEEK_NOTE) ?: false
            TodayPlanContent(
                meals = meals,
                note = note.takeIf { showNote },
                strings = settings.language.strings(),
                colors = WidgetColors(settings.themeMode),
            )
        }
    }

    companion object {
        /** Per-widget setting: whether the week's notes show under today's meals. */
        val SHOW_WEEK_NOTE = booleanPreferencesKey("show_week_note")
    }
}

class TodayPlanWidgetReceiver : MealPlannerWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayPlanWidget()
}

@Composable
private fun TodayPlanContent(meals: List<MealPlanEntry>, note: String?, strings: Strings, colors: WidgetColors) {
    val context = LocalContext.current
    WidgetCard(colors = colors, onClick = openAppAction(context, Routes.CALENDAR)) {
        WidgetHeader(title = strings.homeTodaysPlan, colors = colors)
        LazyColumn {
            if (meals.isEmpty()) {
                item { WidgetMessage(strings.homeNoMealsPlannedToday, colors) }
            }
            items(meals) { entry -> MealRow(entry, strings, colors) }
            if (note != null) {
                item { WeekNoteSection(note, strings, colors) }
            }
        }
    }
}

@Composable
private fun MealRow(entry: MealPlanEntry, strings: Strings, colors: WidgetColors) {
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(openAppAction(context, Routes.recipeDetail(entry.recipe.id.toString()))),
    ) {
        Text(
            text = strings.mealTypeName(entry.mealType),
            style = TextStyle(color = colors.secondaryText, fontSize = WidgetDimens.captionSize),
        )
        Text(
            text = entry.recipe.name,
            maxLines = 1,
            style = TextStyle(color = colors.onBackground, fontSize = WidgetDimens.bodySize, fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun WeekNoteSection(note: String, strings: Strings, colors: WidgetColors) {
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Spacer(GlanceModifier.height(WidgetDimens.rowSpacing))
        Text(
            text = strings.calendarWeekNotes,
            style = TextStyle(color = colors.primary, fontSize = WidgetDimens.captionSize, fontWeight = FontWeight.Bold),
        )
        Text(
            text = note.ifBlank { strings.widget.noWeekNote },
            style = TextStyle(
                color = if (note.isBlank()) colors.secondaryText else colors.onBackground,
                fontSize = WidgetDimens.bodySize,
            ),
        )
    }
}
