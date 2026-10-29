package com.meal.planner.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
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
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onDone()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Recipe" else "New Recipe") },
                navigationIcon = {
                    TextButton(onClick = onDone) { Text("Cancel") }
                },
                actions = {
                    TextButton(onClick = { viewModel.save() }, enabled = uiState.canSave && !uiState.isSaving) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.errorMessage?.let { Text(text = it) }

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Category")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FoodCategory.entries.toList()) { category ->
                    FilterChip(
                        selected = uiState.category == category,
                        onClick = { viewModel.updateCategory(category) },
                        label = { Text(category.name) }
                    )
                }
            }

            Text("Meal types")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MealType.entries.toList()) { mealType ->
                    FilterChip(
                        selected = mealType in uiState.mealTypes,
                        onClick = { viewModel.toggleMealType(mealType) },
                        label = { Text(mealType.name) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.preparationTimeMinutes,
                    onValueChange = viewModel::updatePreparationTime,
                    label = { Text("Prep (min)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.servings,
                    onValueChange = viewModel::updateServings,
                    label = { Text("Servings") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.calories,
                    onValueChange = viewModel::updateCalories,
                    label = { Text("Calories") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = uiState.imageUrl,
                onValueChange = viewModel::updateImageUrl,
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ingredients")
            uiState.ingredients.forEachIndexed { index, row ->
                IngredientRowEditor(
                    row = row,
                    onChange = { viewModel.updateIngredientRow(index, it) },
                    onRemove = { viewModel.removeIngredientRow(index) }
                )
            }
            TextButton(onClick = { viewModel.addIngredientRow() }) {
                Text("+ Add ingredient")
            }

            Text("Instructions")
            uiState.instructions.forEachIndexed { index, row ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${index + 1}.")
                    OutlinedTextField(
                        value = row.description,
                        onValueChange = { viewModel.updateInstructionRow(index, InstructionFormRow(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.removeInstructionRow(index) }) {
                        Text("Remove")
                    }
                }
            }
            TextButton(onClick = { viewModel.addInstructionRow() }) {
                Text("+ Add instruction")
            }

            Button(
                onClick = { viewModel.save() },
                enabled = uiState.canSave && !uiState.isSaving,
                modifier = Modifier.fillMaxWidth().wrapContentWidth()
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Recipe")
            }
        }
    }
}

@Composable
private fun IngredientRowEditor(
    row: IngredientFormRow,
    onChange: (IngredientFormRow) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = row.name,
            onValueChange = { onChange(row.copy(name = it)) },
            label = { Text("Name") },
            modifier = Modifier.weight(2f)
        )
        OutlinedTextField(
            value = row.quantity,
            onValueChange = { onChange(row.copy(quantity = it)) },
            label = { Text("Qty") },
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = {
            val units = MeasurementUnit.entries
            val next = units[(row.unit.ordinal + 1) % units.size]
            onChange(row.copy(unit = next))
        }) {
            Text(row.unit.unit)
        }
        TextButton(onClick = onRemove) {
            Text("Remove")
        }
    }
}
