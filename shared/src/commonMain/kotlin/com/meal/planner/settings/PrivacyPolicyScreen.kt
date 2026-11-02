package com.meal.planner.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meal.planner.theme.MealPlannerTokens

/**
 * Placeholder policy text, not legally reviewed. Replace before production release.
 */
private val PRIVACY_POLICY_PARAGRAPHS = listOf(
    "Last updated: [DATE — PLACEHOLDER]",
    "This placeholder Privacy Policy describes, in general terms, what information Meal Planner may collect and how it may be used. It is provided as a starting point only and must be reviewed and replaced with an accurate, legally-reviewed policy before this app is distributed to real users.",
    "Information We Store\nMeal Planner stores your recipes, meal plans, shopping lists, profile information (name, bio, avatar), and app preferences (notifications, theme, language, currency) locally on your device. As of this version, no account system or remote server exists, and no data described above is transmitted off your device by the app itself.",
    "Images\nIf you choose a profile photo or recipe image from your device, that image is referenced locally and is not uploaded anywhere by the app.",
    "Third-Party Services\nThis placeholder policy does not yet account for any third-party services, analytics, crash reporting, or advertising SDKs. If any such services are added to the app in the future, this policy must be updated accordingly before release.",
    "Children's Privacy\nMeal Planner is not directed at children, and this placeholder policy does not include any children's-privacy-specific language.",
    "Changes to This Policy\nBecause this is placeholder text, it is expected to change. Replace this entire document with reviewed, final copy appropriate to this app's actual data practices before publishing.",
    "Contact\n[PLACEHOLDER — insert a real contact email/address before release]"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val spacing = MealPlannerTokens.spacing

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.screenVertical)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.padding(bottom = spacing.lg)
                ) {
                    Text(
                        text = "This is placeholder policy text and has not been reviewed by legal counsel. " +
                            "Replace before shipping to production.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(spacing.cardPadding)
                    )
                }
            }
            items(PRIVACY_POLICY_PARAGRAPHS) { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = spacing.lg)
                )
            }
        }
    }
}
