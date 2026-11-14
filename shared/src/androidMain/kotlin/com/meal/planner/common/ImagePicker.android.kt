package com.meal.planner.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

// Photos live in app storage and are included in Android backup, whose cloud quota is
// 25 MB per app; a backup over the quota is skipped entirely. Shrinking each photo keeps
// a large recipe collection well under it.
private const val MAX_PHOTO_DIMENSION = 1600
private const val PHOTO_JPEG_QUALITY = 85

@Composable
actual fun rememberImagePicker(onImagePicked: (String?) -> Unit): ImagePickerLaunchers {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pendingCaptureFile = remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) {
            onImagePicked(null)
        } else {
            scope.launch {
                onImagePicked(withContext(Dispatchers.IO) { copyToAppStorage(context, uri) })
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingCaptureFile.value
        if (success && file != null) {
            scope.launch {
                withContext(Dispatchers.IO) { shrinkPhoto(file) }
                onImagePicked(Uri.fromFile(file).toString())
            }
        } else {
            onImagePicked(null)
        }
    }

    return ImagePickerLaunchers(
        launchGallery = {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        launchCamera = {
            val file = createCaptureFile(context)
            pendingCaptureFile.value = file
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            cameraLauncher.launch(uri)
        }
    )
}

private fun createCaptureFile(context: Context): File {
    val dir = File(context.filesDir, "recipe_images").apply { mkdirs() }
    return File(dir, "${UUID.randomUUID()}.jpg")
}

private fun copyToAppStorage(context: Context, uri: Uri): String? {
    return try {
        val dir = File(context.filesDir, "recipe_images").apply { mkdirs() }
        val file = File(dir, "${UUID.randomUUID()}.jpg")
        val copied = context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
            true
        } ?: false
        if (copied) {
            shrinkPhoto(file)
            Uri.fromFile(file).toString()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Re-encodes [file] as a JPEG no larger than [MAX_PHOTO_DIMENSION] on its longest side,
 * baking in the EXIF rotation because re-encoding drops the EXIF data. Leaves the file
 * untouched when it can't be decoded.
 */
private fun shrinkPhoto(file: File) {
    try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        val longestSide = maxOf(bounds.outWidth, bounds.outHeight)
        if (longestSide <= 0) return

        var sampleSize = 1
        while (longestSide / (sampleSize * 2) >= MAX_PHOTO_DIMENSION) sampleSize *= 2
        val decoded = BitmapFactory.decodeFile(
            file.path,
            BitmapFactory.Options().apply { inSampleSize = sampleSize }
        ) ?: return

        val scale = MAX_PHOTO_DIMENSION.toFloat() / maxOf(decoded.width, decoded.height)
        val matrix = Matrix().apply {
            if (scale < 1f) postScale(scale, scale)
            postRotate(exifRotationDegrees(file).toFloat())
        }
        val output = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)

        val temp = File(file.parentFile, "${file.name}.tmp")
        temp.outputStream().use { output.compress(Bitmap.CompressFormat.JPEG, PHOTO_JPEG_QUALITY, it) }
        if (!temp.renameTo(file)) temp.delete()
    } catch (e: Exception) {
        // Keep the original, full-size photo.
    }
}

private fun exifRotationDegrees(file: File): Int =
    when (ExifInterface(file.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
