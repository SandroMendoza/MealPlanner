package com.meal.domain.strings

import com.meal.domain.model.enums.Cuisine
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

private fun frenchCuisineName(cuisine: Cuisine): String = when (cuisine) {
    Cuisine.FILIPINO -> "Philippine"
    Cuisine.CHINESE -> "Chinoise"
    Cuisine.JAPANESE -> "Japonaise"
    Cuisine.KOREAN -> "Coréenne"
    Cuisine.THAI -> "Thaïlandaise"
    Cuisine.VIETNAMESE -> "Vietnamienne"
    Cuisine.INDIAN -> "Indienne"
    Cuisine.ITALIAN -> "Italienne"
    Cuisine.MEXICAN -> "Mexicaine"
    Cuisine.AMERICAN -> "Américaine"
    Cuisine.OTHER -> "Autre"
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
    takePhoto = "Prendre une photo",
    chooseFromGallery = "Choisir dans la galerie",
    category = "Catégorie",
    cuisine = "Cuisine",
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
    cuisineName = ::frenchCuisineName,
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
    homeRecipeOfTheDay = "Recette du jour",
    homeShuffleRecipeCd = "Proposer une autre recette",
    homeQuickActions = "Actions rapides",
    homeAddAMeal = "Ajouter un repas",
    homeShoppingList = "Liste de courses",
    homeReadAloudCd = "Lire le plan du jour à voix haute",
    homeStopReadAloudCd = "Arrêter la lecture du plan du jour",

    calendarTitle = "Plan de Repas",
    calendarAddMeal = "Ajouter un Repas",
    calendarNoMealTypePlanned = { mealType -> "Aucun $mealType prévu" },
    calendarAddMealTypeCd = { mealType -> "Ajouter $mealType" },
    calendarMealOptionsCd = "Options du repas",
    calendarViewRecipe = "Voir la recette",
    calendarChangeRecipe = "Changer de recette",
    calendarWeekNotes = "Notes",
    calendarWeekNotesPlaceholder = "Écrire une note pour cette semaine...",
    calendarWeekNumber = { weekNumber -> "Semaine $weekNumber" },

    recipePickerTitle = "Choisir une recette",
    recipePickerAddRecipeNameTo = { recipeName -> "Ajouter « $recipeName » à..." },
    recipePickerChange = "Changer",
    recipePickerCreateRecipeCd = "Créer une nouvelle recette",

    recipesAddCd = "Ajouter une recette",
    recipesFilters = "Filtres",
    recipesFiltersActiveCd = "Filtres (actifs)",
    recipesShowAsListCd = "Afficher en liste",
    recipesShowAsGridCd = "Afficher en grille",
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
    recipeDetailReadIngredientsCd = "Lire les ingrédients à voix haute",
    recipeDetailStopIngredientsCd = "Arrêter la lecture des ingrédients",
    recipeDetailCookMode = "Mode cuisine",
    recipeDetailCookModePlayCd = "Lire l'étape",
    recipeDetailCookModePauseCd = "Mettre l'étape en pause",
    recipeDetailCookModeNextCd = "Étape suivante",
    recipeDetailCookModePreviousCd = "Étape précédente",
    recipeDetailCookModeStopCd = "Quitter le mode cuisine",
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
    shoppingAddItemButton = "Ajouter un Article",
    shoppingRemoveItemTitle = "Supprimer l'article ?",
    shoppingRemoveItemBody = { itemName -> "Supprimer $itemName de la liste de courses ?" },
    recordPriceTitle = { itemName -> "Combien a coûté $itemName ?" },
    recordPricePriceLabel = { currencySymbol -> "Prix ($currencySymbol)" },
    shoppingUngrouped = "Sans groupe",
    shoppingAssignGroupTitle = "Déplacer vers un groupe",
    shoppingAssignGroupCd = "Attribuer à un groupe",
    shoppingNewGroupPlaceholder = "Nom du nouveau groupe",
    shoppingCreateGroup = "Créer",
    shoppingManageGroupsCd = "Gérer les groupes",
    shoppingReadAloudCd = "Lire la liste de courses à voix haute",
    shoppingStopReadAloudCd = "Arrêter la lecture de la liste de courses",
    shoppingManageGroupsTitle = "Gérer les groupes",
    shoppingDeleteGroupTitle = "Supprimer le groupe ?",
    shoppingDeleteGroupBody = { groupName -> "Les articles de \"$groupName\" reviendront dans Sans groupe." },

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
    settingsDefaultCalendarView = "Vue par défaut du plan de repas",
    settingsDefaultRecipeView = "Vue par défaut des recettes",
    settingsLocalization = "Localisation",
    settingsAbout = "À propos",
    settingsAboutMealPlanner = "À propos de Meal Planner",
    settingsPrivacyPolicy = "Politique de Confidentialité",
    settingsReadAloud = "Lire à voix haute",

    notificationSettingsTitle = "Paramètres de notification",
    notificationSettingsDescription = "Choisissez les repas pour lesquels vous souhaitez un rappel.",
    settingsNotificationsPermissionDenied = "Activez les notifications dans les paramètres système pour recevoir des rappels de repas.",
    settingsNotificationsOpenSettings = "Ouvrir les paramètres",
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

    privacyPolicyParagraphs = listOf(
        "Dernière mise à jour : 13 novembre 2026",
        "Meal Planner est une application de planification de repas qui garde vos données sur votre appareil. Elle n'a pas de compte utilisateur, pas de publicité, ni d'outil d'analyse ou de rapport de plantage. Le développeur ne collecte, ne reçoit et ne vend aucune donnée personnelle.",
        "Informations stockées sur votre appareil\nL'application stocke vos recettes, plans de repas, listes de courses, dépenses, profil (nom, bio et photo) et réglages dans son espace privé sur votre appareil. Ces données ne sont pas incluses dans les sauvegardes cloud d'Android ni dans les transferts d'un appareil à l'autre. Désinstaller l'application ou effacer ses données les supprime définitivement.",
        "Photos\nQuand vous ajoutez une photo de recette ou de profil, l'application utilise le sélecteur de photos d'Android et ne voit donc que les photos que vous choisissez. Ces photos restent sur votre appareil et ne sont jamais envoyées.",
        "Rappels de repas\nSi vous activez les rappels de repas, c'est votre appareil qui les planifie et les affiche. Rien les concernant n'est envoyé ailleurs.",
        "Partage\nQuand vous exportez un rapport de dépenses, l'application transmet le fichier à l'application que vous choisissez dans le menu de partage d'Android. La politique de confidentialité de cette application s'applique ensuite.",
        "Lecture à voix haute\nLa lecture à voix haute utilise le service de synthèse vocale installé sur votre appareil. Le texte lu est traité par ce service selon la politique de confidentialité de son fournisseur.",
        "Assistants vocaux\nSur les versions d'Android compatibles, les assistants installés sur votre appareil peuvent lire et modifier votre liste de courses, vos recettes, votre plan de repas et votre rapport de dépenses quand vous le leur demandez. L'assistant traite ces données selon sa propre politique de confidentialité.",
        "Enfants\nMeal Planner ne s'adresse pas aux enfants de moins de 13 ans et ne collecte sciemment de données auprès de personne.",
        "Modifications de cette politique\nSi les pratiques de l'application en matière de données changent, cette politique est mise à jour et la date ci-dessus aussi.",
        "Contact\nQuestions sur cette politique : sandragon.inc@gmail.com"
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
