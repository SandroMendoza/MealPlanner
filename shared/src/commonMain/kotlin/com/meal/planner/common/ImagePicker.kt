package com.meal.planner.common

import androidx.compose.runtime.Composable

/**
 * Launchers for picking an image either from the device gallery or by capturing a new
 * photo with the camera. Both report a persistent URI string suitable for Coil through
 * the shared [onImagePicked] callback, or null if the user cancelled or the action failed.
 */
class ImagePickerLaunchers(
    val launchGallery: () -> Unit,
    val launchCamera: () -> Unit
)

@Composable
expect fun rememberImagePicker(onImagePicked: (String?) -> Unit): ImagePickerLaunchers
