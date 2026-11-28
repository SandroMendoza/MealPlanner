@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.meal.planner.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.strings.Strings
import com.meal.domain.strings.strings
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.planner.common.loadRecipePhoto
import com.meal.planner.navigation.Routes
import com.meal.presentation.common.today
import com.meal.presentation.widget.nextMeal
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** The next planned meal in the coming week; tapping it opens the recipe. */
class NextMealWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val date = today()
        val settingsFlow = settingsFlow()
        val mealsFlow = widgetKoin.get<GetWeeklyMealPlanUseCase>().execute(date, date.plus(6, DateTimeUnit.DAY))
        val initialSettings = settingsFlow.first()
        val initialMeals = mealsFlow.first()
        val initialPhoto = nextMeal(initialMeals, date, currentHour())?.recipe?.imageUrl
            ?.let { loadRecipePhoto(context, it, PHOTO_WIDTH_PX) }

        provideContent {
            val settings by settingsFlow.collectAsState(initialSettings)
            val meals by mealsFlow.collectAsState(initialMeals)
            val next = nextMeal(meals, date, currentHour())
            val imageUrl = next?.recipe?.imageUrl
            val photo by produceState(initialPhoto, imageUrl) {
                value = imageUrl?.let { loadRecipePhoto(context, it, PHOTO_WIDTH_PX) }
            }
            NextMealContent(next, date, photo, settings.language.strings(), WidgetColors(settings.themeMode))
        }
    }

    private companion object {
        const val PHOTO_WIDTH_PX = 360
    }
}

class NextMealWidgetReceiver : MealPlannerWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextMealWidget()
}

@OptIn(ExperimentalTime::class)
private fun currentHour(): Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour

@Composable
private fun NextMealContent(next: MealPlanEntry?, today: LocalDate, photo: Bitmap?, strings: Strings, colors: WidgetColors) {
    val context = LocalContext.current
    val onClick = if (next != null) {
        openAppAction(context, Routes.recipeDetail(next.recipe.id.toString()))
    } else {
        openAppAction(context, Routes.CALENDAR)
    }
    WidgetCard(colors = colors, onClick = onClick) {
        WidgetHeader(title = strings.widget.nextMeal, colors = colors)
        if (next == null) {
            WidgetMessage(strings.widget.noUpcomingMeals, colors)
        } else {
            val showPhoto = photo != null && LocalSize.current.height >= 150.dp
            if (photo != null && showPhoto) {
                Image(
                    provider = ImageProvider(photo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight().cornerRadius(12.dp),
                )
                Spacer(GlanceModifier.height(WidgetDimens.rowSpacing))
            }
            Column {
                Text(
                    text = "${dayLabel(next.date, today, strings)} · ${strings.mealTypeName(next.mealType)}",
                    maxLines = 1,
                    style = TextStyle(color = colors.secondaryText, fontSize = WidgetDimens.captionSize),
                )
                Text(
                    text = next.recipe.name,
                    maxLines = if (showPhoto) 1 else 2,
                    style = TextStyle(color = colors.onBackground, fontSize = WidgetDimens.bodySize, fontWeight = FontWeight.Medium),
                )
            }
        }
    }
}

private fun dayLabel(date: LocalDate, today: LocalDate, strings: Strings): String = when (date) {
    today -> strings.widget.today
    today.plus(1, DateTimeUnit.DAY) -> strings.widget.tomorrow
    else -> strings.weekdayShort(date.dayOfWeek)
}
