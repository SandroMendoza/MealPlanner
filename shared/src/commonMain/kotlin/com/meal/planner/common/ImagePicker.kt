package com.meal.planner.common

import androidx.compose.runtime.Composable

/**
 * Returns a callback that launches the platform image picker. [onImagePicked] receives a
 * persistent URI string suitable for Coil, or null if the user cancelled or the pick failed.
 */
@Composable
expect fun rememberImagePicker(onImagePicked: (String?) -> Unit): () -> Unit
