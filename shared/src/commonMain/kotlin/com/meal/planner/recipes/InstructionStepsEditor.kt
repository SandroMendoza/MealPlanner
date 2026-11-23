package com.meal.planner.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.recipes.isTypedLineBreak
import com.meal.presentation.recipes.splitInstructionLines
import com.meal.presentation.recipes.stepsFromEdit

/** A step that should take focus, with the cursor at the end of its text or at the start. */
private data class StepFocus(val index: Int, val atEnd: Boolean)

/**
 * The recipe's steps, either as one field per step or, in "Write all" mode, as a single text box
 * with one step per line.
 *
 * In a step, Enter starts a new step below it with the text after the cursor, and Backspace in an
 * empty step removes it and returns to the end of the previous one. Pasting several lines turns
 * them into one step each.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionStepsEditor(
    steps: List<String>,
    onReplaceStep: (index: Int, steps: List<String>) -> Unit,
    onRemoveStep: (index: Int) -> Unit,
    onMoveStep: (from: Int, to: Int) -> Unit,
    onAddStep: () -> Unit,
    onReplaceAll: (steps: List<String>) -> Unit
) {
    val strings = LocalStrings.current
    val spacing = MealPlannerTokens.spacing
    var writeAll by rememberSaveable { mutableStateOf(false) }
    var allText by rememberSaveable { mutableStateOf("") }
    var focus by remember { mutableStateOf<StepFocus?>(null) }

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = !writeAll,
            onClick = { writeAll = false },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) { Text(strings.instructionsSteps) }
        SegmentedButton(
            selected = writeAll,
            onClick = {
                if (!writeAll) {
                    allText = steps.filter { it.isNotBlank() }.joinToString("\n")
                    writeAll = true
                }
            },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) { Text(strings.instructionsWriteAll) }
    }

    if (writeAll) {
        OutlinedTextField(
            value = allText,
            onValueChange = {
                allText = it
                onReplaceAll(splitInstructionLines(it))
            },
            placeholder = { Text(strings.instructionsWriteAllHint) },
            minLines = 5,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth()
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs), modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, text ->
            InstructionStepRow(
                number = index + 1,
                text = text,
                isFirst = index == 0,
                isLast = index == steps.lastIndex,
                focus = focus?.takeIf { it.index == index },
                onFocusHandled = { focus = null },
                onEdit = { before, after ->
                    val result = stepsFromEdit(before, after)
                    onReplaceStep(index, result)
                    if (result.size > 1) {
                        focus = if (isTypedLineBreak(before, after)) {
                            StepFocus(index + 1, atEnd = false)
                        } else {
                            StepFocus(index + result.size - 1, atEnd = true)
                        }
                    }
                },
                onBackspaceWhenEmpty = {
                    onRemoveStep(index)
                    focus = StepFocus(index - 1, atEnd = true)
                },
                onMoveUp = { onMoveStep(index, index - 1) },
                onMoveDown = { onMoveStep(index, index + 1) },
                onRemove = { onRemoveStep(index) }
            )
        }
    }
    TextButton(onClick = {
        focus = StepFocus(steps.size, atEnd = true)
        onAddStep()
    }) {
        Text(strings.recipeFormAddInstruction)
    }
}

@Composable
private fun InstructionStepRow(
    number: Int,
    text: String,
    isFirst: Boolean,
    isLast: Boolean,
    focus: StepFocus?,
    onFocusHandled: () -> Unit,
    onEdit: (before: String, after: String) -> Unit,
    onBackspaceWhenEmpty: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    val strings = LocalStrings.current
    val spacing = MealPlannerTokens.spacing
    val focusRequester = remember { FocusRequester() }
    var field by remember { mutableStateOf(TextFieldValue(text, TextRange(text.length))) }
    // Rows are laid out by position, so a row can be handed another step's text after a split,
    // move or removal; the field then shows that text with the cursor at its end. Typing updates
    // both in the same callback, so they only differ after such a change.
    val shown = if (field.text == text) field else TextFieldValue(text, TextRange(text.length))

    LaunchedEffect(focus) {
        if (focus != null) {
            field = TextFieldValue(text, TextRange(if (focus.atEnd) text.length else 0))
            focusRequester.requestFocus()
            onFocusHandled()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("$number.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = shown,
            onValueChange = { edited ->
                if ('\n' in edited.text) {
                    onEdit(shown.text, edited.text)
                } else {
                    field = edited
                    if (edited.text != shown.text) onEdit(shown.text, edited.text)
                }
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    val removesEmptyStep = event.type == KeyEventType.KeyDown &&
                        event.key == Key.Backspace && shown.text.isEmpty() && !isFirst
                    if (removesEmptyStep) onBackspaceWhenEmpty()
                    removesEmptyStep
                }
        )
        Column {
            IconButton(onClick = onMoveUp, enabled = !isFirst, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = strings.moveUp)
            }
            IconButton(onClick = onMoveDown, enabled = !isLast, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = strings.moveDown)
            }
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Close, contentDescription = strings.remove)
        }
    }
}
