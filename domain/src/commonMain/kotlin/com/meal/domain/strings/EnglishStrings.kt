package com.meal.domain.strings

import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.datetime.DayOfWeek

private val ENGLISH_WEEKDAY_SHORT = mapOf(
    DayOfWeek.MONDAY to "Mon", DayOfWeek.TUESDAY to "Tue", DayOfWeek.WEDNESDAY to "Wed",
    DayOfWeek.THURSDAY to "Thu", DayOfWeek.FRIDAY to "Fri", DayOfWeek.SATURDAY to "Sat", DayOfWeek.SUNDAY to "Sun"
)

private val ENGLISH_WEEKDAY_INITIAL = mapOf(
    DayOfWeek.MONDAY to "M", DayOfWeek.TUESDAY to "T", DayOfWeek.WEDNESDAY to "W",
    DayOfWeek.THURSDAY to "T", DayOfWeek.FRIDAY to "F", DayOfWeek.SATURDAY to "S", DayOfWeek.SUNDAY to "S"
)

private val ENGLISH_MONTH_ABBREVIATIONS = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

private fun englishMealTypeName(mealType: MealType): String = when (mealType) {
    MealType.BREAKFAST -> "Breakfast"
    MealType.LUNCH -> "Lunch"
    MealType.DINNER -> "Dinner"
    MealType.SNACK -> "Snack"
}

private fun englishFoodCategoryName(category: FoodCategory): String = when (category) {
    FoodCategory.MAIN_DISH -> "Main Dish"
    FoodCategory.SIDE_DISH -> "Side Dish"
    FoodCategory.SOUP -> "Soup"
    FoodCategory.SALAD -> "Salad"
    FoodCategory.DESSERT -> "Dessert"
    FoodCategory.BEVERAGE -> "Beverage"
}

private fun englishMeasurementUnitName(unit: MeasurementUnit): String {
    val name = when (unit) {
        MeasurementUnit.GRAMS -> "Grams"
        MeasurementUnit.KILOGRAMS -> "Kilograms"
        MeasurementUnit.MILLILITER -> "Milliliter"
        MeasurementUnit.LITER -> "Liter"
        MeasurementUnit.TEASPOON -> "Teaspoon"
        MeasurementUnit.TABLESPOON -> "Tablespoon"
        MeasurementUnit.CUP -> "Cup"
        MeasurementUnit.PIECE -> "Piece"
    }
    return "$name (${unit.unit})"
}

val englishStrings = Strings(
    back = "Back",
    cancel = "Cancel",
    save = "Save",
    remove = "Remove",
    ok = "OK",
    add = "Add",
    name = "Name",
    qty = "Qty",
    unit = "Unit",
    languageLabel = "Language",
    currencyLabel = "Currency",
    week = "Week",
    range = "Range",
    day = "Day",
    previousWeek = "Previous week",
    nextWeek = "Next week",
    previousDay = "Previous day",
    nextDay = "Next day",
    goToToday = "Go to today",
    goToCurrentWeek = "Go to current week",
    pickAWeek = "Pick a week",
    pickADate = "Pick a date",
    pickADateRange = "Pick a date range",
    searchRecipesPlaceholder = "Search recipes...",
    clearSearch = "Clear search",
    hideKeyboard = "Hide keyboard",
    choosePhoto = "Choose Photo",
    changePhoto = "Change Photo",
    takePhoto = "Take Photo",
    chooseFromGallery = "Choose from Gallery",
    category = "Category",
    ingredients = "Ingredients",
    instructions = "Instructions",
    totalSpent = "Total spent",
    all = "All",
    settings = "Settings",
    favorites = "Favorites",
    myRecipes = "My Recipes",
    spendingReports = "Spending Reports",
    minutesLabel = { minutes -> "$minutes min" },
    weekdayShort = { dayOfWeek -> ENGLISH_WEEKDAY_SHORT.getValue(dayOfWeek) },
    weekdayInitial = { dayOfWeek -> ENGLISH_WEEKDAY_INITIAL.getValue(dayOfWeek) },
    monthAbbreviation = { month -> ENGLISH_MONTH_ABBREVIATIONS[month - 1] },
    mealTypeName = ::englishMealTypeName,
    foodCategoryName = ::englishFoodCategoryName,
    measurementUnitName = ::englishMeasurementUnitName,

    navHome = "Home",
    navMealPlan = "Meal plan",
    navRecipes = "Recipes",
    navShopping = "Shopping",
    navProfile = "Profile",

    homeGreetingDefault = "Good morning!",
    homeGreetingWithName = { name -> "Good morning, $name" },
    homeSubtitle = "Let's plan your meals.",
    homeNotificationsCd = "Notifications",
    homeTodaysPlan = "Today's plan",
    homeViewAll = "View all",
    homeNoMealsPlannedToday = "No meals planned for today.",
    homeQuickActions = "Quick actions",
    homeAddAMeal = "Add a meal",
    homeShoppingList = "Shopping list",
    homeReadAloudCd = "Read today's plan aloud",
    homeStopReadAloudCd = "Stop reading today's plan",

    calendarTitle = "Meal Plan",
    calendarAddMeal = "Add Meal",
    calendarNoMealTypePlanned = { mealType -> "No $mealType planned" },
    calendarAddMealTypeCd = { mealType -> "Add $mealType" },
    calendarMealOptionsCd = "Meal options",
    calendarViewRecipe = "View recipe",
    calendarChangeRecipe = "Change recipe",
    calendarWeekNotes = "Notes",
    calendarWeekNotesPlaceholder = "Write a note for this week...",
    calendarWeekNumber = { weekNumber -> "Week $weekNumber" },

    recipePickerTitle = "Pick a recipe",
    recipePickerAddRecipeNameTo = { recipeName -> "Add \"$recipeName\" to..." },
    recipePickerChange = "Change",
    recipePickerCreateRecipeCd = "Create new recipe",

    recipesAddCd = "Add recipe",
    recipesFilters = "Filters",
    recipesFiltersActiveCd = "Filters (active)",
    recipesSnacks = "Snacks",
    recipesFavoritesOnly = "Favorites only",
    recipesCreatedByMe = "Created by me",
    recipesPrepTimeRange = { start, end -> "Prep time: $start–$end min" },
    recipesCalorieRange = { start, end -> "Calories: $start–$end" },
    recipesClearFilters = "Clear filters",
    recipesNoRecipesYet = "No recipes yet.",
    recipesNoFavoritesYet = "No favorites yet. Tap the heart on a recipe to add one.",
    recipesNoCreatedYet = "No recipes created yet. Tap + to add your own.",
    recipesNoMatchesFilters = "No recipes match your filters.",

    recipeDetailDeleteTitle = "Delete recipe?",
    recipeDetailDeleteBody = { count, dates ->
        "This recipe is scheduled on $count future date(s): $dates. Deleting it will remove it from those days."
    },
    recipeDetailDelete = "Delete",
    recipeDetailNotFound = "Recipe not found.",
    recipeDetailEditCd = "Edit recipe",
    recipeDetailDeleteCd = "Delete recipe",
    recipeDetailRemoveFavoriteCd = "Remove from favorites",
    recipeDetailAddFavoriteCd = "Add to favorites",
    recipeDetailReadIngredientsCd = "Read ingredients aloud",
    recipeDetailStopIngredientsCd = "Stop reading ingredients",
    recipeDetailCookMode = "Cook mode",
    recipeDetailCookModePlayCd = "Play step",
    recipeDetailCookModePauseCd = "Pause step",
    recipeDetailCookModeNextCd = "Next step",
    recipeDetailCookModePreviousCd = "Previous step",
    recipeDetailCookModeStopCd = "Exit cook mode",
    caloriesLabel = { count -> "$count calories" },
    servingsLabel = { count -> if (count == 1) "$count serving" else "$count servings" },
    recipeDetailAddToMealPlan = "Add to Meal Plan",

    recipeFormEditTitle = "Edit Recipe",
    recipeFormNewTitle = "New Recipe",
    recipeFormNameExists = "A recipe with this name already exists.",
    recipeFormDescription = "Description",
    recipeFormMealTypes = "Meal types",
    recipeFormPrepMin = "Prep (min)",
    recipeFormServings = "Servings",
    recipeFormCalories = "Calories",
    recipeFormPhoto = "Photo",
    recipeFormAddIngredient = "+ Add ingredient",
    recipeFormAddInstruction = "+ Add instruction",
    recipeFormSaving = "Saving...",
    recipeFormSaveRecipe = "Save Recipe",

    shoppingNoMealsPlannedPeriod = "No meals planned for this period.",
    shoppingAddItemButton = "Add Item",
    shoppingRemoveItemTitle = "Remove item?",
    shoppingRemoveItemBody = { itemName -> "Remove $itemName from the shopping list?" },
    recordPriceTitle = { itemName -> "How much did $itemName cost?" },
    recordPricePriceLabel = { currencySymbol -> "Price ($currencySymbol)" },
    shoppingUngrouped = "Ungrouped",
    shoppingAssignGroupTitle = "Move to group",
    shoppingAssignGroupCd = "Assign to a group",
    shoppingNewGroupPlaceholder = "New group name",
    shoppingCreateGroup = "Create",
    shoppingManageGroupsCd = "Manage groups",
    shoppingReadAloudCd = "Read shopping list aloud",
    shoppingStopReadAloudCd = "Stop reading shopping list",
    shoppingManageGroupsTitle = "Manage groups",
    shoppingDeleteGroupTitle = "Delete group?",
    shoppingDeleteGroupBody = { groupName -> "Items in \"$groupName\" will move back to Ungrouped." },

    profileTitle = "Profile",
    profileEditProfile = "Edit profile",
    profileYourNamePlaceholder = "Your name",
    profileRecipesStat = "Recipes",
    profileCreatedStat = "Created",
    profileThisWeekStat = "This Week",
    editProfileBio = "Bio (optional)",

    settingsPreferences = "Preferences",
    settingsNotifications = "Notifications",
    settingsDarkMode = "Dark mode",
    settingsLockPastDates = "Lock past dates",
    settingsDefaultCalendarView = "Default meal plan view",
    settingsLocalization = "Localization",
    settingsAbout = "About",
    settingsAboutMealPlanner = "About Meal Planner",
    settingsPrivacyPolicy = "Privacy Policy",
    settingsReadAloud = "Read aloud",

    notificationSettingsTitle = "Notification Settings",
    notificationSettingsDescription = "Choose which meals you want to be reminded about.",
    settingsNotificationsPermissionDenied = "Enable notifications in system settings to receive meal reminders.",
    settingsNotificationsOpenSettings = "Open settings",
    mealReminderNotificationTitle = { mealType -> "${englishMealTypeName(mealType)} reminder" },
    mealReminderNotificationBody = "Tap to see what's planned.",

    aboutAppName = "Meal Planner",
    aboutVersion = "Version 1.0",
    aboutBody = "Meal Planner helps you plan weekly meals, organize recipes, build shopping " +
        "lists automatically from your meal plan, and keep track of what you spend on groceries.\n\n" +
        "Browse and save recipes, assign them to breakfast, lunch, dinner, or snack slots on your weekly calendar, " +
        "and let Meal Planner generate a shopping list for you. Mark recipes as favorites, track ingredient prices " +
        "over time, and review spending reports to see where your grocery budget goes.\n\n" +
        "Built with Kotlin Multiplatform and Jetpack Compose.",

    privacyPolicyParagraphs = listOf(
        "Last updated: 13 November 2026",
        "Meal Planner is a meal planning app that keeps your data on your device. It has no user accounts, no ads, and no analytics or crash reporting. The developer does not collect, receive or sell any personal data.",
        "Information stored on your device\nThe app stores your recipes, meal plans, shopping lists, spending entries, profile (name, bio and photo) and settings in its private storage on your device. This data is not included in Android cloud backups or device-to-device transfers. Uninstalling the app or clearing its data deletes it permanently.",
        "Photos\nWhen you add a recipe or profile photo, the app uses the Android photo picker, so it only sees the photos you select. Those photos stay on your device and are never uploaded.",
        "Meal reminders\nIf you turn on meal reminders, your device schedules and shows them. Nothing about them is sent anywhere.",
        "Sharing\nWhen you export a spending report, the app passes the file to the app you choose in the Android share menu. That app's own privacy policy covers what happens next.",
        "Read aloud\nThe read-aloud feature uses the text-to-speech service installed on your device. The text being read is handled by that service under its provider's privacy policy.",
        "Voice assistants\nOn supported Android versions, assistant apps on your device can read and update your shopping list, recipes, meal plan and spending report when you ask them to. The assistant handles that data under its own privacy policy.",
        "Children\nMeal Planner is not directed at children under 13 and does not knowingly collect data from anyone.",
        "Changes to this policy\nIf the app's data practices change, this policy is updated and the date above changes with it.",
        "Contact\nQuestions about this policy: sandragon.inc@gmail.com"
    ),

    spendingExportCd = "Export report as PDF",
    spendingNoPricedItems = "No priced items in this period.",
    spendingBreakdown = "Breakdown",
    spendingAvgPerPeriod = "Avg / period",
    spendingPdfTitle = "Spending Report",
    spendingPdfDateRange = { start, end -> "$start to $end" },
    spendingPdfTotalSpent = { amount -> "Total spent: $amount" },
    spendingShareChooserTitle = "Share spending report"
)
