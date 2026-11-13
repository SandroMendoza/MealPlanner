package com.meal.planner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage

/**
 * Shows the recipe's photo when available, falling back to a tinted icon
 * (the look every screen used before images were wired up) when it isn't.
 */
@Composable
fun RecipeThumbnail(
    imageUrl: String?,
    fallbackIcon: ImageVector,
    size: Dp,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.small,
    modifier: Modifier = Modifier
) {
    if (imageUrl.isNullOrBlank()) {
        Box(
            modifier = modifier
                .size(size)
                .background(MaterialTheme.colorScheme.primaryContainer, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    } else {
        AsyncImage(
            model = recipeImageModel(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape)
        )
    }
}
