package com.meal.planner.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.meal.domain.model.ShoppingListItem
import com.meal.domain.strings.Strings
import com.meal.domain.strings.strings
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.GetCollapsedShoppingSectionsUseCase
import com.meal.domain.usecase.GetShoppingGroupsUseCase
import com.meal.domain.usecase.ToggleShoppingListItemUseCase
import com.meal.planner.navigation.Routes
import com.meal.planner.shared.R
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.formatAmount
import com.meal.presentation.common.weekEndFor
import com.meal.presentation.shopping.ShoppingListRangeMode
import com.meal.presentation.shopping.ShoppingListSection
import com.meal.presentation.shopping.ShoppingListUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * This week's shopping list in the same groups and order as the app, without pantry staples and
 * hidden groups. Tapping an item checks it off.
 */
class ShoppingListWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val settingsFlow = settingsFlow()
        val sectionsFlow = shoppingSectionsFlow()
        val initialSettings = settingsFlow.first()
        val initialSections = sectionsFlow.first()

        provideContent {
            val settings by settingsFlow.collectAsState(initialSettings)
            val sections by sectionsFlow.collectAsState(initialSections)
            ShoppingListContent(sections, settings.language.strings(), WidgetColors(settings.themeMode))
        }
    }
}

class ShoppingListWidgetReceiver : MealPlannerWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShoppingListWidget()
}

/** Checks or unchecks one item of this week's shopping list from the widget. */
class ToggleShoppingItemAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val itemKey = parameters[ITEM_KEY] ?: return
        val checked = parameters[CHECKED] ?: return
        val weekStart = currentWeekSunday()
        widgetKoin.get<ToggleShoppingListItemUseCase>().execute(itemKey, weekStart, weekEndFor(weekStart), checked)
        ShoppingListWidget().update(context, glanceId)
    }

    companion object {
        val ITEM_KEY = ActionParameters.Key<String>("item_key")
        val CHECKED = ActionParameters.Key<Boolean>("checked")
    }
}

private fun shoppingSectionsFlow(): Flow<List<ShoppingListSection>> {
    val weekStart = currentWeekSunday()
    val weekEnd = weekEndFor(weekStart)
    return combine(
        widgetKoin.get<GenerateShoppingListUseCase>().execute(weekStart, weekEnd),
        widgetKoin.get<GetShoppingGroupsUseCase>().execute(),
        widgetKoin.get<GetCollapsedShoppingSectionsUseCase>().execute(),
    ) { items, groups, hiddenKeys ->
        ShoppingListUiState(ShoppingListRangeMode.WEEK, weekStart, weekEnd, items = items, groups = groups)
            .sections
            .filterNot { it.key in hiddenKeys }
    }
}

@Composable
private fun ShoppingListContent(sections: List<ShoppingListSection>, strings: Strings, colors: WidgetColors) {
    val context = LocalContext.current
    WidgetCard(colors = colors, onClick = openAppAction(context, Routes.SHOPPING_LIST)) {
        WidgetHeader(
            title = strings.homeShoppingList,
            colors = colors,
            trailingIcon = R.drawable.ic_widget_add,
            trailingContentDescription = strings.widget.quickAddTitle,
            onTrailingClick = actionStartActivity(Intent(context, QuickAddActivity::class.java)),
        )
        if (sections.isEmpty()) {
            WidgetMessage(strings.widget.shoppingListEmpty, colors)
        } else {
            val showHeadings = sections.size > 1 || sections.first().group != null
            LazyColumn {
                sections.forEach { section ->
                    if (showHeadings) {
                        item {
                            Text(
                                text = section.group?.name ?: strings.shoppingUngrouped,
                                style = TextStyle(color = colors.primary, fontSize = WidgetDimens.captionSize, fontWeight = FontWeight.Bold),
                                modifier = GlanceModifier.padding(top = 6.dp, bottom = 2.dp),
                            )
                        }
                    }
                    items(section.items) { item -> ShoppingItemRow(item, strings, colors) }
                }
            }
        }
    }
}

@Composable
private fun ShoppingItemRow(item: ShoppingListItem, strings: Strings, colors: WidgetColors) {
    val toggle = actionRunCallback<ToggleShoppingItemAction>(
        actionParametersOf(
            ToggleShoppingItemAction.ITEM_KEY to item.itemKey,
            ToggleShoppingItemAction.CHECKED to !item.checked,
        )
    )
    val textColor = if (item.checked) colors.secondaryText else colors.onBackground
    val decoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
    Row(
        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp).clickable(toggle),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(if (item.checked) R.drawable.ic_widget_checked else R.drawable.ic_widget_unchecked),
            contentDescription = null,
            colorFilter = ColorFilter.tint(if (item.checked) colors.primary else colors.secondaryText),
            modifier = GlanceModifier.size(20.dp),
        )
        Spacer(GlanceModifier.width(8.dp))
        Text(
            text = item.name,
            maxLines = 1,
            style = TextStyle(color = textColor, fontSize = WidgetDimens.bodySize, textDecoration = decoration),
            modifier = GlanceModifier.defaultWeight(),
        )
        val amount = formatAmount(item.totalQuantity, item.unit, strings)
        if (amount.isNotEmpty()) {
            Text(
                text = amount,
                maxLines = 1,
                style = TextStyle(color = colors.secondaryText, fontSize = WidgetDimens.captionSize, textDecoration = decoration),
                modifier = GlanceModifier.padding(start = 8.dp),
            )
        }
    }
}
