package com.meal.domain.strings

import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.datetime.DayOfWeek

/**
 * Every translatable UI string in the app. One instance per supported [com.meal.domain.model.Language].
 * All fields must be supplied explicitly per language (no defaults) so the compiler forces completeness.
 */
data class Strings(
    // Common / shared across many screens
    val back: String,
    val cancel: String,
    val save: String,
    val remove: String,
    val ok: String,
    val add: String,
    val name: String,
    val qty: String,
    val unit: String,
    val languageLabel: String,
    val currencyLabel: String,
    val week: String,
    val range: String,
    val day: String,
    val previousWeek: String,
    val nextWeek: String,
    val previousDay: String,
    val nextDay: String,
    val goToToday: String,
    val goToCurrentWeek: String,
    val pickAWeek: String,
    val pickADate: String,
    val pickADateRange: String,
    val searchRecipesPlaceholder: String,
    val clearSearch: String,
    val hideKeyboard: String,
    val choosePhoto: String,
    val changePhoto: String,
    val takePhoto: String,
    val chooseFromGallery: String,
    val category: String,
    val cuisine: String,
    val ingredients: String,
    val instructions: String,
    val totalSpent: String,
    val all: String,
    val settings: String,
    val favorites: String,
    val myRecipes: String,
    val spendingReports: String,
    val minutesLabel: (Int) -> String,
    val weekdayShort: (DayOfWeek) -> String,
    val weekdayInitial: (DayOfWeek) -> String,
    val monthAbbreviation: (month: Int) -> String,
    val mealTypeName: (MealType) -> String,
    val foodCategoryName: (FoodCategory) -> String,
    val cuisineName: (Cuisine) -> String,
    val measurementUnitName: (MeasurementUnit) -> String,

    // Bottom navigation
    val navHome: String,
    val navMealPlan: String,
    val navRecipes: String,
    val navShopping: String,
    val navProfile: String,

    // Home
    val homeGreetingDefault: String,
    val homeGreetingWithName: (String) -> String,
    val homeSubtitle: String,
    val homeNotificationsCd: String,
    val homeTodaysPlan: String,
    val homeViewAll: String,
    val homeNoMealsPlannedToday: String,
    val homeRecipeOfTheDay: String,
    val homeShuffleRecipeCd: String,
    val homeQuickActions: String,
    val homeAddAMeal: String,
    val homeShoppingList: String,
    val homeReadAloudCd: String,
    val homeStopReadAloudCd: String,

    // Meal calendar
    val calendarTitle: String,
    val calendarAddMeal: String,
    val calendarNoMealTypePlanned: (String) -> String,
    val calendarAddMealTypeCd: (String) -> String,
    val calendarMealOptionsCd: String,
    val calendarViewRecipe: String,
    val calendarChangeRecipe: String,
    val calendarWeekNotes: String,
    val calendarWeekNotesPlaceholder: String,
    val calendarWeekNumber: (Int) -> String,

    // Recipe picker (from calendar)
    val recipePickerTitle: String,
    val recipePickerAddRecipeNameTo: (String) -> String,
    val recipePickerChange: String,
    val recipePickerCreateRecipeCd: String,

    // Recipes list
    val recipesAddCd: String,
    val recipesFilters: String,
    val recipesFiltersActiveCd: String,
    val recipesShowAsListCd: String,
    val recipesShowAsGridCd: String,
    val recipesSnacks: String,
    val recipesFavoritesOnly: String,
    val recipesCreatedByMe: String,
    val recipesPrepTimeRange: (start: Int, end: Int) -> String,
    val recipesCalorieRange: (start: Int, end: Int) -> String,
    val recipesClearFilters: String,
    val recipesNoRecipesYet: String,
    val recipesNoFavoritesYet: String,
    val recipesNoCreatedYet: String,
    val recipesNoMatchesFilters: String,

    // Recipe detail
    val recipeDetailDeleteTitle: String,
    val recipeDetailDeleteBody: (futureDateCount: Int, dates: String) -> String,
    val recipeDetailDelete: String,
    val recipeDetailNotFound: String,
    val recipeDetailEditCd: String,
    val recipeDetailDeleteCd: String,
    val recipeDetailRemoveFavoriteCd: String,
    val recipeDetailAddFavoriteCd: String,
    val caloriesLabel: (Int) -> String,
    val servingsLabel: (Int) -> String,
    val recipeDetailAddToMealPlan: String,
    val recipeDetailReadIngredientsCd: String,
    val recipeDetailStopIngredientsCd: String,
    val recipeDetailCookMode: String,
    val recipeDetailCookModePlayCd: String,
    val recipeDetailCookModePauseCd: String,
    val recipeDetailCookModeNextCd: String,
    val recipeDetailCookModePreviousCd: String,
    val recipeDetailCookModeStopCd: String,

    // Recipe form
    val recipeFormEditTitle: String,
    val recipeFormNewTitle: String,
    val recipeFormNameExists: String,
    val recipeFormDescription: String,
    val recipeFormMealTypes: String,
    val recipeFormPrepMin: String,
    val recipeFormServings: String,
    val recipeFormCalories: String,
    val recipeFormPhoto: String,
    val recipeFormAddIngredient: String,
    val recipeFormAddInstruction: String,
    val recipeFormSaving: String,
    val recipeFormSaveRecipe: String,

    // Shopping list
    val shoppingNoMealsPlannedPeriod: String,
    val shoppingAddItemButton: String,
    val shoppingRemoveItemTitle: String,
    val shoppingRemoveItemBody: (itemName: String) -> String,
    val recordPriceTitle: (itemName: String) -> String,
    val recordPricePriceLabel: (currencySymbol: String) -> String,
    val shoppingUngrouped: String,
    val shoppingAssignGroupTitle: String,
    val shoppingAssignGroupCd: String,
    val shoppingNewGroupPlaceholder: String,
    val shoppingCreateGroup: String,
    val shoppingManageGroupsCd: String,
    val shoppingManageGroupsTitle: String,
    val shoppingDeleteGroupTitle: String,
    val shoppingDeleteGroupBody: (groupName: String) -> String,
    val shoppingReadAloudCd: String,
    val shoppingStopReadAloudCd: String,

    // Profile
    val profileTitle: String,
    val profileEditProfile: String,
    val profileYourNamePlaceholder: String,
    val profileRecipesStat: String,
    val profileCreatedStat: String,
    val profileThisWeekStat: String,
    val editProfileBio: String,

    // Settings
    val settingsPreferences: String,
    val settingsNotifications: String,
    val settingsDarkMode: String,
    val settingsLockPastDates: String,
    val settingsDefaultCalendarView: String,
    val settingsDefaultRecipeView: String,
    val settingsLocalization: String,
    val settingsAbout: String,
    val settingsAboutMealPlanner: String,
    val settingsPrivacyPolicy: String,
    val settingsReadAloud: String,

    // Notification settings
    val notificationSettingsTitle: String,
    val notificationSettingsDescription: String,
    val settingsNotificationsPermissionDenied: String,
    val settingsNotificationsOpenSettings: String,
    val mealReminderNotificationTitle: (MealType) -> String,
    val mealReminderNotificationBody: String,

    // About screen
    val aboutAppName: String,
    val aboutVersion: String,
    val aboutBody: String,

    // Privacy policy screen
    val privacyPolicyParagraphs: List<String>,

    // Spending reports
    val spendingExportCd: String,
    val spendingNoPricedItems: String,
    val spendingBreakdown: String,
    val spendingAvgPerPeriod: String,
    val spendingPdfTitle: String,
    val spendingPdfDateRange: (start: String, end: String) -> String,
    val spendingPdfTotalSpent: (amount: String) -> String,
    val spendingShareChooserTitle: String
)
