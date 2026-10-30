package com.meal.planner.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.Recipe
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.displayName
import com.meal.planner.components.RecipeThumbnail
import com.meal.planner.theme.MealPlannerPalette
import com.meal.planner.theme.MealPlannerTokens
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
    onAddRecipeClick: () -> Unit = {}
) {
    val spacing = MealPlannerTokens.spacing
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipeClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add recipe")
            }
        }
    ) {
        val uiState by viewModel.state.collectAsStateWithLifecycle()
        val hasActiveFilters = uiState.selectedCategory != null ||
            uiState.showFavoritesOnly ||
            uiState.prepTimeRange != 0f..180f ||
            uiState.calorieRange != 0f..1500f

        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.errorMessage != null -> Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = viewModel::onSearchQueryChange,
                                placeholder = { Text("Search recipes...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                        }
                                    }
                                },
                                singleLine = true,
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
                                    Icon(Icons.Default.Menu, contentDescription = "Filters (active)")
                                }
                            } else {
                                IconButton(onClick = { showFilterSheet = true }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Filters")
                                }
                            }
                        }
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            items(listOf<MealType?>(null) + MealType.entries) { mealType ->
                                FilterChip(
                                    selected = uiState.selectedMealType == mealType,
                                    onClick = { viewModel.onMealTypeSelected(mealType) },
                                    label = { Text(mealType.quickFilterLabel()) }
                                )
                            }
                        }
                    }

                    if (uiState.showEmptyState) {
                        item {
                            Text(
                                text = uiState.emptyStateMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(top = spacing.lg),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(uiState.filteredRecipes) { recipe ->
                            RecipeItem(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) },
                                onToggleFavorite = { viewModel.onToggleFavorite(recipe) }
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
                onPrepTimeRangeChange = viewModel::onPrepTimeRangeChange,
                onCalorieRangeChange = viewModel::onCalorieRangeChange,
                onClearFilters = {
                    viewModel.onCategorySelected(null)
                    viewModel.onFavoritesTabSelected(false)
                    viewModel.onPrepTimeRangeChange(0f..180f)
                    viewModel.onCalorieRangeChange(0f..1500f)
                }
            )
        }
    }
}

private fun MealType?.quickFilterLabel(): String = when (this) {
    null -> "All"
    MealType.SNACK -> "Snacks"
    else -> displayName()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeFilterSheet(
    uiState: RecipeUiState,
    onDismiss: () -> Unit,
    onCategorySelected: (FoodCategory?) -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit,
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
                text = "Filters",
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
                Text("Favorites only", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.showFavoritesOnly, onCheckedChange = onFavoritesOnlyChange)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.lg))

            SectionLabel("Category")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(listOf<FoodCategory?>(null) + FoodCategory.entries) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category?.displayName() ?: "All") }
                    )
                }
            }

            Column(modifier = Modifier.padding(top = spacing.lg)) {
                SectionLabel(
                    "Prep time: ${uiState.prepTimeRange.start.toInt()}" +
                        "–${uiState.prepTimeRange.endInclusive.toInt()} min"
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
                    "Calories: ${uiState.calorieRange.start.toInt()}" +
                        "–${uiState.calorieRange.endInclusive.toInt()}"
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
                Text("Clear filters")
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
fun RecipeItem(recipe: Recipe, onClick: () -> Unit = {}, onToggleFavorite: (() -> Unit)? = null) {
    val spacing = MealPlannerTokens.spacing

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecipeThumbnail(
                imageUrl = recipe.imageUrl,
                fallbackIcon = Icons.AutoMirrored.Filled.MenuBook,
                size = 56.dp,
                shape = MaterialTheme.shapes.medium
            )
            Column(modifier = Modifier.padding(start = spacing.md).weight(1f)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                recipe.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = spacing.xxs)
                    )
                }
                Text(
                    text = "${recipe.category.displayName()} · ${recipe.preparationTimeMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = spacing.xxs)
                )
            }
            if (onToggleFavorite != null) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (recipe.isFavorite) MealPlannerPalette.FavoriteRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
