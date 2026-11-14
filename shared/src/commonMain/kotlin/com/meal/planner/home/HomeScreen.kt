package com.meal.planner.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.meal.domain.model.MealPlanEntry
import com.meal.domain.model.Recipe
import com.meal.planner.common.displayName
import com.meal.planner.common.icon
import com.meal.planner.components.RecipeThumbnail
import com.meal.planner.components.recipeImageModel
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.common.today
import com.meal.presentation.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun HomeScreen(
    onViewAllClick: () -> Unit,
    onAddMealClick: () -> Unit,
    onShoppingListClick: () -> Unit,
    onMealClick: (Uuid) -> Unit,
    onNotificationsClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val spacing = MealPlannerTokens.spacing
    val todaysPlanParts = mutableListOf<String>()
    for (entry in uiState.todaysEntries) {
        todaysPlanParts += "${entry.mealType.displayName()}: ${entry.recipe.name}"
    }
    val todaysPlanSpokenText = todaysPlanParts.joinToString(". ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = if (uiState.greetingName.isBlank()) {
                        LocalStrings.current.homeGreetingDefault
                    } else {
                        LocalStrings.current.homeGreetingWithName(uiState.greetingName)
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = LocalStrings.current.homeSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                if (uiState.readAloudAvailable) {
                    IconButton(
                        onClick = { viewModel.toggleReadAloud(todaysPlanSpokenText) },
                        modifier = Modifier
                            .size(MealPlannerTokens.iconSize.xl.minus(4.dp))
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (uiState.isSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (uiState.isSpeaking) {
                                LocalStrings.current.homeStopReadAloudCd
                            } else {
                                LocalStrings.current.homeReadAloudCd
                            }
                        )
                    }
                }
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(MealPlannerTokens.iconSize.xl.minus(4.dp))
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = LocalStrings.current.homeNotificationsCd)
                }
            }
        }

        Spacer(Modifier.height(spacing.sectionGap))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(LocalStrings.current.homeTodaysPlan, style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onViewAllClick) {
                Text(LocalStrings.current.homeViewAll, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }

        val todayDate = today()
        Text(
            text = "${LocalStrings.current.weekdayShort(todayDate.dayOfWeek)}, " +
                "${LocalStrings.current.monthAbbreviation(todayDate.monthNumber)} ${todayDate.dayOfMonth}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(spacing.sm))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(spacing.xxxl),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                uiState.todaysEntries.isEmpty() -> Text(
                    text = LocalStrings.current.homeNoMealsPlannedToday,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(spacing.xxl)
                )
                else -> Column {
                    uiState.todaysEntries.forEachIndexed { index, entry ->
                        TodayPlanRow(entry = entry, onClick = { onMealClick(entry.recipe.id) })
                        if (index != uiState.todaysEntries.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(spacing.sectionGap))

        Text(LocalStrings.current.homeQuickActions, style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(spacing.sm))

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), modifier = Modifier.fillMaxWidth()) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                label = LocalStrings.current.homeAddAMeal,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = Icons.Default.Add,
                onClick = onAddMealClick
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                label = LocalStrings.current.homeShoppingList,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.ShoppingCart,
                onClick = onShoppingListClick
            )
        }

        uiState.featuredRecipe?.let { recipe ->
            Spacer(Modifier.height(spacing.sectionGap))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(LocalStrings.current.homeRecipeOfTheDay, style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = viewModel::shuffleFeaturedRecipe) {
                    Icon(Icons.Filled.Shuffle, contentDescription = LocalStrings.current.homeShuffleRecipeCd)
                }
            }

            Spacer(Modifier.height(spacing.sm))

            FeaturedRecipeCard(recipe = recipe, onClick = { onMealClick(recipe.id) })
        }
    }
}

@Composable
private fun TodayPlanRow(entry: MealPlanEntry, onClick: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RecipeThumbnail(
            imageUrl = entry.recipe.imageUrl,
            fallbackIcon = entry.mealType.icon(),
            size = MealPlannerTokens.iconSize.xl,
            shape = MaterialTheme.shapes.small
        )
        Spacer(Modifier.width(spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.mealType.displayName(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = entry.recipe.name, style = MaterialTheme.typography.titleMedium)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
private fun FeaturedRecipeCard(recipe: Recipe, onClick: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
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
                    model = recipeImageModel(recipe.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(
            text = recipe.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(spacing.cardPadding)
        )
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    label: String,
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        Column(
            modifier = Modifier.padding(spacing.lg).height(110.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(MealPlannerTokens.iconSize.lg)
                    .background(contentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = contentColor)
            }
            Text(label, style = MaterialTheme.typography.titleMedium, color = contentColor)
        }
    }
}
