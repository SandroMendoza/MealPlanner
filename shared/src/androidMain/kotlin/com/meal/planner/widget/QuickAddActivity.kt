package com.meal.planner.widget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.lifecycle.lifecycleScope
import com.meal.domain.model.IngredientUnits
import com.meal.domain.model.enums.MeasurementUnit
import com.meal.domain.usecase.AddShoppingListItemUseCase
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.weekEndFor
import kotlinx.coroutines.launch

/**
 * A dialog over the home screen, opened from the quick add widget, that adds one item to this
 * week's shopping list and closes.
 */
class QuickAddActivity : ComponentActivity() {

    private var adding = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WidgetDialogTheme { strings ->
                var name by remember { mutableStateOf("") }
                val focusRequester = remember { FocusRequester() }
                val submit = { if (name.isNotBlank()) addItem(name.trim(), strings.widget.addedToShoppingList(name.trim())) }
                AlertDialog(
                    onDismissRequest = ::finish,
                    title = { Text(strings.widget.quickAddTitle) },
                    text = {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(strings.name) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(onDone = { submit() }),
                            modifier = Modifier.focusRequester(focusRequester),
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { submit() }, enabled = name.isNotBlank()) { Text(strings.add) }
                    },
                    dismissButton = {
                        TextButton(onClick = ::finish) { Text(strings.cancel) }
                    },
                )
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
            }
        }
    }

    // Uses the same amount as an item added through the shopping list's quick-add link: one of
    // the unit the ingredient is usually bought in.
    private fun addItem(name: String, confirmation: String) {
        if (adding) return
        adding = true
        lifecycleScope.launch {
            val unit = IngredientUnits.unitsFor(name)?.first() ?: MeasurementUnit.PIECE
            val weekStart = currentWeekSunday()
            widgetKoin.get<AddShoppingListItemUseCase>().execute(name, 1.0, unit, weekStart, weekEndFor(weekStart))
            Toast.makeText(this@QuickAddActivity, confirmation, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
