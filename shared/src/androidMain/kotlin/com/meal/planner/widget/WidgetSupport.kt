package com.meal.planner.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProvider as DayNightColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.meal.domain.model.AppSettings
import com.meal.domain.model.ThemeMode
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.planner.theme.MealPlannerColorScheme
import com.meal.planner.theme.MealPlannerDarkColorScheme
import kotlinx.coroutines.flow.Flow
import org.koin.core.context.GlobalContext

/**
 * Widgets are created by the launcher's host process through their receivers, outside Koin, so
 * they resolve use cases from the global Koin context that [android.app.Application] starts.
 */
internal val widgetKoin get() = GlobalContext.get()

internal fun settingsFlow(): Flow<AppSettings> = widgetKoin.get<GetSettingsUseCase>().execute()

/** An intent for a `mealplanner://open/<path>` link, opened in the app's existing task. */
internal fun openAppIntent(context: Context, path: String): Intent =
    Intent(Intent.ACTION_VIEW, Uri.parse("mealplanner://open/$path"))
        .setPackage(context.packageName)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

internal fun openAppAction(context: Context, path: String): Action = actionStartActivity(openAppIntent(context, path))

/** The app's colours for a widget, following the theme chosen in Settings. */
internal class WidgetColors(private val themeMode: ThemeMode) {
    private fun pick(light: Color, dark: Color): ColorProvider = when (themeMode) {
        ThemeMode.LIGHT -> ColorProvider(light)
        ThemeMode.DARK -> ColorProvider(dark)
        ThemeMode.SYSTEM -> DayNightColorProvider(day = light, night = dark)
    }

    private val light = MealPlannerColorScheme
    private val dark = MealPlannerDarkColorScheme

    val background = pick(light.background, dark.background)
    val onBackground = pick(light.onBackground, dark.onBackground)
    val surface = pick(light.surface, dark.surface)
    val secondaryText = pick(light.onSurfaceVariant, dark.onSurfaceVariant)
    val primary = pick(light.primary, dark.primary)
    val onPrimary = pick(light.onPrimary, dark.onPrimary)
    val primaryContainer = pick(light.primaryContainer, dark.primaryContainer)
    val onPrimaryContainer = pick(light.onPrimaryContainer, dark.onPrimaryContainer)
}

internal object WidgetDimens {
    val corner = 20.dp
    val padding = 12.dp
    val rowSpacing = 6.dp
    val icon = 24.dp
    val iconButton = icon + 8.dp
    val titleSize = 15.sp
    val bodySize = 14.sp
    val captionSize = 12.sp
}

/** The rounded card every widget draws on; tapping an empty area runs [onClick]. */
@Composable
internal fun WidgetCard(
    colors: WidgetColors,
    onClick: Action?,
    modifier: GlanceModifier = GlanceModifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    var cardModifier = modifier
        .fillMaxSize()
        .cornerRadius(WidgetDimens.corner)
        .background(colors.background)
        .padding(WidgetDimens.padding)
    if (onClick != null) cardModifier = cardModifier.clickable(onClick)
    Column(modifier = cardModifier, content = content)
}

/** A widget's title row, with an optional icon button at the end. */
@Composable
internal fun WidgetHeader(
    title: String,
    colors: WidgetColors,
    trailingIcon: Int? = null,
    trailingContentDescription: String? = null,
    onTrailingClick: Action? = null,
) {
    Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            maxLines = 1,
            style = TextStyle(color = colors.primary, fontSize = WidgetDimens.titleSize, fontWeight = FontWeight.Bold),
            modifier = GlanceModifier.defaultWeight(),
        )
        if (trailingIcon != null && onTrailingClick != null) {
            WidgetIconButton(trailingIcon, trailingContentDescription, onTrailingClick, colors)
        }
    }
    Spacer(GlanceModifier.height(WidgetDimens.rowSpacing))
}

/** A round, tappable icon in the app's primary colour. */
@Composable
internal fun WidgetIconButton(icon: Int, contentDescription: String?, onClick: Action, colors: WidgetColors) {
    Box(
        modifier = GlanceModifier.size(WidgetDimens.iconButton).cornerRadius(16.dp).clickable(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            provider = ImageProvider(icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(colors.primary),
            modifier = GlanceModifier.size(WidgetDimens.icon),
        )
    }
}

@Composable
internal fun WidgetMessage(text: String, colors: WidgetColors) {
    Text(text = text, style = TextStyle(color = colors.secondaryText, fontSize = WidgetDimens.bodySize))
}
