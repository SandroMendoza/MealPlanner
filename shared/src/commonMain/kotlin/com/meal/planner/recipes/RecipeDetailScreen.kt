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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.meal.domain.model.Instruction
import com.meal.domain.usecase.AddRecipeToSlotUseCase
import com.meal.planner.common.displayName
import com.meal.planner.components.MealTypeDialog
import com.meal.planner.components.recipeImageModel
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerPalette
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.common.formatQuantity
import com.meal.presentation.common.today
import com.meal.presentation.recipes.RecipeDetailViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
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
    ),
    addRecipeToSlotUseCase: AddRecipeToSlotUseCase = koinInject()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showMealTypeDialog by remember { mutableStateOf(false) }
    var assignDate by remember { mutableStateOf(today()) }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) onBack()
    }

    uiState.pendingDeleteConfirmationDates?.let { dates ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirmation() },
            title = { Text(LocalStrings.current.recipeDetailDeleteTitle, style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    LocalStrings.current.recipeDetailDeleteBody(dates.size, dates.joinToString { it.toString() }),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(force = true) }) {
                    Text(LocalStrings.current.recipeDetailDelete, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) { Text(LocalStrings.current.cancel) }
            }
        )
    }

    if (showMealTypeDialog) {
        uiState.recipe?.let { recipe ->
            MealTypeDialog(
                recipeName = recipe.name,
                date = assignDate,
                onDateChange = { assignDate = it },
                onSelect = { mealType ->
                    coroutineScope.launch {
                        addRecipeToSlotUseCase.execute(assignDate, mealType, recipe.id)
                        showMealTypeDialog = false
                        onAddToMealPlan()
                    }
                },
                onDismiss = { showMealTypeDialog = false }
            )
        }
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
                    text = LocalStrings.current.recipeDetailNotFound,
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
                                model = recipeImageModel(recipe.imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        RecipeDetailOverlayButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = LocalStrings.current.back,
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
                                contentDescription = LocalStrings.current.recipeDetailEditCd,
                                onClick = { onEdit(recipeId) }
                            )
                            RecipeDetailOverlayButton(
                                icon = Icons.Filled.Delete,
                                contentDescription = LocalStrings.current.recipeDetailDeleteCd,
                                tint = MaterialTheme.colorScheme.error,
                                onClick = { viewModel.delete() }
                            )
                            RecipeDetailOverlayButton(
                                icon = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (recipe.isFavorite) {
                                    LocalStrings.current.recipeDetailRemoveFavoriteCd
                                } else {
                                    LocalStrings.current.recipeDetailAddFavoriteCd
                                },
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
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                Text(
                                    text = recipe.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = spacing.lg)
                                )

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
                                        text = LocalStrings.current.minutesLabel(recipe.preparationTimeMinutes)
                                    )
                                    RecipeDetailMetaItem(
                                        icon = Icons.Filled.People,
                                        text = LocalStrings.current.servingsLabel(recipe.servings)
                                    )
                                    RecipeDetailMetaItem(
                                        icon = Icons.AutoMirrored.Filled.MenuBook,
                                        text = recipe.category.displayName()
                                    )
                                }

                                recipe.cuisine?.let { cuisine ->
                                    Row(modifier = Modifier.padding(top = spacing.xs)) {
                                        RecipeDetailMetaItem(
                                            icon = Icons.Filled.Public,
                                            text = cuisine.displayName()
                                        )
                                    }
                                }

                                recipe.calories?.let {
                                    Text(
                                        text = LocalStrings.current.caloriesLabel(it),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = spacing.xs)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = spacing.lg, bottom = spacing.xs),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = LocalStrings.current.ingredients,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (uiState.readAloudAvailable) {
                                        IconButton(onClick = { viewModel.toggleReadIngredientsAloud() }) {
                                            Icon(
                                                imageVector = if (uiState.ingredientsSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = if (uiState.ingredientsSpeaking) {
                                                    LocalStrings.current.recipeDetailStopIngredientsCd
                                                } else {
                                                    LocalStrings.current.recipeDetailReadIngredientsCd
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            items(recipe.ingredients, key = { it.id }) { ri ->
                                Text(
                                    text = "• ${formatQuantity(ri.quantity)} ${ri.unit.unit} ${ri.ingredient.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = spacing.sm)
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = spacing.lg, bottom = spacing.xs),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = LocalStrings.current.instructions,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (uiState.readAloudAvailable && !uiState.cookModeActive) {
                                        TextButton(onClick = { viewModel.startCookMode() }) {
                                            Icon(
                                                imageVector = Icons.Filled.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(MealPlannerTokens.iconSize.sm)
                                            )
                                            Text(
                                                text = LocalStrings.current.recipeDetailCookMode,
                                                style = MaterialTheme.typography.labelLarge,
                                                modifier = Modifier.padding(start = spacing.xxs)
                                            )
                                        }
                                    }
                                }
                            }
                            if (uiState.cookModeActive) {
                                item {
                                    CookModeStep(
                                        instructions = recipe.instructions,
                                        stepIndex = uiState.cookModeStepIndex,
                                        speaking = uiState.cookModeSpeaking,
                                        onPrevious = { viewModel.previousStep() },
                                        onNext = { viewModel.nextStep() },
                                        onTogglePlayPause = { viewModel.togglePlayPause() },
                                        onStop = { viewModel.stopCookMode() }
                                    )
                                }
                            } else {
                                items(recipe.instructions, key = { it.stepNumber }) { instruction ->
                                    Text(
                                        text = "${instruction.stepNumber}. ${instruction.description}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = spacing.sm)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                assignDate = today()
                                showMealTypeDialog = true
                            },
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacing.sm, bottom = spacing.lg)
                        ) {
                            Text(LocalStrings.current.recipeDetailAddToMealPlan)
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
private fun CookModeStep(
    instructions: List<Instruction>,
    stepIndex: Int,
    speaking: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val step = instructions.getOrNull(stepIndex) ?: return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(spacing.lg)
    ) {
        Text(
            text = "${step.stepNumber}. ${step.description}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious, enabled = stepIndex > 0) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = LocalStrings.current.recipeDetailCookModePreviousCd)
            }
            IconButton(onClick = onTogglePlayPause) {
                Icon(
                    imageVector = if (speaking) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (speaking) {
                        LocalStrings.current.recipeDetailCookModePauseCd
                    } else {
                        LocalStrings.current.recipeDetailCookModePlayCd
                    }
                )
            }
            IconButton(onClick = onNext, enabled = stepIndex < instructions.lastIndex) {
                Icon(Icons.Filled.SkipNext, contentDescription = LocalStrings.current.recipeDetailCookModeNextCd)
            }
            IconButton(onClick = onStop) {
                Icon(Icons.Filled.Close, contentDescription = LocalStrings.current.recipeDetailCookModeStopCd)
            }
        }
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
