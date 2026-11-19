package com.meal.planner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.meal.planner.theme.MealPlannerTokens

/**
 * Small picture of an ingredient, with the name's first letter on a tinted tile underneath.
 * Ingredients without a bundled picture show only the letter.
 */
@Composable
fun IngredientThumbnail(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = MealPlannerTokens.iconSize.md,
    shape: Shape = MaterialTheme.shapes.small,
    alpha: Float = 1f
) {
    Box(
        modifier = modifier
            .size(size)
            .alpha(alpha)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().firstOrNull()?.uppercase().orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        val imageModel = remember(name) { ingredientImageModel(name) }
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
