package com.meal.planner.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.planner.components.CurrencyDropdownField
import com.meal.planner.components.LanguageDropdownField
import com.meal.planner.components.SettingsRow
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAboutClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.screenVertical)
        ) {
            SectionLabel("Preferences")
            SettingsRow(label = "Notifications", icon = Icons.Default.Notifications) {
                Switch(checked = uiState.notificationsEnabled, onCheckedChange = viewModel::setNotificationsEnabled)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = "Dark mode", icon = Icons.Default.DarkMode) {
                Switch(checked = uiState.darkModeEnabled, onCheckedChange = viewModel::setDarkModeEnabled)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(Modifier.height(spacing.sectionGap))
            SectionLabel("Localization")
            LanguageDropdownField(
                value = uiState.language,
                onValueChange = viewModel::setLanguage,
                modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm)
            )
            CurrencyDropdownField(
                value = uiState.currency,
                onValueChange = viewModel::setCurrency,
                modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm)
            )

            Spacer(Modifier.height(spacing.sectionGap))
            SectionLabel("About")
            SettingsRow(label = "About Meal Planner", icon = Icons.Default.Info, onClick = onAboutClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = "Privacy Policy", icon = Icons.Default.PrivacyTip, onClick = onPrivacyPolicyClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
