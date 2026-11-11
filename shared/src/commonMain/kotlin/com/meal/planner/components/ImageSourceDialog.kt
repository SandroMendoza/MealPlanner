package com.meal.planner.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens

@Composable
fun ImageSourceDialog(
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(LocalStrings.current.choosePhoto, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onTakePhoto)
                        .padding(vertical = spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(LocalStrings.current.takePhoto, style = MaterialTheme.typography.bodyLarge)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onChooseFromGallery)
                        .padding(vertical = spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(LocalStrings.current.chooseFromGallery, style = MaterialTheme.typography.bodyLarge)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(LocalStrings.current.cancel) }
        }
    )
}
