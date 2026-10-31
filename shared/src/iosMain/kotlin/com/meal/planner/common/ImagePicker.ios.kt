package com.meal.planner.common

import androidx.compose.runtime.Composable

// Not implemented: this machine has no Xcode toolchain to build/verify a PhotosUI-based
// picker. Returns a no-op launcher until an iOS actual is implemented and tested.
@Composable
actual fun rememberImagePicker(onImagePicked: (String?) -> Unit): () -> Unit {
    return { onImagePicked(null) }
}
