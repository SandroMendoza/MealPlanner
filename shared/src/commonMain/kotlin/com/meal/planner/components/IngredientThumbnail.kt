package com.meal.planner.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.meal.domain.model.IngredientCatalog
import com.meal.planner.theme.MealPlannerTokens
import mealplanner.shared.generated.resources.Res
import mealplanner.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.imageResource

/**
 * Small picture of an ingredient: its tile from the ingredient image sheet when the ingredient
 * is in [IngredientCatalog] and its sheet is bundled, otherwise the name's first letter on a
 * tinted tile.
 */
@Composable
fun IngredientThumbnail(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = MealPlannerTokens.iconSize.md,
    shape: Shape = MaterialTheme.shapes.small,
    alpha: Float = 1f
) {
    val entry = remember(name) { IngredientCatalog.find(name) }
    val sheet = entry?.let { Res.allDrawableResources["ingredients_${it.sheet}"] }
    if (entry != null && sheet != null) {
        val atlas = imageResource(sheet)
        val painter = remember(atlas, entry.tile) {
            val tileSize = atlas.width / IngredientCatalog.SHEET_COLUMNS
            BitmapPainter(
                image = atlas,
                srcOffset = IntOffset(
                    x = entry.tile % IngredientCatalog.SHEET_COLUMNS * tileSize,
                    y = entry.tile / IngredientCatalog.SHEET_COLUMNS * tileSize
                ),
                srcSize = IntSize(tileSize, tileSize)
            )
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier
                .size(size)
                .alpha(alpha)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .alpha(alpha)
                .background(MaterialTheme.colorScheme.primaryContainer, shape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.trim().firstOrNull()?.uppercase().orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
