package com.meal.planner.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

private const val ABOUT_BODY_TEXT = "Meal Planner helps you plan weekly meals, organize recipes, build shopping " +
    "lists automatically from your meal plan, and keep track of what you spend on groceries.\n\n" +
    "Browse and save recipes, assign them to breakfast, lunch, dinner, or snack slots on your weekly calendar, " +
    "and let Meal Planner generate a shopping list for you. Mark recipes as favorites, track ingredient prices " +
    "over time, and review spending reports to see where your grocery budget goes.\n\n" +
    "Built with Kotlin Multiplatform and Jetpack Compose."

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val spacing = MealPlannerTokens.spacing

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("About Meal Planner", style = MaterialTheme.typography.titleLarge) },
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
            Text(text = "Meal Planner", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(spacing.xs))
            Text(
                text = "Version 1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(spacing.lg))
            Text(text = ABOUT_BODY_TEXT, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
