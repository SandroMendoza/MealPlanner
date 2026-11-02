package com.meal.planner.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

private val AVATAR_SIZE = 112.dp
private val EDIT_BADGE_SIZE = 32.dp

@Composable
fun ProfileScreen(
    onMyRecipesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSpendingReportsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.screenVertical)
        ) {
            Text(
                text = LocalStrings.current.profileTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(spacing.sectionGap))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box {
                    ProfileAvatar(name = uiState.name, avatarUrl = uiState.avatarUrl, size = AVATAR_SIZE)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(EDIT_BADGE_SIZE)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        IconButton(onClick = { viewModel.openEditDialog() }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = LocalStrings.current.profileEditProfile,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(spacing.md))

            Text(
                text = uiState.name.ifBlank { LocalStrings.current.profileYourNamePlaceholder },
                style = MaterialTheme.typography.titleLarge,
                color = if (uiState.name.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (uiState.bio.isNotBlank()) {
                Spacer(Modifier.height(spacing.xs))
                Text(
                    text = uiState.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(spacing.sectionGap))

            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileStat(label = LocalStrings.current.profileRecipesStat, value = uiState.recipeCount, modifier = Modifier.weight(1f))
                ProfileStat(label = LocalStrings.current.profileCreatedStat, value = uiState.createdCount, modifier = Modifier.weight(1f))
                ProfileStat(label = LocalStrings.current.profileThisWeekStat, value = uiState.thisWeekCount, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(spacing.sectionGap))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            ProfileNavRow(label = LocalStrings.current.myRecipes, icon = Icons.AutoMirrored.Filled.MenuBook, onClick = onMyRecipesClick)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileNavRow(label = LocalStrings.current.favorites, icon = Icons.Default.Favorite, onClick = onFavoritesClick)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileNavRow(label = LocalStrings.current.spendingReports, icon = Icons.AutoMirrored.Filled.ReceiptLong, onClick = onSpendingReportsClick)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileNavRow(label = LocalStrings.current.settings, icon = Icons.Default.Settings, onClick = onSettingsClick)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        if (uiState.showEditDialog) {
            EditProfileDialog(
                name = uiState.name,
                bio = uiState.bio,
                avatarUrl = uiState.avatarUrl,
                onConfirm = { name, bio, avatarUrl -> viewModel.saveProfile(name, bio, avatarUrl) },
                onDismiss = { viewModel.dismissEditDialog() }
            )
        }
    }
}

@Composable
private fun ProfileAvatar(name: String, avatarUrl: String, size: androidx.compose.ui.unit.Dp) {
    if (avatarUrl.isBlank()) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    } else {
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun ProfileStat(label: String, value: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfileNavRow(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(spacing.md))
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}
