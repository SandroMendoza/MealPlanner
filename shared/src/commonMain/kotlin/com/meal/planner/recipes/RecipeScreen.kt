package com.meal.planner.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.displayName
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.planner.theme.activeFilterChipColors
import com.meal.presentation.recipes.RecipeEmptyState
import com.meal.presentation.recipes.RecipeUiState
import com.meal.presentation.recipes.RecipeViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel = koinViewModel(),
    onRecipeClick: (Uuid) -> Unit = {},
    onAddRecipeClick: () -> Unit = {},
    initialFavoritesOnly: Boolean = false,
    initialCreatedOnly: Boolean = false
) {
    val spacing = MealPlannerTokens.spacing
    val focusManager = LocalFocusManager.current
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(initialFavoritesOnly, initialCreatedOnly) {
        if (initialFavoritesOnly) viewModel.onFavoritesTabSelected(true)
        if (initialCreatedOnly) viewModel.onCreatedOnlyTabSelected(true)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipeClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = LocalStrings.current.recipesAddCd)
            }
        }
    ) {
        val uiState by viewModel.state.collectAsStateWithLifecycle()
        val hasActiveFilters = uiState.selectedCategory != null ||
            uiState.showFavoritesOnly ||
            uiState.showCreatedOnly ||
            uiState.prepTimeRange != 0f..180f ||
            uiState.calorieRange != 0f..1500f
        val filteredRecipes = remember(uiState) { uiState.filteredRecipes }
        val showEmptyState = !uiState.isLoading && filteredRecipes.isEmpty() && uiState.errorMessage == null

        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.errorMessage != null -> Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = viewModel::onSearchQueryChange,
                                placeholder = { Text(LocalStrings.current.searchRecipesPlaceholder) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = LocalStrings.current.clearSearch)
                                        }
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                shape = MaterialTheme.shapes.extraLarge,
                                modifier = Modifier.weight(1f)
                            )

                            if (hasActiveFilters) {
                                FilledIconButton(
                                    onClick = { showFilterSheet = true },
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.FilterAlt, contentDescription = LocalStrings.current.recipesFiltersActiveCd)
                                }
                            } else {
                                IconButton(onClick = { showFilterSheet = true }) {
                                    Icon(Icons.Default.FilterAlt, contentDescription = LocalStrings.current.recipesFilters)
                                }
                            }
                        }
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            items(listOf<MealType?>(null) + MealType.entries) { mealType ->
                                FilterChip(
                                    selected = uiState.selectedMealType == mealType,
                                    onClick = { viewModel.onMealTypeSelected(mealType) },
                                    label = { Text(mealType.quickFilterLabel()) },
                                    colors = activeFilterChipColors()
                                )
                            }
                        }
                    }

                    if (showEmptyState) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = uiState.emptyState.message(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(top = spacing.lg),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(filteredRecipes, key = { it.id }) { recipe ->
                            RecipeGridCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            RecipeFilterSheet(
                uiState = uiState,
                onDismiss = { showFilterSheet = false },
                onCategorySelected = viewModel::onCategorySelected,
                onFavoritesOnlyChange = viewModel::onFavoritesTabSelected,
                onCreatedOnlyChange = viewModel::onCreatedOnlyTabSelected,
                onPrepTimeRangeChange = viewModel::onPrepTimeRangeChange,
                onCalorieRangeChange = viewModel::onCalorieRangeChange,
                onClearFilters = {
                    viewModel.onCategorySelected(null)
                    viewModel.onFavoritesTabSelected(false)
                    viewModel.onCreatedOnlyTabSelected(false)
                    viewModel.onPrepTimeRangeChange(0f..180f)
                    viewModel.onCalorieRangeChange(0f..1500f)
                }
            )
        }
    }
}

@Composable
private fun MealType?.quickFilterLabel(): String = when (this) {
    null -> LocalStrings.current.all
    MealType.SNACK -> LocalStrings.current.recipesSnacks
    else -> displayName()
}

@Composable
fun RecipeEmptyState.message(): String = when (this) {
    RecipeEmptyState.NO_RECIPES -> LocalStrings.current.recipesNoRecipesYet
    RecipeEmptyState.NO_FAVORITES -> LocalStrings.current.recipesNoFavoritesYet
    RecipeEmptyState.NO_CREATED -> LocalStrings.current.recipesNoCreatedYet
    RecipeEmptyState.NO_MATCHES -> LocalStrings.current.recipesNoMatchesFilters
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeFilterSheet(
    uiState: RecipeUiState,
    onDismiss: () -> Unit,
    onCategorySelected: (FoodCategory?) -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit,
    onCreatedOnlyChange: (Boolean) -> Unit,
    onPrepTimeRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onCalorieRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onClearFilters: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.sm)
        ) {
            Text(
                text = LocalStrings.current.recipesFilters,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(LocalStrings.current.recipesFavoritesOnly, style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.showFavoritesOnly, onCheckedChange = onFavoritesOnlyChange)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(LocalStrings.current.recipesCreatedByMe, style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.showCreatedOnly, onCheckedChange = onCreatedOnlyChange)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.lg))

            SectionLabel(LocalStrings.current.category)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(listOf<FoodCategory?>(null) + FoodCategory.entries) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category?.displayName() ?: LocalStrings.current.all) },
                        colors = activeFilterChipColors()
                    )
                }
            }

            Column(modifier = Modifier.padding(top = spacing.lg)) {
                SectionLabel(
                    LocalStrings.current.recipesPrepTimeRange(
                        uiState.prepTimeRange.start.toInt(),
                        uiState.prepTimeRange.endInclusive.toInt()
                    )
                )
                RangeSlider(
                    value = uiState.prepTimeRange,
                    onValueChange = onPrepTimeRangeChange,
                    valueRange = 0f..180f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(modifier = Modifier.padding(top = spacing.sm, bottom = spacing.lg)) {
                SectionLabel(
                    LocalStrings.current.recipesCalorieRange(
                        uiState.calorieRange.start.toInt(),
                        uiState.calorieRange.endInclusive.toInt()
                    )
                )
                RangeSlider(
                    value = uiState.calorieRange,
                    onValueChange = onCalorieRangeChange,
                    valueRange = 0f..1500f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextButton(
                onClick = onClearFilters,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(LocalStrings.current.recipesClearFilters)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val spacing = MealPlannerTokens.spacing
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = spacing.xs)
    )
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun RecipeGridCard(recipe: Recipe, onClick: () -> Unit) {
    val spacing = MealPlannerTokens.spacing

    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                if (recipe.imageUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                } else {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Column(modifier = Modifier.padding(spacing.sm)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = spacing.xxs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.xxs)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(MealPlannerTokens.iconSize.sm)
                    )
                    Text(
                        text = LocalStrings.current.minutesLabel(recipe.preparationTimeMinutes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

