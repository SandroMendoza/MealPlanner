package com.meal.planner.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.meal.domain.model.enums.MealType
import com.meal.planner.calendar.MealCalendarScreen
import com.meal.planner.calendar.RecipePickerScreen
import com.meal.planner.home.HomeScreen
import com.meal.planner.recipes.RecipeDetailScreen
import com.meal.planner.recipes.RecipeFormScreen
import com.meal.planner.recipes.RecipeScreen
import com.meal.planner.shopping.ShoppingListScreen
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi

private val TAB_ROUTES = listOf(Routes.HOME, Routes.CALENDAR, Routes.RECIPES, Routes.SHOPPING_LIST, Routes.PROFILE)

@OptIn(ExperimentalUuidApi::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in TAB_ROUTES) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = { navController.navigateToTab(Routes.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.CALENDAR,
                        onClick = { navController.navigateToTab(Routes.CALENDAR) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        label = { Text("Meal plan") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.RECIPES,
                        onClick = { navController.navigateToTab(Routes.RECIPES) },
                        icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                        label = { Text("Recipes") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.SHOPPING_LIST,
                        onClick = { navController.navigateToTab(Routes.SHOPPING_LIST) },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text("Shopping") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.PROFILE,
                        onClick = { navController.navigateToTab(Routes.PROFILE) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onViewAllClick = { navController.navigateToTab(Routes.CALENDAR) },
                    onAddMealClick = { navController.navigateToTab(Routes.CALENDAR) },
                    onShoppingListClick = { navController.navigateToTab(Routes.SHOPPING_LIST) },
                    onMealClick = { recipeId -> navController.navigate(Routes.recipeDetail(recipeId.toString())) }
                )
            }
            composable(Routes.RECIPES) {
                RecipeScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Routes.recipeDetail(recipeId.toString()))
                    },
                    onAddRecipeClick = { navController.navigate(Routes.RECIPE_NEW) }
                )
            }
            composable(Routes.RECIPE_NEW) {
                RecipeFormScreen(
                    recipeId = null,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.RECIPE_DETAIL_PATTERN,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { entry ->
                val recipeId = entry.arguments?.read { getStringOrNull("recipeId") }.orEmpty()
                RecipeDetailScreen(
                    recipeId = recipeId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Routes.recipeEdit(id)) },
                    onAddToMealPlan = { navController.navigateToTab(Routes.CALENDAR) }
                )
            }
            composable(
                route = Routes.RECIPE_EDIT_PATTERN,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { entry ->
                val recipeId = entry.arguments?.read { getStringOrNull("recipeId") }.orEmpty()
                RecipeFormScreen(
                    recipeId = recipeId,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(Routes.CALENDAR) {
                MealCalendarScreen(
                    onSlotClick = { date, mealType, replaceSlotId ->
                        navController.navigate(
                            Routes.calendarSlotPick(date.toString(), mealType.name, replaceSlotId?.toString())
                        )
                    },
                    onRecipeClick = { recipeId -> navController.navigate(Routes.recipeDetail(recipeId.toString())) }
                )
            }
            composable(
                route = Routes.CALENDAR_SLOT_PICK_PATTERN,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType },
                    navArgument("mealType") { type = NavType.StringType },
                    navArgument("replaceSlotId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                val date = LocalDate.parse(entry.arguments?.read { getStringOrNull("date") }.orEmpty())
                val mealType = MealType.valueOf(entry.arguments?.read { getStringOrNull("mealType") }.orEmpty())
                val replaceSlotId = entry.arguments?.read { getStringOrNull("replaceSlotId") }
                RecipePickerScreen(
                    date = date,
                    mealType = mealType,
                    replaceSlotId = replaceSlotId,
                    onBack = { navController.popBackStack() },
                    onAssigned = { navController.popBackStack() }
                )
            }
            composable(Routes.SHOPPING_LIST) {
                ShoppingListScreen()
            }
            composable(Routes.PROFILE) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profile coming soon")
                }
            }
        }
    }
}

private fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
