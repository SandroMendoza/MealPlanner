package com.meal.planner.common

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

@Composable
actual fun rememberImagePicker(onImagePicked: (String?) -> Unit): ImagePickerLaunchers {
    val context = LocalContext.current

    val pendingCaptureFile = remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> onImagePicked(uri?.let { copyToAppStorage(context, it) }) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingCaptureFile.value
        onImagePicked(if (success && file != null) Uri.fromFile(file).toString() else null)
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
        if (copied) Uri.fromFile(file).toString() else null
    } catch (e: Exception) {
        null
    }
}
