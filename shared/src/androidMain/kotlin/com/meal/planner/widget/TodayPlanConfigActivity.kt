package com.meal.planner.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Settings for one "Today's plan" widget: whether it shows the week's notes. Opens when the
 * widget is placed on older Android versions, and from the widget's settings button on Android 12
 * and later.
 */
class TodayPlanConfigActivity : ComponentActivity() {

    private var showWeekNote by mutableStateOf<Boolean?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        // The widget keeps its current settings when the dialog is dismissed, so placing it
        // never fails because the dialog was closed.
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, result)

        val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        lifecycleScope.launch {
            val state = getAppWidgetState(this@TodayPlanConfigActivity, PreferencesGlanceStateDefinition, glanceId)
            showWeekNote = state[TodayPlanWidget.SHOW_WEEK_NOTE] ?: false
        }

        setContent {
            val current = showWeekNote ?: return@setContent
            WidgetDialogTheme { strings ->
                AlertDialog(
                    onDismissRequest = ::finish,
                    title = { Text(strings.widget.settingsTitle) },
                    text = {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(strings.widget.showWeekNote, modifier = Modifier.weight(1f))
                            Switch(checked = current, onCheckedChange = { showWeekNote = it })
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            lifecycleScope.launch {
                                updateAppWidgetState(this@TodayPlanConfigActivity, glanceId) { prefs ->
                                    prefs[TodayPlanWidget.SHOW_WEEK_NOTE] = current
                                }
                                TodayPlanWidget().update(this@TodayPlanConfigActivity, glanceId)
                                finish()
                            }
                        }) { Text(strings.save) }
                    },
                    dismissButton = {
                        TextButton(onClick = ::finish) { Text(strings.cancel) }
                    },
                )
            }
        }
    }
}
