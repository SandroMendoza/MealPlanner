package com.meal.data.di

import com.meal.data.datasource.MealSlotLocalDataSource
import com.meal.data.datasource.RecipeLocalDataSource
import com.meal.data.datasource.ShoppingListLocalDataSource
import com.meal.data.db.DatabaseDriverFactory
import com.meal.data.db.MealPlannerDatabase
import com.meal.data.repository.MealPlanRepositoryImpl
import com.meal.data.repository.RecipeRepositoryImpl
import com.meal.data.repository.ShoppingListRepositoryImpl
import com.meal.domain.repository.MealPlanRepository
import com.meal.domain.repository.RecipeRepository
import com.meal.domain.repository.ShoppingListRepository
import org.koin.dsl.module

val dataModule = module {
    single { MealPlannerDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single { RecipeLocalDataSource(get()) }
    single { MealSlotLocalDataSource(get()) }
    single { ShoppingListLocalDataSource(get()) }

    single <RecipeRepository> {
        RecipeRepositoryImpl(
            get()
        )
    }
    single <MealPlanRepository> { MealPlanRepositoryImpl(get()) }
    single <ShoppingListRepository> { ShoppingListRepositoryImpl(get()) }
}
