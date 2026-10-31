package com.meal.planner.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.meal.planner.common.displayName
import com.meal.planner.theme.MealPlannerPalette
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
    onAddToMealPlan: () -> Unit = {},
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
        contentWindowInsets = WindowInsets(0)
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
                recipe != null -> Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
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
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(64.dp)
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

                        RecipeDetailOverlayButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(spacing.lg)
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(spacing.lg),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            RecipeDetailOverlayButton(
                                icon = Icons.Filled.Edit,
                                contentDescription = "Edit recipe",
                                onClick = { onEdit(recipeId) }
                            )
                            RecipeDetailOverlayButton(
                                icon = Icons.Filled.Delete,
                                contentDescription = "Delete recipe",
                                tint = MaterialTheme.colorScheme.error,
                                onClick = { viewModel.delete() }
                            )
                            RecipeDetailOverlayButton(
                                icon = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (recipe.isFavorite) MealPlannerPalette.FavoriteRed else MaterialTheme.colorScheme.onSurface,
                                onClick = { viewModel.toggleFavorite() }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .offset(y = -spacing.lg)
                            .clip(
                                androidx.compose.foundation.shape.RoundedCornerShape(
                                    topStart = MealPlannerTokens.radius.xl,
                                    topEnd = MealPlannerTokens.radius.xl
                                )
                            )
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = spacing.lg)
                    ) {
                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = spacing.lg)
                        )

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                recipe.description?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = spacing.xxs)
                                    )
                                }

                                Row(
                                    modifier = Modifier.padding(top = spacing.sm),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RecipeDetailMetaItem(
                                        icon = Icons.Filled.AccessTime,
                                        text = "${recipe.preparationTimeMinutes} min"
                                    )
                                    RecipeDetailMetaItem(
                                        icon = Icons.Filled.People,
                                        text = "${recipe.servings} serving" + if (recipe.servings == 1) "" else "s"
                                    )
                                    RecipeDetailMetaItem(
                                        icon = Icons.AutoMirrored.Filled.MenuBook,
                                        text = recipe.category.displayName()
                                    )
                                }

                                recipe.calories?.let {
                                    Text(
                                        text = "$it calories",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = spacing.xs)
                                    )
                                }

                                Text(
                                    text = "Ingredients",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = spacing.lg, bottom = spacing.xs)
                                )
                            }
                            items(recipe.ingredients) { ingredient ->
                                Text(
                                    text = "• ${ingredient.quantity} ${ingredient.unit.unit} ${ingredient.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = spacing.sm)
                                )
                            }

                            item {
                                Text(
                                    text = "Instructions",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = spacing.lg, bottom = spacing.xs)
                                )
                            }
                            items(recipe.instructions) { instruction ->
                                Text(
                                    text = "${instruction.stepNumber}. ${instruction.description}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = spacing.sm)
                                )
                            }
                        }

                        Button(
                            onClick = onAddToMealPlan,
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacing.sm, bottom = spacing.lg)
                        ) {
                            Text("Add to Meal Plan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeDetailOverlayButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            contentColor = tint
        ),
        modifier = modifier.size(40.dp)
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Composable
private fun RecipeDetailMetaItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    val spacing = MealPlannerTokens.spacing
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.xxs)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(MealPlannerTokens.iconSize.sm)
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
