package com.meal.planner.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.meal.planner.common.rememberImagePicker
import com.meal.planner.components.ImageSourceDialog
import com.meal.planner.components.RecipeThumbnail
import com.meal.planner.strings.LocalStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileDialog(
    name: String,
    bio: String,
    avatarUrl: String,
    onConfirm: (name: String, bio: String, avatarUrl: String) -> Unit,
    onDismiss: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var nameText by remember { mutableStateOf(name) }
    var bioText by remember { mutableStateOf(bio) }
    var avatarUrlText by remember { mutableStateOf(avatarUrl) }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePicker(onImagePicked = { uri ->
        uri?.let { avatarUrlText = it }
    })

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(decorFitsSystemWindows = false),
        title = { Text(LocalStrings.current.profileEditProfile) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {
                RecipeThumbnail(
                    imageUrl = avatarUrlText,
                    fallbackIcon = Icons.Default.Person,
                    size = 72.dp,
                    shape = CircleShape
                )
                OutlinedButton(onClick = { showImageSourceDialog = true }) {
                    Text(if (avatarUrlText.isBlank()) LocalStrings.current.choosePhoto else LocalStrings.current.changePhoto)
                }
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text(LocalStrings.current.name) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bioText,
                    onValueChange = { bioText = it },
                    label = { Text(LocalStrings.current.editProfileBio) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (WindowInsets.isImeVisible) {
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = {
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = LocalStrings.current.hideKeyboard)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(nameText.trim(), bioText.trim(), avatarUrlText) }) {
                Text(LocalStrings.current.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
        }
    )

    if (showImageSourceDialog) {
        ImageSourceDialog(
            onTakePhoto = {
                showImageSourceDialog = false
                imagePicker.launchCamera()
            },
            onChooseFromGallery = {
                showImageSourceDialog = false
                imagePicker.launchGallery()
            },
            onDismiss = { showImageSourceDialog = false }
        )
    }
}
