package com.meal.planner.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.recipes.RecipeDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit = {},
    viewModel: RecipeDetailViewModel = koinViewModel(
        parameters = { parametersOf(Uuid.parse(recipeId)) }
    )
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onBack()
    }

    uiState.pendingDeleteConfirmationDates?.let { dates ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirmation() },
            title = { Text("Delete recipe?", style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    "This recipe is scheduled on ${dates.size} future date(s): " +
                        dates.joinToString { it.toString() } +
                        ". Deleting it will remove it from those days.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(force = true) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.recipe?.name.orEmpty(),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                },
                actions = {
                    TextButton(onClick = { onEdit(recipeId) }) { Text("Edit") }
                    TextButton(onClick = { viewModel.delete() }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            val recipe = uiState.recipe
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.errorMessage != null -> Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.notFound -> Text(
                    text = "Recipe not found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
                recipe != null -> LazyColumn(contentPadding = PaddingValues(spacing.lg)) {
                    item {
                        recipe.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Category: ${recipe.category}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = spacing.sm)
                        )
                        Text(
                            text = "Meal types: ${recipe.mealTypes.joinToString()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Prep time: ${recipe.preparationTimeMinutes} min",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Servings: ${recipe.servings}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        recipe.calories?.let {
                            Text(
                                text = "Calories: $it",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = spacing.lg)
                        )
                    }
                    items(recipe.ingredients) { ingredient ->
                        Text(
                            text = "- ${ingredient.quantity} ${ingredient.unit.unit} ${ingredient.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = spacing.xxs)
                        )
                    }
                    item {
                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = spacing.lg)
                        )
                    }
                    items(recipe.instructions) { instruction ->
                        Text(
                            text = "${instruction.stepNumber}. ${instruction.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = spacing.xxs)
                        )
                    }
                }
            }
        }
    }
}
