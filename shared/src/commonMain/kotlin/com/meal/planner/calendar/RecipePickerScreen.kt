package com.meal.planner.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.AddRecipeToSlotUseCase
import com.meal.domain.usecase.ReplaceRecipeInSlotUseCase
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
    mealType: MealType,
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Pick a recipe", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Cancel") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.showEmptyState -> Text(
                    text = "No recipes yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    items(uiState.recipes) { recipe ->
                        RecipeItem(
                            recipe = recipe,
                            onClick = {
                                coroutineScope.launch {
                                    if (replaceSlotId != null) {
                                        replaceRecipeInSlotUseCase.execute(Uuid.parse(replaceSlotId), recipe.id)
                                    } else {
                                        addRecipeToSlotUseCase.execute(date, mealType, recipe.id)
                                    }
                                    onAssigned()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
