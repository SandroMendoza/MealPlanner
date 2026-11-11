package com.meal.planner.navigation

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
import com.meal.planner.profile.ProfileScreen
import com.meal.planner.recipes.RecipeDetailScreen
import com.meal.planner.recipes.RecipeFormScreen
import com.meal.planner.recipes.RecipeScreen
import com.meal.planner.settings.AboutScreen
import com.meal.planner.settings.NotificationSettingsScreen
import com.meal.planner.settings.PrivacyPolicyScreen
import com.meal.planner.settings.SettingsScreen
import com.meal.planner.shopping.ShoppingListScreen
import com.meal.planner.spending.SpendingReportsScreen
import com.meal.planner.strings.LocalStrings
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi

private val TAB_ROUTES = listOf(Routes.HOME, Routes.CALENDAR, Routes.RECIPES, Routes.RECIPES_FAVORITES, Routes.RECIPES_CREATED, Routes.SHOPPING_LIST, Routes.PROFILE)

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
                        label = { Text(LocalStrings.current.navHome) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.CALENDAR,
                        onClick = { navController.navigateToTab(Routes.CALENDAR) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        label = { Text(LocalStrings.current.navMealPlan) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.RECIPES || currentRoute == Routes.RECIPES_FAVORITES || currentRoute == Routes.RECIPES_CREATED,
                        onClick = { navController.navigateToTab(Routes.RECIPES) },
                        icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                        label = { Text(LocalStrings.current.navRecipes) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.SHOPPING_LIST,
                        onClick = { navController.navigateToTab(Routes.SHOPPING_LIST) },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text(LocalStrings.current.navShopping) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.PROFILE,
                        onClick = { navController.navigateToTab(Routes.PROFILE) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(LocalStrings.current.navProfile) }
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
            composable(Routes.RECIPES_FAVORITES) {
                RecipeScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Routes.recipeDetail(recipeId.toString()))
                    },
                    onAddRecipeClick = { navController.navigate(Routes.RECIPE_NEW) },
                    initialFavoritesOnly = true
                )
            }
            composable(Routes.RECIPES_CREATED) {
                RecipeScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Routes.recipeDetail(recipeId.toString()))
                    },
                    onAddRecipeClick = { navController.navigate(Routes.RECIPE_NEW) },
                    initialCreatedOnly = true
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
                            Routes.calendarSlotPick(date.toString(), mealType?.name, replaceSlotId?.toString())
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
                val mealTypeArg = entry.arguments?.read { getStringOrNull("mealType") }.orEmpty()
                val mealType = mealTypeArg.takeIf { it != Routes.ANY_MEAL_TYPE }?.let { MealType.valueOf(it) }
                val replaceSlotId = entry.arguments?.read { getStringOrNull("replaceSlotId") }
                RecipePickerScreen(
                    date = date,
                    mealType = mealType,
                    replaceSlotId = replaceSlotId,
                    onBack = { navController.popBackStack() },
                    onAssigned = { navController.popBackStack() },
                    onCreateRecipeClick = { navController.navigate(Routes.RECIPE_NEW) }
                )
            }
            composable(Routes.SHOPPING_LIST) {
                ShoppingListScreen()
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onMyRecipesClick = { navController.navigateToTab(Routes.RECIPES_CREATED) },
                    onFavoritesClick = { navController.navigateToTab(Routes.RECIPES_FAVORITES) },
                    onSpendingReportsClick = { navController.navigate(Routes.SPENDING_REPORTS) },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) }
                )
            }
            composable(Routes.SPENDING_REPORTS) {
                SpendingReportsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onNotificationSettingsClick = { navController.navigate(Routes.NOTIFICATION_SETTINGS) },
                    onAboutClick = { navController.navigate(Routes.ABOUT) },
                    onPrivacyPolicyClick = { navController.navigate(Routes.PRIVACY_POLICY) }
                )
            }
            composable(Routes.NOTIFICATION_SETTINGS) {
                NotificationSettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(onBack = { navController.popBackStack() })
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
