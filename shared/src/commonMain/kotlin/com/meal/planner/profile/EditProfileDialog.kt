package com.meal.planner.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import com.meal.planner.common.rememberImagePicker
import com.meal.planner.components.RecipeThumbnail

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

    val launchImagePicker = rememberImagePicker(onImagePicked = { uri ->
        uri?.let { avatarUrlText = it }
    })

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit profile") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                RecipeThumbnail(
                    imageUrl = avatarUrlText,
                    fallbackIcon = Icons.Default.Person,
                    size = 72.dp,
                    shape = CircleShape
                )
                OutlinedButton(onClick = launchImagePicker) {
                    Text(if (avatarUrlText.isBlank()) "Choose Photo" else "Change Photo")
                }
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bioText,
                    onValueChange = { bioText = it },
                    label = { Text("Bio (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(nameText.trim(), bioText.trim(), avatarUrlText) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
