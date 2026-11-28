package com.meal.planner.ingredients

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.Ingredient
import com.meal.domain.model.ingredientDisplayName
import com.meal.domain.model.normalizedIngredientName
import com.meal.planner.common.centeredMaxWidth
import com.meal.planner.common.rememberImagePicker
import com.meal.planner.components.ImageSourceDialog
import com.meal.planner.components.IngredientThumbnail
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.planner.theme.activeFilterChipColors
import com.meal.presentation.ingredients.IngredientSection
import com.meal.presentation.ingredients.IngredientsFilter
import com.meal.presentation.ingredients.IngredientsUiState
import com.meal.presentation.ingredients.IngredientsViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val ROW_THUMBNAIL_SIZE = 40.dp
private val DETAIL_THUMBNAIL_SIZE = 96.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun IngredientsScreen(
    onBack: () -> Unit,
    viewModel: IngredientsViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(strings.ingredientsScreen.title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                windowInsets = WindowInsets(0)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSheet = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(strings.ingredientsScreen.add) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .centeredMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::updateQuery,
                placeholder = { Text(strings.ingredientsScreen.searchPlaceholder) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = strings.cancel)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm)
            )
            FilterRow(selected = uiState.filter, onSelect = viewModel::setFilter)
            IngredientList(
                uiState = uiState,
                onToggleStaple = { viewModel.setStaple(it.id, !it.staple) },
                onClick = { selectedId = it.id }
            )
        }
    }

    if (showAddSheet) {
        AddIngredientsSheet(
            exists = uiState::exists,
            onSave = { names ->
                viewModel.addIngredients(names)
                showAddSheet = false
            },
            onDismiss = { showAddSheet = false }
        )
    }

    uiState.ingredients.firstOrNull { it.id == selectedId }?.let { ingredient ->
        IngredientDetailSheet(
            ingredient = ingredient,
            onImagePicked = { viewModel.changeImage(ingredient.id, it) },
            onResetImage = { viewModel.resetImage(ingredient.id) },
            onStapleChange = { viewModel.setStaple(ingredient.id, it) },
            onDismiss = { selectedId = null }
        )
    }
}

@Composable
private fun FilterRow(selected: IngredientsFilter, onSelect: (IngredientsFilter) -> Unit) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    Row(
        modifier = Modifier.padding(horizontal = spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        IngredientsFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelect(filter) },
                label = {
                    Text(
                        when (filter) {
                            IngredientsFilter.ALL -> strings.ingredientsScreen.filterAll
                            IngredientsFilter.STAPLES -> strings.shoppingAtHome
                            IngredientsFilter.CUSTOM -> strings.ingredientsScreen.mine
                        }
                    )
                },
                colors = activeFilterChipColors()
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun IngredientList(
    uiState: IngredientsUiState,
    onToggleStaple: (Ingredient) -> Unit,
    onClick: (Ingredient) -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    if (uiState.isLoading) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.padding(spacing.xxl))
        }
        return
    }
    val sections = uiState.sections
    if (sections.isEmpty()) {
        Text(
            text = strings.ingredientsScreen.noResults,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.xxl)
        )
        return
    }
    // Bottom padding keeps the last row clear of the floating action button.
    LazyColumn(contentPadding = PaddingValues(top = spacing.sm, bottom = 88.dp)) {
        sections.forEach { section ->
            if (!section.isSearchResult) {
                item(key = "header-${section.category}", contentType = "header") {
                    Text(
                        text = section.title(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(
                            start = spacing.screenHorizontal,
                            end = spacing.screenHorizontal,
                            top = spacing.lg,
                            bottom = spacing.xs
                        )
                    )
                }
            }
            items(section.ingredients, key = { it.id.toString() }, contentType = { "ingredient" }) { ingredient ->
                IngredientRow(
                    ingredient = ingredient,
                    onToggleStaple = { onToggleStaple(ingredient) },
                    onClick = { onClick(ingredient) }
                )
            }
        }
    }
}

@Composable
private fun IngredientSection.title(): String {
    val strings = LocalStrings.current
    return category?.let(strings.ingredientsScreen.categoryName) ?: strings.ingredientsScreen.mine
}

@Composable
private fun IngredientRow(ingredient: Ingredient, onToggleStaple: () -> Unit, onClick: () -> Unit) {
    val spacing = MealPlannerTokens.spacing
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = spacing.screenHorizontal, end = spacing.xs, top = spacing.xs, bottom = spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        IngredientThumbnail(
            name = ingredient.name,
            size = ROW_THUMBNAIL_SIZE,
            imageUri = ingredient.imageUri
        )
        Text(
            text = ingredientDisplayName(ingredient.name),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        StapleToggle(staple = ingredient.staple, onToggle = onToggleStaple)
    }
}

@Composable
private fun StapleToggle(staple: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (staple) Icons.Filled.Home else Icons.Outlined.Home,
            contentDescription = LocalStrings.current.ingredientsScreen.staple,
            tint = if (staple) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Collects several names before saving: each name becomes a chip on Enter, and names that are
 * already ingredients are refused with a message under the field.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddIngredientsSheet(
    exists: (String) -> Boolean,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val names = remember { mutableStateListOf<String>() }
    var text by remember { mutableStateOf("") }

    val trimmed = text.trim()
    val alreadyExists = trimmed.isNotEmpty() && exists(trimmed)
    val alreadyListed = names.any { normalizedIngredientName(it) == normalizedIngredientName(trimmed) }
    val canAddText = trimmed.isNotEmpty() && !alreadyExists && !alreadyListed
    val addText = {
        if (canAddText) {
            names += ingredientDisplayName(trimmed)
            text = ""
        }
    }
    val toSave = if (canAddText) names + ingredientDisplayName(trimmed) else names.toList()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = spacing.lg, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(strings.ingredientsScreen.add, style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(strings.ingredientsScreen.addFieldLabel) },
                supportingText = {
                    Text(if (alreadyExists) strings.ingredientsScreen.alreadyExists else strings.ingredientsScreen.addHint)
                },
                isError = alreadyExists,
                trailingIcon = {
                    IconButton(onClick = addText, enabled = canAddText) {
                        Icon(Icons.Default.Add, contentDescription = strings.add)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { addText() }),
                modifier = Modifier.fillMaxWidth()
            )
            if (names.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    names.forEach { name ->
                        InputChip(
                            selected = false,
                            onClick = { names.remove(name) },
                            label = { Text(name) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = strings.ingredientsScreen.removeFromListCd(name),
                                    modifier = Modifier.padding(spacing.xxs)
                                )
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.End)
            ) {
                TextButton(onClick = onDismiss) { Text(strings.cancel) }
                Button(onClick = { onSave(toSave) }, enabled = toSave.isNotEmpty()) {
                    Text(if (toSave.isEmpty()) strings.save else strings.ingredientsScreen.save(toSave.size))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientDetailSheet(
    ingredient: Ingredient,
    onImagePicked: (String) -> Unit,
    onResetImage: () -> Unit,
    onStapleChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    var showImageSourceDialog by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePicker(onImagePicked = { uri -> uri?.let(onImagePicked) })

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            IngredientThumbnail(
                name = ingredient.name,
                size = DETAIL_THUMBNAIL_SIZE,
                shape = MaterialTheme.shapes.medium,
                imageUri = ingredient.imageUri
            )
            Text(ingredientDisplayName(ingredient.name), style = MaterialTheme.typography.titleLarge)
            Text(
                text = ingredient.category?.let(strings.ingredientsScreen.categoryName) ?: strings.ingredientsScreen.mine,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                OutlinedButton(onClick = { showImageSourceDialog = true }) { Text(strings.changePhoto) }
                if (ingredient.imageUri != null) {
                    TextButton(onClick = onResetImage) { Text(strings.ingredientsScreen.defaultPicture) }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(strings.ingredientsScreen.staple, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        strings.ingredientsScreen.stapleHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = ingredient.staple, onCheckedChange = onStapleChange)
            }
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
