package com.meal.planner.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.MealType
import com.meal.domain.usecase.AddRecipeToSlotUseCase
import com.meal.domain.usecase.ReplaceRecipeInSlotUseCase
import com.meal.planner.components.MealTypeDialog
import com.meal.planner.recipes.RecipeGridCard
import com.meal.planner.recipes.message
import com.meal.planner.strings.LocalStrings
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
    onCreateRecipeClick: () -> Unit,
    recipeViewModel: RecipeViewModel = koinViewModel(),
    addRecipeToSlotUseCase: AddRecipeToSlotUseCase = koinInject(),
    replaceRecipeInSlotUseCase: ReplaceRecipeInSlotUseCase = koinInject()
) {
    val spacing = MealPlannerTokens.spacing
    val focusManager = LocalFocusManager.current
    val uiState by recipeViewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var pendingRecipe by remember { mutableStateOf<Recipe?>(null) }
    var assignDate by remember { mutableStateOf(date) }
    val filteredRecipes = remember(uiState) { uiState.filteredRecipes }
    val showEmptyState = !uiState.isLoading && filteredRecipes.isEmpty() && uiState.errorMessage == null

    fun assign(recipeId: Uuid, chosenMealType: MealType, onDate: LocalDate = date) {
        coroutineScope.launch {
            if (replaceSlotId != null) {
                replaceRecipeInSlotUseCase.execute(Uuid.parse(replaceSlotId), recipeId)
            } else {
                addRecipeToSlotUseCase.execute(onDate, chosenMealType, recipeId)
            }
            onAssigned()
        }
    }

    pendingRecipe?.let { recipe ->
        MealTypeDialog(
            recipeName = recipe.name,
            date = assignDate,
            onDateChange = { assignDate = it },
            onSelect = { chosenMealType ->
                pendingRecipe = null
                assign(recipe.id, chosenMealType, assignDate)
            },
            onDismiss = { pendingRecipe = null }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.recipePickerTitle, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(LocalStrings.current.cancel) }
                },
                windowInsets = WindowInsets(0)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateRecipeClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = LocalStrings.current.recipePickerCreateRecipeCd)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = recipeViewModel::onSearchQueryChange,
                placeholder = { Text(LocalStrings.current.searchRecipesPlaceholder) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { recipeViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = LocalStrings.current.clearSearch)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth().padding(spacing.lg)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    showEmptyState -> Text(
                        text = uiState.emptyState.message(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = spacing.lg, end = spacing.lg, bottom = spacing.lg),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        items(filteredRecipes, key = { it.id }) { recipe ->
                            RecipeGridCard(
                                recipe = recipe,
                                onClick = {
                                    if (mealType != null) {
                                        assign(recipe.id, mealType)
                                    } else {
                                        assignDate = date
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
