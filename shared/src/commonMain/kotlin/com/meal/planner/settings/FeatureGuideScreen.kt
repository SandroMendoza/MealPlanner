package com.meal.planner.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meal.domain.strings.GuideSection
import com.meal.planner.common.centeredMaxWidth
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureGuideScreen(onBack: () -> Unit, onReplayWalkthrough: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(strings.featureGuide, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = spacing.screenHorizontal, vertical = spacing.screenVertical),
            verticalArrangement = Arrangement.spacedBy(spacing.sectionGap)
        ) {
            item {
                FilledTonalButton(
                    onClick = onReplayWalkthrough,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.centeredMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(MealPlannerTokens.iconSize.sm)
                    )
                    Text(strings.featureGuideReplayWalkthrough, modifier = Modifier.padding(start = spacing.sm))
                }
            }
            items(strings.featureGuideSections) { section ->
                GuideSectionContent(section, trySayingLabel = strings.featureGuideTrySaying)
            }
        }
    }
}

@Composable
private fun GuideSectionContent(section: GuideSection, trySayingLabel: String) {
    val spacing = MealPlannerTokens.spacing
    Column(
        modifier = Modifier.centeredMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        section.items.forEach { item ->
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text("•", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (section.examples.isNotEmpty()) {
            Text(
                text = trySayingLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = spacing.xs)
            )
            section.examples.forEach { example ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = example,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm)
                    )
                }
            }
        }
    }
}
