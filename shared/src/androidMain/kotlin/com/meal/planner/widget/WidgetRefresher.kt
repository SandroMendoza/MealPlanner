@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.meal.planner.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.updateAll
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetCollapsedShoppingSectionsUseCase
import com.meal.domain.usecase.GetShoppingGroupsUseCase
import com.meal.domain.usecase.GetWeekNoteUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.presentation.common.sundayOf
import com.meal.presentation.common.today
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

/**
 * Keeps the placed widgets in step with the app's data. A widget only redraws when it's told to,
 * so while the app process runs this watches what each widget shows and redraws the widgets whose
 * data changed. It only runs once a widget is placed.
 */
object WidgetRefresher {

    private val receivers = listOf(
        TodayPlanWidgetReceiver::class.java,
        WeekNoteWidgetReceiver::class.java,
        ShoppingListWidgetReceiver::class.java,
        NextMealWidgetReceiver::class.java,
        QuickAddWidgetReceiver::class.java,
        RecipeOfTheDayWidgetReceiver::class.java,
    )

    private var job: Job? = null

    /** Starts watching when at least one widget is on the home screen. */
    fun startIfWidgetsPlaced(context: Context) {
        val manager = AppWidgetManager.getInstance(context) ?: return
        val placed = receivers.any { manager.getAppWidgetIds(ComponentName(context, it)).isNotEmpty() }
        if (placed) start(context)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Synchronized
    fun start(context: Context) {
        if (job?.isActive == true) return
        val appContext = context.applicationContext
        job = CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            val koin = widgetKoin
            val dates = dateTicks()

            refreshOn(appContext, settingsFlow(), TodayPlanWidget(), WeekNoteWidget(), ShoppingListWidget(), NextMealWidget(), QuickAddWidget(), RecipeOfTheDayWidget())
            refreshOn(appContext, dates, TodayPlanWidget(), WeekNoteWidget(), ShoppingListWidget(), NextMealWidget(), RecipeOfTheDayWidget())
            refreshOn(
                appContext,
                dates.flatMapLatest { koin.get<GetWeeklyMealPlanUseCase>().execute(it, it.plus(6, DateTimeUnit.DAY)) },
                TodayPlanWidget(), NextMealWidget(),
            )
            refreshOn(
                appContext,
                dates.flatMapLatest { koin.get<GetWeekNoteUseCase>().execute(sundayOf(it)) },
                TodayPlanWidget(), WeekNoteWidget(),
            )
            refreshOn(
                appContext,
                dates.flatMapLatest { date ->
                    val weekStart = sundayOf(date)
                    combine(
                        koin.get<GenerateShoppingListUseCase>().execute(weekStart, weekEndFor(weekStart)),
                        koin.get<GetShoppingGroupsUseCase>().execute(),
                        koin.get<GetCollapsedShoppingSectionsUseCase>().execute(),
                    ) { items, groups, hidden -> Triple(items, groups, hidden) }
                },
                ShoppingListWidget(),
            )
            refreshOn(
                appContext,
                koin.get<GetAllRecipesUseCase>().execute().map { recipes ->
                    recipes.map { listOf(it.id, it.name, it.imageUrl, it.isFavorite, it.preparationTimeMinutes) }
                },
                RecipeOfTheDayWidget(),
            )
        }
    }

    /**
     * Redraws [widgets] each time [source] changes. The first value is what the widgets already
     * show, and writes that leave the data as it was (such as the shopping list re-syncing with
     * the meal plan) don't count as changes.
     */
    @OptIn(FlowPreview::class)
    private fun CoroutineScope.refreshOn(context: Context, source: Flow<Any?>, vararg widgets: GlanceAppWidget) {
        launch {
            source.distinctUntilChanged()
                .drop(1)
                .debounce(DEBOUNCE)
                .collect { widgets.forEach { it.updateAll(context) } }
        }
    }

    /** Today's date, emitted again when the day changes. */
    private fun dateTicks(): Flow<LocalDate> = flow {
        while (true) {
            emit(today())
            delay(DATE_CHECK_INTERVAL)
        }
    }.distinctUntilChanged()

    private val DEBOUNCE = 500.milliseconds
    private val DATE_CHECK_INTERVAL = 5.minutes
}
