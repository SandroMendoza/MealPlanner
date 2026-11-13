package com.meal.presentation.di

import com.meal.presentation.calendar.MealCalendarViewModel
import com.meal.presentation.home.HomeViewModel
import com.meal.presentation.notifications.NotificationSettingsViewModel
import com.meal.presentation.preferences.AppPreferencesViewModel
import com.meal.presentation.profile.ProfileViewModel
import com.meal.presentation.recipes.RecipeDetailViewModel
import com.meal.presentation.recipes.RecipeFormViewModel
import com.meal.presentation.recipes.RecipeViewModel
import com.meal.presentation.settings.SettingsViewModel
import com.meal.presentation.shopping.ShoppingListViewModel
import com.meal.presentation.spending.SpendingReportsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
val presentationModule = module {
    viewModel { RecipeViewModel(get(), get()) }
    viewModel { params -> RecipeDetailViewModel(params.get(), get(), get(), get(), get(), get()) }
    viewModel { params ->
        val recipeIdString = params.get<String>()
        RecipeFormViewModel(
            recipeId = recipeIdString.takeIf { it.isNotBlank() }?.let { Uuid.parse(it) },
            getRecipeByIdUseCase = get(),
            addRecipeUseCase = get(),
            updateRecipeUseCase = get(),
            getAllIngredientsUseCase = get(),
            getAllRecipesUseCase = get()
        )
    }
    viewModel { MealCalendarViewModel(get(), get(), get(), get(), get()) }
    viewModel { ShoppingListViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { SpendingReportsViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { AppPreferencesViewModel(get()) }
    viewModel { NotificationSettingsViewModel(get(), get()) }
}