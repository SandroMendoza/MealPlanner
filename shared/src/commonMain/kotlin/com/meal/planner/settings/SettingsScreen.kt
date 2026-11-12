package com.meal.planner.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.CalendarViewMode
import com.meal.planner.components.CurrencyDropdownField
import com.meal.planner.components.LanguageDropdownField
import com.meal.planner.components.SettingsRow
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun CalendarViewMode.label(): String = when (this) {
    CalendarViewMode.DAY -> LocalStrings.current.day
    CalendarViewMode.WEEK -> LocalStrings.current.week
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
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
                title = { Text(LocalStrings.current.settings, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = LocalStrings.current.back)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.screenVertical)
        ) {
            SectionLabel(LocalStrings.current.settingsPreferences)
            SettingsRow(label = LocalStrings.current.settingsNotifications, icon = Icons.Default.Notifications, onClick = onNotificationSettingsClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsDarkMode, icon = Icons.Default.DarkMode) {
                Switch(checked = uiState.darkModeEnabled, onCheckedChange = viewModel::setDarkModeEnabled)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsLockPastDates, icon = Icons.Default.Lock) {
                Switch(checked = uiState.lockPastDates, onCheckedChange = viewModel::setLockPastDates)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsReadAloud, icon = Icons.AutoMirrored.Filled.VolumeUp) {
                Switch(checked = uiState.readAloudEnabled, onCheckedChange = viewModel::setReadAloudEnabled)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsDefaultCalendarView, icon = Icons.Default.CalendarMonth) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.width(120.dp)) {
                    CalendarViewMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = uiState.defaultCalendarView == mode,
                            onClick = { viewModel.setDefaultCalendarView(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = CalendarViewMode.entries.size),
                            icon = {}
                        ) {
                            Text(mode.label(), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(Modifier.height(spacing.sectionGap))
            SectionLabel(LocalStrings.current.settingsLocalization)
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
            SectionLabel(LocalStrings.current.settingsAbout)
            SettingsRow(label = LocalStrings.current.settingsAboutMealPlanner, icon = Icons.Default.Info, onClick = onAboutClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(label = LocalStrings.current.settingsPrivacyPolicy, icon = Icons.Default.PrivacyTip, onClick = onPrivacyPolicyClick) {
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
