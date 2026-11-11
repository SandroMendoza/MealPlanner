package com.meal.domain.strings

import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.datetime.DayOfWeek

private val FRENCH_WEEKDAY_SHORT = mapOf(
    DayOfWeek.MONDAY to "Lun", DayOfWeek.TUESDAY to "Mar", DayOfWeek.WEDNESDAY to "Mer",
    DayOfWeek.THURSDAY to "Jeu", DayOfWeek.FRIDAY to "Ven", DayOfWeek.SATURDAY to "Sam", DayOfWeek.SUNDAY to "Dim"
)

private val FRENCH_WEEKDAY_INITIAL = mapOf(
    DayOfWeek.MONDAY to "L", DayOfWeek.TUESDAY to "M", DayOfWeek.WEDNESDAY to "M",
    DayOfWeek.THURSDAY to "J", DayOfWeek.FRIDAY to "V", DayOfWeek.SATURDAY to "S", DayOfWeek.SUNDAY to "D"
)

private val FRENCH_MONTH_ABBREVIATIONS = listOf(
    "Janv", "Févr", "Mars", "Avr", "Mai", "Juin", "Juil", "Août", "Sept", "Oct", "Nov", "Déc"
)

private fun frenchMealTypeName(mealType: MealType): String = when (mealType) {
    MealType.BREAKFAST -> "Petit-déjeuner"
    MealType.LUNCH -> "Déjeuner"
    MealType.DINNER -> "Dîner"
    MealType.SNACK -> "Collation"
}

private fun frenchFoodCategoryName(category: FoodCategory): String = when (category) {
    FoodCategory.MAIN_DISH -> "Plat principal"
    FoodCategory.SIDE_DISH -> "Accompagnement"
    FoodCategory.SOUP -> "Soupe"
    FoodCategory.SALAD -> "Salade"
    FoodCategory.DESSERT -> "Dessert"
    FoodCategory.BEVERAGE -> "Boisson"
}

private fun frenchMeasurementUnitName(unit: MeasurementUnit): String {
    val name = when (unit) {
        MeasurementUnit.GRAMS -> "Grammes"
        MeasurementUnit.KILOGRAMS -> "Kilogrammes"
        MeasurementUnit.MILLILITER -> "Millilitre"
        MeasurementUnit.LITER -> "Litre"
        MeasurementUnit.TEASPOON -> "Cuillère à café"
        MeasurementUnit.TABLESPOON -> "Cuillère à soupe"
        MeasurementUnit.CUP -> "Tasse"
        MeasurementUnit.PIECE -> "Pièce"
    }
    return "$name (${unit.unit})"
}

val frenchStrings = Strings(
    back = "Retour",
    cancel = "Annuler",
    save = "Enregistrer",
    remove = "Supprimer",
    ok = "OK",
    add = "Ajouter",
    name = "Nom",
    qty = "Qté",
    unit = "Unité",
    languageLabel = "Langue",
    currencyLabel = "Devise",
    week = "Semaine",
    range = "Plage",
    day = "Jour",
    previousWeek = "Semaine précédente",
    nextWeek = "Semaine suivante",
    previousDay = "Jour précédent",
    nextDay = "Jour suivant",
    goToToday = "Aller à aujourd'hui",
    goToCurrentWeek = "Aller à la semaine actuelle",
    pickAWeek = "Choisir une semaine",
    pickADate = "Choisir une date",
    pickADateRange = "Choisir une plage de dates",
    searchRecipesPlaceholder = "Rechercher des recettes...",
    clearSearch = "Effacer la recherche",
    hideKeyboard = "Masquer le clavier",
    choosePhoto = "Choisir une photo",
    changePhoto = "Changer la photo",
    category = "Catégorie",
    ingredients = "Ingrédients",
    instructions = "Instructions",
    totalSpent = "Total dépensé",
    all = "Tout",
    settings = "Paramètres",
    favorites = "Favoris",
    myRecipes = "Mes recettes",
    spendingReports = "Rapports de dépenses",
    minutesLabel = { minutes -> "$minutes min" },
    weekdayShort = { dayOfWeek -> FRENCH_WEEKDAY_SHORT.getValue(dayOfWeek) },
    weekdayInitial = { dayOfWeek -> FRENCH_WEEKDAY_INITIAL.getValue(dayOfWeek) },
    monthAbbreviation = { month -> FRENCH_MONTH_ABBREVIATIONS[month - 1] },
    mealTypeName = ::frenchMealTypeName,
    foodCategoryName = ::frenchFoodCategoryName,
    measurementUnitName = ::frenchMeasurementUnitName,

    navHome = "Accueil",
    navMealPlan = "Plan de repas",
    navRecipes = "Recettes",
    navShopping = "Courses",
    navProfile = "Profil",

    homeGreetingDefault = "Bonjour !",
    homeGreetingWithName = { name -> "Bonjour, $name !" },
    homeSubtitle = "Planifions vos repas.",
    homeNotificationsCd = "Notifications",
    homeTodaysPlan = "Plan du jour",
    homeViewAll = "Tout voir",
    homeNoMealsPlannedToday = "Aucun repas prévu pour aujourd'hui.",
    homeQuickActions = "Actions rapides",
    homeAddAMeal = "Ajouter un repas",
    homeShoppingList = "Liste de courses",

    calendarTitle = "Plan de Repas",
    calendarAddMeal = "Ajouter un Repas",
    calendarNoMealTypePlanned = { mealType -> "Aucun $mealType prévu" },
    calendarAddMealTypeCd = { mealType -> "Ajouter $mealType" },
    calendarMealOptionsCd = "Options du repas",
    calendarViewRecipe = "Voir la recette",
    calendarChangeRecipe = "Changer de recette",

    recipePickerTitle = "Choisir une recette",
    recipePickerAddRecipeNameTo = { recipeName -> "Ajouter « $recipeName » à..." },
    recipePickerChange = "Changer",

    recipesAddCd = "Ajouter une recette",
    recipesFilters = "Filtres",
    recipesFiltersActiveCd = "Filtres (actifs)",
    recipesSnacks = "Collations",
    recipesFavoritesOnly = "Favoris uniquement",
    recipesCreatedByMe = "Créées par moi",
    recipesPrepTimeRange = { start, end -> "Temps de préparation : $start–$end min" },
    recipesCalorieRange = { start, end -> "Calories : $start–$end" },
    recipesClearFilters = "Effacer les filtres",
    recipesNoRecipesYet = "Pas encore de recettes.",
    recipesNoFavoritesYet = "Pas encore de favoris. Touchez le cœur d'une recette pour en ajouter une.",
    recipesNoCreatedYet = "Aucune recette créée pour l'instant. Touchez + pour ajouter la vôtre.",
    recipesNoMatchesFilters = "Aucune recette ne correspond à vos filtres.",

    recipeDetailDeleteTitle = "Supprimer la recette ?",
    recipeDetailDeleteBody = { count, dates ->
        "Cette recette est programmée pour $count date(s) future(s) : $dates. La supprimer l'enlèvera de ces jours."
    },
    recipeDetailDelete = "Supprimer",
    recipeDetailNotFound = "Recette introuvable.",
    recipeDetailEditCd = "Modifier la recette",
    recipeDetailDeleteCd = "Supprimer la recette",
    recipeDetailRemoveFavoriteCd = "Retirer des favoris",
    recipeDetailAddFavoriteCd = "Ajouter aux favoris",
    caloriesLabel = { count -> "$count calories" },
    servingsLabel = { count -> if (count == 1) "$count portion" else "$count portions" },
    recipeDetailAddToMealPlan = "Ajouter au Plan de Repas",

    recipeFormEditTitle = "Modifier la Recette",
    recipeFormNewTitle = "Nouvelle Recette",
    recipeFormNameExists = "Une recette portant ce nom existe déjà.",
    recipeFormDescription = "Description",
    recipeFormMealTypes = "Types de repas",
    recipeFormPrepMin = "Préparation (min)",
    recipeFormServings = "Portions",
    recipeFormCalories = "Calories",
    recipeFormPhoto = "Photo",
    recipeFormAddIngredient = "+ Ajouter un ingrédient",
    recipeFormAddInstruction = "+ Ajouter une instruction",
    recipeFormSaving = "Enregistrement...",
    recipeFormSaveRecipe = "Enregistrer la Recette",

    shoppingNoMealsPlannedPeriod = "Aucun repas prévu pour cette période.",
    shoppingAddPrice = "Ajouter un prix",
    shoppingAddItemButton = "Ajouter un Article",
    shoppingRemoveItemTitle = "Supprimer l'article ?",
    shoppingRemoveItemBody = { itemName -> "Supprimer $itemName de la liste de courses ?" },
    recordPriceTitle = { itemName -> "Combien a coûté $itemName ?" },
    recordPricePriceLabel = { currencySymbol -> "Prix ($currencySymbol)" },

    profileTitle = "Profil",
    profileEditProfile = "Modifier le profil",
    profileYourNamePlaceholder = "Votre nom",
    profileRecipesStat = "Recettes",
    profileCreatedStat = "Créées",
    profileThisWeekStat = "Cette Semaine",
    editProfileBio = "Bio (facultatif)",

    settingsPreferences = "Préférences",
    settingsNotifications = "Notifications",
    settingsDarkMode = "Mode sombre",
    settingsLockPastDates = "Verrouiller les dates passées",
    settingsLocalization = "Localisation",
    settingsAbout = "À propos",
    settingsAboutMealPlanner = "À propos de Meal Planner",
    settingsPrivacyPolicy = "Politique de Confidentialité",

    notificationSettingsTitle = "Paramètres de notification",
    notificationSettingsDescription = "Choisissez les repas pour lesquels vous souhaitez un rappel.",
    settingsNotificationsPermissionDenied = "Activez les notifications dans les paramètres système pour recevoir des rappels de repas.",
    settingsNotificationsOpenSettings = "Ouvrir les paramètres",
    settingsNotificationsExactAlarmHint = "Pour que les rappels arrivent à l'heure exacte, autorisez les alarmes précises.",
    settingsNotificationsEnableExactAlarms = "Autoriser les alarmes précises",
    mealReminderNotificationTitle = { mealType -> "Rappel : ${frenchMealTypeName(mealType).lowercase()}" },
    mealReminderNotificationBody = "Appuyez pour voir ce qui est prévu.",

    aboutAppName = "Meal Planner",
    aboutVersion = "Version 1.0",
    aboutBody = "Meal Planner vous aide à planifier vos repas de la semaine, organiser vos recettes, générer " +
        "automatiquement des listes de courses à partir de votre plan de repas, et suivre vos dépenses " +
        "alimentaires.\n\nParcourez et enregistrez des recettes, assignez-les aux créneaux petit-déjeuner, " +
        "déjeuner, dîner ou collation de votre calendrier hebdomadaire, et laissez Meal Planner générer une " +
        "liste de courses pour vous. Marquez des recettes comme favorites, suivez le prix des ingrédients " +
        "dans le temps, et consultez les rapports de dépenses pour voir où va votre budget courses.\n\n" +
        "Créé avec Kotlin Multiplatform et Jetpack Compose.",

    privacyDisclaimer = "Ceci est un texte de politique provisoire qui n'a pas été examiné par un conseiller " +
        "juridique. Remplacez-le avant la mise en production.",
    privacyPolicyParagraphs = listOf(
        "Dernière mise à jour : [DATE — À COMPLÉTER]",
        "Cette Politique de Confidentialité provisoire décrit, en termes généraux, les informations que Meal " +
            "Planner peut collecter et la façon dont elles peuvent être utilisées. Elle est fournie uniquement " +
            "comme point de départ et doit être revue et remplacée par une politique précise et validée " +
            "juridiquement avant que cette application ne soit distribuée à de vrais utilisateurs.",
        "Informations que nous stockons\nMeal Planner stocke vos recettes, plans de repas, listes de " +
            "courses, informations de profil (nom, bio, avatar) et préférences de l'application (notifications, " +
            "thème, langue, devise) localement sur votre appareil. À ce stade, aucun système de compte ni " +
            "serveur distant n'existe, et aucune des données mentionnées ci-dessus n'est transmise hors de " +
            "votre appareil par l'application elle-même.",
        "Images\nSi vous choisissez une photo de profil ou une image de recette depuis votre appareil, cette " +
            "image est référencée localement et n'est téléchargée nulle part par l'application.",
        "Services tiers\nCette politique provisoire ne tient pas encore compte d'éventuels services tiers, " +
            "outils d'analyse, rapports de plantage ou SDK publicitaires. Si de tels services sont ajoutés à " +
            "l'application à l'avenir, cette politique devra être mise à jour en conséquence avant toute " +
            "publication.",
        "Confidentialité des enfants\nMeal Planner ne s'adresse pas aux enfants, et cette politique " +
            "provisoire ne comprend pas de mentions spécifiques à la confidentialité des enfants.",
        "Modifications de cette politique\nComme il s'agit d'un texte provisoire, il est appelé à changer. " +
            "Remplacez l'intégralité de ce document par un texte final et validé, adapté aux pratiques réelles " +
            "de cette application en matière de données, avant sa publication.",
        "Contact\n[À COMPLÉTER — ajoutez une adresse e-mail ou postale de contact réelle avant la publication]"
    ),

    spendingExportCd = "Exporter le rapport en PDF",
    spendingNoPricedItems = "Aucun article avec un prix pour cette période.",
    spendingBreakdown = "Détail",
    spendingAvgPerPeriod = "Moy. / période",
    spendingPdfTitle = "Rapport de Dépenses",
    spendingPdfDateRange = { start, end -> "$start au $end" },
    spendingPdfTotalSpent = { amount -> "Total dépensé : $amount" },
    spendingShareChooserTitle = "Partager le rapport de dépenses"
)
