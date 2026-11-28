@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.meal.planner.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.meal.domain.model.Recipe
import com.meal.domain.strings.Strings
import com.meal.domain.strings.strings
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.planner.common.loadRecipePhoto
import com.meal.planner.navigation.Routes
import com.meal.planner.shared.R
import com.meal.presentation.common.today
import com.meal.presentation.widget.recipeOfTheDayIndex
import com.meal.presentation.widget.recipeOfTheDayPool
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * One recipe a day from the favorites (or the built-in recipes when there are none). The shuffle
 * button moves to another recipe for the rest of the day.
 */
class RecipeOfTheDayWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val date = today()
        val settingsFlow = settingsFlow()
        val poolFlow = widgetKoin.get<GetAllRecipesUseCase>().execute().map(::recipeOfTheDayPool)
        val initialSettings = settingsFlow.first()
        val initialPool = poolFlow.first()

        provideContent {
            val settings by settingsFlow.collectAsState(initialSettings)
            val pool by poolFlow.collectAsState(initialPool)
            val shuffles = if (currentState(SHUFFLE_DAY) == date.toEpochDays().toLong()) currentState(SHUFFLES) ?: 0 else 0
            val recipe = pool.takeIf { it.isNotEmpty() }?.let { it[recipeOfTheDayIndex(date, shuffles, it.size)] }
            val imageUrl = recipe?.imageUrl
            val photo by produceState<Bitmap?>(null, imageUrl) {
                value = imageUrl?.let { loadRecipePhoto(context, it, PHOTO_WIDTH_PX) }
            }
            RecipeOfTheDayContent(recipe, photo, settings.language.strings(), WidgetColors(settings.themeMode))
        }
    }

    internal companion object {
        const val PHOTO_WIDTH_PX = 480

        /** The day (epoch days) [SHUFFLES] counts for; shuffles from an earlier day don't apply. */
        val SHUFFLE_DAY = longPreferencesKey("shuffle_day")
        val SHUFFLES = intPreferencesKey("shuffles")
    }
}

class RecipeOfTheDayWidgetReceiver : MealPlannerWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RecipeOfTheDayWidget()
}

/** Moves a recipe-of-the-day widget on to another recipe for today. */
class ShuffleRecipeAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val day = today().toEpochDays().toLong()
        updateAppWidgetState(context, glanceId) { prefs ->
            val shuffles = if (prefs[RecipeOfTheDayWidget.SHUFFLE_DAY] == day) prefs[RecipeOfTheDayWidget.SHUFFLES] ?: 0 else 0
            prefs[RecipeOfTheDayWidget.SHUFFLE_DAY] = day
            prefs[RecipeOfTheDayWidget.SHUFFLES] = shuffles + 1
        }
        RecipeOfTheDayWidget().update(context, glanceId)
    }
}

@Composable
private fun RecipeOfTheDayContent(recipe: Recipe?, photo: Bitmap?, strings: Strings, colors: WidgetColors) {
    val context = LocalContext.current
    val onClick = recipe?.let { openAppAction(context, Routes.recipeDetail(it.id.toString())) }
        ?: openAppAction(context, Routes.RECIPES)
    WidgetCard(colors = colors, onClick = onClick) {
        WidgetHeader(title = strings.widget.recipeOfTheDay, colors = colors)
        if (recipe == null) {
            WidgetMessage(strings.recipesNoRecipesYet, colors)
        } else {
            if (photo != null && LocalSize.current.height >= 150.dp) {
                Image(
                    provider = ImageProvider(photo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight().cornerRadius(12.dp),
                )
                Spacer(GlanceModifier.height(WidgetDimens.rowSpacing))
            }
            Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = recipe.name,
                        maxLines = 1,
                        style = TextStyle(color = colors.onBackground, fontSize = WidgetDimens.bodySize, fontWeight = FontWeight.Medium),
                    )
                    Text(
                        text = strings.minutesLabel(recipe.preparationTimeMinutes),
                        style = TextStyle(color = colors.secondaryText, fontSize = WidgetDimens.captionSize),
                    )
                }
                WidgetIconButton(
                    icon = R.drawable.ic_widget_shuffle,
                    contentDescription = strings.widget.shuffleCd,
                    onClick = actionRunCallback<ShuffleRecipeAction>(),
                    colors = colors,
                )
            }
        }
    }
}
