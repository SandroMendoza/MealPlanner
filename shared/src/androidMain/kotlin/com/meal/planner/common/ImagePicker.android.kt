package com.meal.planner.common

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

@Composable
actual fun rememberImagePicker(onImagePicked: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> onImagePicked(uri?.let { copyToAppStorage(context, it) }) }

    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
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
