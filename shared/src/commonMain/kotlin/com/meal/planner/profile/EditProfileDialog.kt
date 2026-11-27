package com.meal.planner.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.meal.planner.common.rememberImagePicker
import com.meal.planner.components.ImageSourceDialog
import com.meal.planner.components.KeyboardDismissButton
import com.meal.planner.components.RecipeThumbnail
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens

// The width range Material 3 gives an alert dialog.
private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 560.dp

@Composable
fun EditProfileDialog(
    name: String,
    bio: String,
    avatarUrl: String,
    onConfirm: (name: String, bio: String, avatarUrl: String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameText by remember { mutableStateOf(name) }
    var bioText by remember { mutableStateOf(bio) }
    var avatarUrlText by remember { mutableStateOf(avatarUrl) }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePicker(onImagePicked = { uri ->
        uri?.let { avatarUrlText = it }
    })

    // A full-window dialog rather than AlertDialog: the dialog is its own window, so the app's
    // floating hide-keyboard button can't show over it, and that button needs a container as tall
    // as the window to sit just above the keyboard.
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        val spacing = MealPlannerTokens.spacing
        // Read inside the dialog, which has its own focus owner.
        val focusManager = LocalFocusManager.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().imePadding().padding(spacing.xxl),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = AlertDialogDefaults.shape,
                    color = AlertDialogDefaults.containerColor,
                    tonalElevation = AlertDialogDefaults.TonalElevation,
                    // Taps on the card itself must not reach the outside-tap handler behind it.
                    modifier = Modifier
                        .widthIn(min = DialogMinWidth, max = DialogMaxWidth)
                        .pointerInput(Unit) { detectTapGestures { } }
                ) {
                    Column(modifier = Modifier.padding(spacing.xxl)) {
                        Text(
                            text = LocalStrings.current.profileEditProfile,
                            style = MaterialTheme.typography.headlineSmall,
                            color = AlertDialogDefaults.titleContentColor,
                            modifier = Modifier.padding(bottom = spacing.lg)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                            modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())
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
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = spacing.xxl),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.End)
                        ) {
                            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
                            TextButton(onClick = { onConfirm(nameText.trim(), bioText.trim(), avatarUrlText) }) {
                                Text(LocalStrings.current.save)
                            }
                        }
                    }
                }
            }
            KeyboardDismissButton(modifier = Modifier.align(Alignment.BottomEnd))
        }
    }

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
