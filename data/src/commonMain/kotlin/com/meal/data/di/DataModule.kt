package com.meal.data.di

import com.meal.data.datasource.IngredientLocalDataSource
import com.meal.data.datasource.MealSlotLocalDataSource
import com.meal.data.datasource.RecipeLocalDataSource
import com.meal.data.datasource.SettingsLocalDataSource
import com.meal.data.datasource.ShoppingListLocalDataSource
import com.meal.data.datasource.UserProfileLocalDataSource
import com.meal.data.db.DatabaseDriverFactory
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.repository.IngredientRepositoryImpl
import com.meal.data.repository.MealPlanRepositoryImpl
import com.meal.data.repository.RecipeRepositoryImpl
import com.meal.data.repository.SettingsRepositoryImpl
import com.meal.data.repository.ShoppingListRepositoryImpl
import com.meal.data.repository.UserProfileRepositoryImpl
import com.meal.domain.repository.IngredientRepository
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.RecipeRepository
import com.meal.domain.repository.SettingsRepository
import com.meal.domain.repository.ShoppingListRepository
import com.meal.domain.repository.UserProfileRepository
import org.koin.dsl.module

val dataModule = module {
    single { MealPlannerDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single { RecipeLocalDataSource(get()) }
    single { MealSlotLocalDataSource(get()) }
    single { ShoppingListLocalDataSource(get()) }
    single { IngredientLocalDataSource(get()) }
    single { UserProfileLocalDataSource(get()) }
    single { SettingsLocalDataSource(get()) }

    single <RecipeRepository> {
        RecipeRepositoryImpl(
            get()
        )
    }
    single <MealPlanRepository> { MealPlanRepositoryImpl(get()) }
    single <ShoppingListRepository> { ShoppingListRepositoryImpl(get()) }
    single <IngredientRepository> { IngredientRepositoryImpl(get()) }
    single <UserProfileRepository> { UserProfileRepositoryImpl(get()) }
    single <SettingsRepository> { SettingsRepositoryImpl(get()) }
}
