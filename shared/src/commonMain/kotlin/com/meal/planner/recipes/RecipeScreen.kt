package com.meal.planner.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.Recipe
import com.meal.presentation.recipes.RecipeViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel = koinViewModel(),
    onRecipeClick: (Uuid) -> Unit = {},
    onAddRecipeClick: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRecipeClick) {
                Text("+")
            }
        }
    ) {
        val uiState by viewModel.state.collectAsStateWithLifecycle()
        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.errorMessage != null -> Text(
                    text = uiState.errorMessage.orEmpty(),
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.showEmptyState -> Text(
                    text = "No recipes yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn {
                    items(uiState.recipes) { recipe ->
                        RecipeItem(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = recipe.name)
        recipe.description?.let { Text(text = it) }
    }
}
