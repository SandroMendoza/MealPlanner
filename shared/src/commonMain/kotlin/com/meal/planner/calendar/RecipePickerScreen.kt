package com.meal.planner.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.AddRecipeToSlotUseCase
import com.meal.domain.usecase.ReplaceRecipeInSlotUseCase
import com.meal.planner.common.displayName
import com.meal.planner.common.icon
import com.meal.planner.recipes.RecipeItem
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.recipes.RecipeViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun RecipePickerScreen(
    date: LocalDate,
    mealType: MealType?,
    replaceSlotId: String?,
    onBack: () -> Unit,
    onAssigned: () -> Unit,
    recipeViewModel: RecipeViewModel = koinViewModel(),
    addRecipeToSlotUseCase: AddRecipeToSlotUseCase = koinInject(),
    replaceRecipeInSlotUseCase: ReplaceRecipeInSlotUseCase = koinInject()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by recipeViewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var pendingRecipe by remember { mutableStateOf<Recipe?>(null) }

    fun assign(recipeId: Uuid, chosenMealType: MealType) {
        coroutineScope.launch {
            if (replaceSlotId != null) {
                replaceRecipeInSlotUseCase.execute(Uuid.parse(replaceSlotId), recipeId)
            } else {
                addRecipeToSlotUseCase.execute(date, chosenMealType, recipeId)
            }
            onAssigned()
        }
    }

    pendingRecipe?.let { recipe ->
        MealTypeDialog(
            recipeName = recipe.name,
            onSelect = { chosenMealType ->
                pendingRecipe = null
                assign(recipe.id, chosenMealType)
            },
            onDismiss = { pendingRecipe = null }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Pick a recipe", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Cancel") }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = recipeViewModel::onSearchQueryChange,
                placeholder = { Text("Search recipes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { recipeViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth().padding(spacing.lg)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    uiState.showEmptyState -> Text(
                        text = uiState.emptyStateMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> LazyColumn(
                        contentPadding = PaddingValues(start = spacing.lg, end = spacing.lg, bottom = spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        items(uiState.filteredRecipes) { recipe ->
                            RecipeItem(
                                recipe = recipe,
                                onClick = {
                                    if (mealType != null) {
                                        assign(recipe.id, mealType)
                                    } else {
                                        pendingRecipe = recipe
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MealTypeDialog(
    recipeName: String,
    onSelect: (MealType) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add \"$recipeName\" to...", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                MealType.entries.forEach { mealType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mealType) }
                            .padding(vertical = spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.md)
                    ) {
                        Icon(
                            imageVector = mealType.icon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(mealType.displayName(), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
