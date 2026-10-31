package com.meal.domain.di

import com.meal.domain.repository.IngredientRepository
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.RecipeRepository
import com.meal.domain.repository.ShoppingListRepository
import com.meal.domain.usecase.AddRecipeToSlotUseCase
import com.meal.domain.usecase.AddRecipeUseCase
import com.meal.domain.usecase.DeleteRecipeUseCase
import com.meal.domain.usecase.GenerateShoppingListUseCase
import com.meal.domain.usecase.GetAllIngredientsUseCase
import com.meal.domain.usecase.GetAllRecipesUseCase
import com.meal.domain.usecase.GetRecipeByIdUseCase
import com.meal.domain.usecase.GetWeeklyMealPlanUseCase
import com.meal.domain.usecase.RemoveMealSlotUseCase
import com.meal.domain.usecase.ReplaceRecipeInSlotUseCase
import com.meal.domain.usecase.ToggleFavoriteUseCase
import com.meal.domain.usecase.ToggleShoppingListItemUseCase
import com.meal.domain.usecase.UpdateRecipeUseCase
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
        ToggleFavoriteUseCase(get<RecipeRepository>())
    }
    factory {
        GetAllIngredientsUseCase(get<IngredientRepository>())
    }
}
