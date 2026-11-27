package com.meal.planner.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.common.displayName
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.planner.theme.activeFilterChipColors
import com.meal.presentation.recipes.RecipeUiState
import com.meal.presentation.recipes.RecipeViewModel

/** The filter button next to recipe search; filled while any filter is active. */
@Composable
internal fun RecipeFilterButton(hasActiveFilters: Boolean, onClick: () -> Unit) {
    if (hasActiveFilters) {
        FilledIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Default.FilterAlt, contentDescription = LocalStrings.current.recipesFiltersActiveCd)
        }
    } else {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.FilterAlt, contentDescription = LocalStrings.current.recipesFilters)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipeFilterSheet(
    uiState: RecipeUiState,
    viewModel: RecipeViewModel,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = LocalCompactHeight.current)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
                    .padding(top = spacing.lg)
                    .toggleable(value = uiState.showFavoritesOnly, role = Role.Switch, onValueChange = viewModel::onFavoritesTabSelected),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(LocalStrings.current.recipesFavoritesOnly, style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.showFavoritesOnly, onCheckedChange = null)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(value = uiState.showCreatedOnly, role = Role.Switch, onValueChange = viewModel::onCreatedOnlyTabSelected),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(LocalStrings.current.recipesCreatedByMe, style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.showCreatedOnly, onCheckedChange = null)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.lg))

            SectionLabel(LocalStrings.current.category)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(listOf<FoodCategory?>(null) + FoodCategory.entries) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(category?.displayName() ?: LocalStrings.current.all) },
                        colors = activeFilterChipColors()
                    )
                }
            }

            Column(modifier = Modifier.padding(top = spacing.lg)) {
                SectionLabel(LocalStrings.current.mealType)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    items(listOf<MealType?>(null) + MealType.entries) { mealType ->
                        FilterChip(
                            selected = uiState.selectedMealType == mealType,
                            onClick = { viewModel.onMealTypeSelected(mealType) },
                            label = { Text(mealType?.displayName() ?: LocalStrings.current.all) },
                            colors = activeFilterChipColors()
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(top = spacing.lg)) {
                SectionLabel(LocalStrings.current.cuisine)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    items(listOf<Cuisine?>(null) + Cuisine.entries) { cuisine ->
                        FilterChip(
                            selected = uiState.selectedCuisine == cuisine,
                            onClick = { viewModel.onCuisineSelected(cuisine) },
                            label = { Text(cuisine?.displayName() ?: LocalStrings.current.all) },
                            colors = activeFilterChipColors()
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(top = spacing.lg, bottom = spacing.lg)) {
                val start = uiState.prepTimeRange.start.toInt()
                val end = uiState.prepTimeRange.endInclusive.toInt()
                SectionLabel(
                    if (uiState.prepTimeHasNoMax) {
                        LocalStrings.current.recipesPrepTimeRangeNoMax(start, end)
                    } else {
                        LocalStrings.current.recipesPrepTimeRange(start, end)
                    }
                )
                RangeSlider(
                    value = uiState.prepTimeRange,
                    onValueChange = viewModel::onPrepTimeRangeChange,
                    valueRange = RecipeUiState.FULL_PREP_TIME_RANGE,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextButton(
                onClick = viewModel::clearFilters,
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
