package com.meal.planner.components

import com.meal.domain.model.BUNDLED_IMAGE_PREFIX
import mealplanner.shared.generated.resources.Res

/**
 * Turns a stored recipe image reference into something Coil can load. Bundled photos resolve to
 * their URI under `composeResources/files/recipes/`; any other value is returned unchanged.
 */
fun recipeImageModel(imageUrl: String?): String? =
    if (imageUrl != null && imageUrl.startsWith(BUNDLED_IMAGE_PREFIX)) {
        Res.getUri("files/recipes/" + imageUrl.removePrefix(BUNDLED_IMAGE_PREFIX))
    } else {
        imageUrl
    }
