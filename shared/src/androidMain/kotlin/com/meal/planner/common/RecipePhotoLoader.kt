package com.meal.planner.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.meal.domain.model.BUNDLED_IMAGE_PREFIX
import mealplanner.shared.generated.resources.Res

/**
 * Decodes a recipe photo, bundled or saved on the device, scaled to [widthPx] wide, for places
 * outside Compose such as notifications and home-screen widgets. Returns null when the photo
 * can't be read.
 */
suspend fun loadRecipePhoto(context: Context, imageUrl: String, widthPx: Int): Bitmap? = runCatching {
    val bitmap = if (imageUrl.startsWith(BUNDLED_IMAGE_PREFIX)) {
        val bytes = Res.readBytes("files/recipes/" + imageUrl.removePrefix(BUNDLED_IMAGE_PREFIX))
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } else {
        context.contentResolver.openInputStream(Uri.parse(imageUrl))?.use { BitmapFactory.decodeStream(it) }
    } ?: return@runCatching null
    Bitmap.createScaledBitmap(bitmap, widthPx, widthPx * bitmap.height / bitmap.width, true)
}.getOrNull()
