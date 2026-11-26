package com.meal.planner.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import coil3.compose.AsyncImage
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.common.displayName
import com.meal.planner.common.filterIntegerInput
import com.meal.planner.common.rememberImagePicker
import com.meal.planner.components.ImageSourceDialog
import com.meal.planner.components.IngredientPickerSheet
import com.meal.planner.components.IngredientThumbnail
import com.meal.planner.components.recipeImageModel
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.planner.theme.activeFilterChipColors
import com.meal.presentation.common.formatAmount
import com.meal.presentation.recipes.IngredientFormRow
import com.meal.presentation.recipes.RecipeFormViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeFormScreen(
    recipeId: String?,
    onDone: () -> Unit,
    viewModel: RecipeFormViewModel = koinViewModel(
        parameters = { parametersOf(recipeId.orEmpty()) }
    )
) {
    val spacing = MealPlannerTokens.spacing
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onDone()
    }

    var showDiscardDialog by remember { mutableStateOf(false) }
    val close = { if (uiState.hasUnsavedChanges) showDiscardDialog = true else onDone() }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = uiState.hasUnsavedChanges,
        onBackCompleted = { showDiscardDialog = true }
    )

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(LocalStrings.current.recipeFormDiscardTitle, style = MaterialTheme.typography.titleLarge) },
            text = { Text(LocalStrings.current.recipeFormDiscardBody, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onDone()
                }) {
                    Text(LocalStrings.current.recipeFormDiscard, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text(LocalStrings.current.recipeFormKeepEditing) }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            // On a short screen the keyboard leaves room for only one of the bar and the field
            // being typed in, so the bar steps aside until the keyboard closes.
            if (!(LocalCompactHeight.current && WindowInsets.isImeVisible)) {
                TopAppBar(
                    title = {
                        Text(
                            if (uiState.isEditMode) LocalStrings.current.recipeFormEditTitle else LocalStrings.current.recipeFormNewTitle,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = close) { Text(LocalStrings.current.cancel) }
                    },
                    actions = {
                        TextButton(onClick = { viewModel.save() }, enabled = uiState.canSave && !uiState.isSaving) {
                            Text(LocalStrings.current.save)
                        }
                    },
                    windowInsets = WindowInsets(0)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            uiState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { RequiredFieldLabel(LocalStrings.current.name) },
                isError = uiState.isDuplicateName,
                supportingText = {
                    if (uiState.isDuplicateName) {
                        Text(LocalStrings.current.recipeFormNameExists, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text(LocalStrings.current.recipeFormDescription) },
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel(LocalStrings.current.category, required = true)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(FoodCategory.entries.toList()) { category ->
                    FilterChip(
                        selected = uiState.category == category,
                        onClick = { viewModel.updateCategory(category) },
                        label = { Text(category.displayName()) },
                        colors = activeFilterChipColors()
                    )
                }
            }

            SectionLabel(LocalStrings.current.cuisine)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(Cuisine.entries.toList(), key = { it.name }) { cuisine ->
                    FilterChip(
                        selected = uiState.cuisine == cuisine,
                        onClick = { viewModel.updateCuisine(cuisine) },
                        label = { Text(cuisine.displayName()) },
                        colors = activeFilterChipColors()
                    )
                }
            }

            SectionLabel(LocalStrings.current.mealType)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(MealType.entries.toList(), key = { it.name }) { mealType ->
                    FilterChip(
                        selected = mealType in uiState.mealTypes,
                        onClick = { viewModel.toggleMealType(mealType) },
                        label = { Text(mealType.displayName()) },
                        colors = activeFilterChipColors()
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                OutlinedTextField(
                    value = uiState.preparationTimeMinutes,
                    onValueChange = { viewModel.updatePreparationTime(filterIntegerInput(it)) },
                    label = { NumericFieldLabel(LocalStrings.current.recipeFormPrepMin) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.servings,
                    onValueChange = { viewModel.updateServings(filterIntegerInput(it)) },
                    label = { NumericFieldLabel(LocalStrings.current.recipeFormServings) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f)
                )
            }

            SectionLabel(LocalStrings.current.recipeFormPhoto)
            var showImageSourceDialog by remember { mutableStateOf(false) }
            val imagePicker = rememberImagePicker(onImagePicked = { uri ->
                uri?.let(viewModel::updateImageUrl)
            })
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = recipeImageModel(uiState.imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
                OutlinedButton(onClick = { showImageSourceDialog = true }) {
                    Text(if (uiState.imageUrl.isBlank()) LocalStrings.current.choosePhoto else LocalStrings.current.changePhoto)
                }
            }
            if (showImageSourceDialog) {
                ImageSourceDialog(
                    onTakePhoto = {
                        showImageSourceDialog = false
                        imagePicker.launchCamera()
                    },
                    onChooseFromGallery = {
                        showImageSourceDialog = false
                        imagePicker.launchGallery()
                    },
                    onDismiss = { showImageSourceDialog = false }
                )
            }

            SectionLabel(LocalStrings.current.ingredients, required = true)
            var pickerOpen by remember { mutableStateOf(false) }
            var editingIndex by remember { mutableStateOf<Int?>(null) }
            if (uiState.ingredients.isEmpty()) {
                Text(
                    text = LocalStrings.current.recipeFormNoIngredients,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                uiState.ingredients.forEachIndexed { index, row ->
                    IngredientListRow(
                        row = row,
                        onClick = {
                            editingIndex = index
                            pickerOpen = true
                        },
                        onRemove = { viewModel.removeIngredientRow(index) }
                    )
                }
            }
            OutlinedButton(onClick = {
                editingIndex = null
                pickerOpen = true
            }) {
                Text(LocalStrings.current.recipeFormAddIngredient)
            }
            if (pickerOpen) {
                val index = editingIndex
                IngredientPickerSheet(
                    knownNames = remember(uiState.availableIngredients) { uiState.availableIngredients.map { it.name } },
                    editing = index?.let { uiState.ingredients.getOrNull(it) },
                    onConfirm = { row ->
                        if (index != null) viewModel.updateIngredientRow(index, row) else viewModel.addIngredient(row)
                    },
                    onDismiss = { pickerOpen = false }
                )
            }

            SectionLabel(LocalStrings.current.instructions)
            InstructionStepsEditor(
                steps = uiState.instructions.map { it.description },
                onReplaceStep = viewModel::replaceInstructionRow,
                onRemoveStep = viewModel::removeInstructionRow,
                onMoveStep = viewModel::moveInstructionRow,
                onAddStep = viewModel::addInstructionRow,
                onReplaceAll = viewModel::replaceInstructions
            )

            Button(
                onClick = { viewModel.save() },
                enabled = uiState.canSave && !uiState.isSaving,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth().wrapContentWidth()
            ) {
                Text(if (uiState.isSaving) LocalStrings.current.recipeFormSaving else LocalStrings.current.recipeFormSaveRecipe)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, required: Boolean = false) {
    Text(
        text = if (required) requiredLabel(text) else AnnotatedString(text),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun RequiredFieldLabel(text: String) {
    Text(requiredLabel(text))
}

@Composable
private fun requiredLabel(text: String): AnnotatedString = buildAnnotatedString {
    append(text)
    withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) { append(" *") }
}

@Composable
private fun NumericFieldLabel(text: String) {
    Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun IngredientListRow(
    row: IngredientFormRow,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    val amount = if (row.quantity.isBlank()) strings.measurementUnitLabel(row.unit, 1.0) else formatAmount(row.quantity.toDoubleOrNull() ?: 0.0, row.unit, strings)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(start = spacing.xs, top = spacing.xs, bottom = spacing.xs)
    ) {
        IngredientThumbnail(name = row.name, size = MealPlannerTokens.iconSize.lg)
        Text(
            text = row.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Close, contentDescription = LocalStrings.current.remove)
        }
    }
}

