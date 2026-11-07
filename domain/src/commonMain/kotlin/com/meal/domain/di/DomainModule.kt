package com.meal.domain.di

import com.meal.domain.repository.IngredientRepository
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.MealReminderRepository
import com.meal.domain.repository.RecipeRepository
import com.meal.domain.repository.SettingsRepository
import com.meal.domain.repository.ShoppingListRepository
import com.meal.domain.repository.UserProfileRepository
import com.meal.domain.service.ReminderScheduler
import com.meal.domain.usecase.AddRecipeToSlotUseCase
import com.meal.domain.usecase.AddRecipeUseCase
import com.meal.domain.usecase.AddShoppingListItemUseCase
import com.meal.domain.usecase.DeleteRecipeUseCase
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetMealRemindersUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.GetSpendingReportUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.RecordItemPriceUseCase
import com.meal.domain.usecase.RemoveMealSlotUseCase
import com.meal.domain.usecase.RemoveShoppingListItemUseCase
import com.meal.domain.usecase.ReplaceRecipeInSlotUseCase
import com.meal.domain.usecase.ToggleFavoriteUseCase
import com.meal.domain.usecase.ToggleShoppingListItemUseCase
import com.meal.domain.usecase.UpdateRecipeUseCase
import com.meal.domain.usecase.GetSettingsUseCase
import com.meal.domain.usecase.GetUserProfileUseCase
import com.meal.domain.usecase.UpdateMealReminderUseCase
import com.meal.domain.usecase.UpdateSettingsUseCase
import com.meal.domain.usecase.UpdateUserProfileUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        GetAllRecipesUseCase(get<RecipeRepository>())
    }
    factory {
        GetRecipeByIdUseCase(get<RecipeRepository>())
    }
    factory {
        AddRecipeUseCase(get<RecipeRepository>())
    }
    factory {
        UpdateRecipeUseCase(get<RecipeRepository>())
    }
    factory {
        DeleteRecipeUseCase(get<RecipeRepository>(), get<MealPlanRepository>())
    }
    factory {
        GetWeeklyMealPlanUseCase(get<MealPlanRepository>(), get<RecipeRepository>())
    }
    factory {
        AddRecipeToSlotUseCase(get<MealPlanRepository>())
    }
    factory {
        ReplaceRecipeInSlotUseCase(get<MealPlanRepository>())
    }
    factory {
        RemoveMealSlotUseCase(get<MealPlanRepository>())
    }
    factory {
        GenerateShoppingListUseCase(get<GetWeeklyMealPlanUseCase>(), get<ShoppingListRepository>())
    }
    factory {
        ToggleShoppingListItemUseCase(get<ShoppingListRepository>())
    }
    factory {
        AddShoppingListItemUseCase(get<ShoppingListRepository>())
    }
    factory {
        RemoveShoppingListItemUseCase(get<ShoppingListRepository>())
    }
    factory {
        RecordItemPriceUseCase(get<ShoppingListRepository>())
    }
    factory {
        GetSpendingReportUseCase(get<ShoppingListRepository>())
    }
    factory {
        ToggleFavoriteUseCase(get<RecipeRepository>())
    }
    factory {
        GetAllIngredientsUseCase(get<IngredientRepository>())
    }
    factory {
        GetUserProfileUseCase(get<UserProfileRepository>())
    }
    factory {
        UpdateUserProfileUseCase(get<UserProfileRepository>())
    }
    factory {
        GetSettingsUseCase(get<SettingsRepository>())
    }
    factory {
        UpdateSettingsUseCase(get<SettingsRepository>())
    }
    factory {
        GetMealRemindersUseCase(get<MealReminderRepository>())
    }
    factory {
        UpdateMealReminderUseCase(get<MealReminderRepository>(), get<ReminderScheduler>())
    }
}
