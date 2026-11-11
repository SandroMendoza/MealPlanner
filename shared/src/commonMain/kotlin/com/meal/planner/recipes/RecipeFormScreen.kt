package com.meal.planner.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.displayName
import com.meal.planner.common.rememberImagePicker
import com.meal.planner.components.IngredientNameField
import com.meal.planner.components.UnitDropdownField
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.planner.theme.activeFilterChipColors
import com.meal.presentation.recipes.IngredientFormRow
import com.meal.presentation.recipes.InstructionFormRow
import com.meal.presentation.recipes.RecipeFormViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) LocalStrings.current.recipeFormEditTitle else LocalStrings.current.recipeFormNewTitle,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onDone) { Text(LocalStrings.current.cancel) }
                },
                actions = {
                    TextButton(onClick = { viewModel.save() }, enabled = uiState.canSave && !uiState.isSaving) {
                        Text(LocalStrings.current.save)
                    }
                },
                windowInsets = WindowInsets(0)
            )
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
                label = { Text(LocalStrings.current.name) },
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

            SectionLabel(LocalStrings.current.category)
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

            SectionLabel(LocalStrings.current.recipeFormMealTypes)
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
                    onValueChange = viewModel::updatePreparationTime,
                    label = { Text(LocalStrings.current.recipeFormPrepMin) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.servings,
                    onValueChange = viewModel::updateServings,
                    label = { Text(LocalStrings.current.recipeFormServings) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.calories,
                    onValueChange = viewModel::updateCalories,
                    label = { Text(LocalStrings.current.recipeFormCalories) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.weight(1f)
                )
            }

            SectionLabel(LocalStrings.current.recipeFormPhoto)
            val launchImagePicker = rememberImagePicker(onImagePicked = { uri ->
                uri?.let(viewModel::updateImageUrl)
            })
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = uiState.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
                OutlinedButton(onClick = launchImagePicker) {
                    Text(if (uiState.imageUrl.isBlank()) LocalStrings.current.choosePhoto else LocalStrings.current.changePhoto)
                }
            }

            SectionLabel(LocalStrings.current.ingredients)
            uiState.ingredients.forEachIndexed { index, row ->
                IngredientRowEditor(
                    row = row,
                    suggestions = uiState.availableIngredients.map { it.name },
                    onChange = { viewModel.updateIngredientRow(index, it) },
                    onRemove = { viewModel.removeIngredientRow(index) }
                )
            }
            TextButton(onClick = { viewModel.addIngredientRow() }) {
                Text(LocalStrings.current.recipeFormAddIngredient)
            }

            SectionLabel(LocalStrings.current.instructions)
            uiState.instructions.forEachIndexed { index, row ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${index + 1}.", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = row.description,
                        onValueChange = { viewModel.updateInstructionRow(index, InstructionFormRow(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.removeInstructionRow(index) }) {
                        Text(LocalStrings.current.remove)
                    }
                }
            }
            TextButton(onClick = { viewModel.addInstructionRow() }) {
                Text(LocalStrings.current.recipeFormAddInstruction)
            }

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
private fun SectionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun IngredientRowEditor(
    row: IngredientFormRow,
    suggestions: List<String>,
    onChange: (IngredientFormRow) -> Unit,
    onRemove: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.fillMaxWidth()
        ) {
            IngredientNameField(
                value = row.name,
                suggestions = suggestions,
                onValueChange = { onChange(row.copy(name = it)) },
                modifier = Modifier.weight(2f)
            )
            OutlinedTextField(
                value = row.quantity,
                onValueChange = { onChange(row.copy(quantity = it)) },
                label = { Text(LocalStrings.current.qty) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.fillMaxWidth().padding(top = spacing.xs)
        ) {
            UnitDropdownField(
                value = row.unit,
                onValueChange = { onChange(row.copy(unit = it)) },
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRemove) {
                Text(LocalStrings.current.remove)
            }
        }
    }
}

