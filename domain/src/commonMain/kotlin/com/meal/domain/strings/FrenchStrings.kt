package com.meal.domain.strings

import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.DayPeriod
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

private fun frenchGreeting(period: DayPeriod): String = when (period) {
    DayPeriod.MORNING, DayPeriod.AFTERNOON -> "Bonjour"
    DayPeriod.EVENING -> "Bonsoir"
}

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
        MeasurementUnit.OUNCE -> "Once"
        MeasurementUnit.POUND -> "Livre"
        MeasurementUnit.FLUID_OUNCE -> "Once liquide"
        MeasurementUnit.PINT -> "Pinte"
        MeasurementUnit.QUART -> "Quart"
        MeasurementUnit.GALLON -> "Gallon"
        MeasurementUnit.PINCH -> "Pincée"
        MeasurementUnit.DASH -> "Trait"
        MeasurementUnit.DROP -> "Goutte"
        MeasurementUnit.HANDFUL -> "Poignée"
        MeasurementUnit.DOZEN -> "Douzaine"
        MeasurementUnit.SLICE -> "Tranche"
        MeasurementUnit.CUBE -> "Cube"
        MeasurementUnit.STICK -> "Bâton"
        MeasurementUnit.FILLET -> "Filet"
        MeasurementUnit.CLOVE -> "Gousse"
        MeasurementUnit.HEAD -> "Tête"
        MeasurementUnit.BUNCH -> "Botte"
        MeasurementUnit.STALK -> "Tige"
        MeasurementUnit.SPRIG -> "Brin"
        MeasurementUnit.LEAF -> "Feuille"
        MeasurementUnit.PACK -> "Paquet"
        MeasurementUnit.SACHET -> "Sachet"
        MeasurementUnit.CAN -> "Boîte de conserve"
        MeasurementUnit.BOTTLE -> "Bouteille"
        MeasurementUnit.JAR -> "Bocal"
        MeasurementUnit.BOX -> "Boîte"
        MeasurementUnit.BAG -> "Sac"
        MeasurementUnit.CARTON -> "Brique"
        MeasurementUnit.TRAY -> "Plateau"
        MeasurementUnit.LOAF -> "Pain"
        MeasurementUnit.CENTIMETER -> "Centimètre"
        MeasurementUnit.INCH -> "Pouce"
        MeasurementUnit.TO_TASTE -> "Au goût"
    }
    return if (unit == MeasurementUnit.TO_TASTE || name.equals(unit.unit, ignoreCase = true)) name else "$name (${unit.unit})"
}

/** French puts a unit in the plural from 2 upwards ("1,5 tasse", "2 tasses"); metric symbols never change. */
private fun frenchMeasurementUnitLabel(unit: MeasurementUnit, quantity: Double): String {
    fun word(one: String, many: String = "${one}s") = if (quantity < 2.0) one else many
    return when (unit) {
        MeasurementUnit.TEASPOON -> "c. à c."
        MeasurementUnit.TABLESPOON -> "c. à s."
        MeasurementUnit.CUP -> word("tasse")
        MeasurementUnit.PINT -> word("pinte")
        MeasurementUnit.QUART -> word("quart")
        MeasurementUnit.GALLON -> word("gallon")
        MeasurementUnit.PINCH -> word("pincée")
        MeasurementUnit.DASH -> word("trait")
        MeasurementUnit.DROP -> word("goutte")
        MeasurementUnit.HANDFUL -> word("poignée")
        MeasurementUnit.PIECE -> word("pièce")
        MeasurementUnit.DOZEN -> word("douzaine")
        MeasurementUnit.SLICE -> word("tranche")
        MeasurementUnit.CUBE -> word("cube")
        MeasurementUnit.STICK -> word("bâton")
        MeasurementUnit.FILLET -> word("filet")
        MeasurementUnit.CLOVE -> word("gousse")
        MeasurementUnit.HEAD -> word("tête")
        MeasurementUnit.BUNCH -> word("botte")
        MeasurementUnit.STALK -> word("tige")
        MeasurementUnit.SPRIG -> word("brin")
        MeasurementUnit.LEAF -> word("feuille")
        MeasurementUnit.PACK -> word("paquet")
        MeasurementUnit.SACHET -> word("sachet")
        MeasurementUnit.CAN -> word("boîte de conserve", "boîtes de conserve")
        MeasurementUnit.BOTTLE -> word("bouteille")
        MeasurementUnit.JAR -> word("bocal", "bocaux")
        MeasurementUnit.BOX -> word("boîte")
        MeasurementUnit.BAG -> word("sac")
        MeasurementUnit.CARTON -> word("brique")
        MeasurementUnit.TRAY -> word("plateau", "plateaux")
        MeasurementUnit.LOAF -> word("pain")
        MeasurementUnit.INCH -> "po"
        MeasurementUnit.TO_TASTE -> "au goût"
        else -> unit.unit
    }
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
    moreUnits = "Plus d'unités…",
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
    mealType = "Type de repas",
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
    measurementUnitLabel = ::frenchMeasurementUnitLabel,

    navHome = "Accueil",
    navMealPlan = "Plan de repas",
    navRecipes = "Recettes",
    navShopping = "Courses",
    navProfile = "Profil",

    homeGreetingDefault = { period -> "${frenchGreeting(period)} !" },
    homeGreetingWithName = { period, name -> "${frenchGreeting(period)}, $name !" },
    homeSubtitle = "Planifions vos repas.",
    homeNotificationsCd = "Notifications",
    homeTodaysPlan = "Plan du jour",
    homeViewAll = "Tout voir",
    homeNoMealsPlannedToday = "Aucun repas prévu pour aujourd'hui.",
    homeQuickActions = "Actions rapides",
    homeAddAMeal = "Ajouter un repas",
    homeShoppingList = "Liste de courses",
    homeReadAloudCd = "Lire le plan du jour à voix haute",
    homeStopReadAloudCd = "Arrêter la lecture du plan du jour",

    calendarTitle = "Plan de Repas",
    calendarAddMeal = "Ajouter un Repas",
    calendarNoMealsPlannedDay = "Aucun repas prévu pour ce jour.",
    calendarNoMealsPlanned = "Aucun repas prévu",
    calendarRemoveMealTitle = "Supprimer le repas ?",
    calendarRemoveMealBody = { recipeName -> "Supprimer $recipeName du planning des repas ?" },
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
    recipesFavoritesOnly = "Favoris uniquement",
    recipesCreatedByMe = "Créées par moi",
    recipesPrepTimeRange = { start, end -> "Temps de préparation : $start–$end min" },
    recipesPrepTimeRangeNoMax = { start, end -> "Temps de préparation : $start–$end+ min" },
    recipesClearFilters = "Effacer les filtres",
    recipesNoRecipesYet = "Pas encore de recettes.",
    recipesNoFavoritesYet = "Pas encore de favoris. Touchez le cœur d'une recette pour en ajouter une.",
    recipesNoCreatedYet = "Aucune recette créée pour l'instant. Touchez + pour ajouter la vôtre.",
    recipesNoMatchesFilters = "Aucune recette ne correspond à vos filtres.",

    recipeDetailDeleteTitle = "Supprimer la recette ?",
    recipeDetailDeleteBody = { count, dates ->
        "Cette recette est programmée pour $count date(s) future(s) : $dates. La supprimer l'enlèvera de ces jours."
    },
    recipeDetailDeleteUnplannedBody = { recipeName -> "« $recipeName » sera supprimée. Cette action est irréversible." },
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
    servingsLabel = { count -> if (count == 1) "$count portion" else "$count portions" },
    recipeDetailAddToMealPlan = "Ajouter au Plan de Repas",

    recipeFormEditTitle = "Modifier la Recette",
    recipeFormNewTitle = "Nouvelle Recette",
    recipeFormNameExists = "Une recette portant ce nom existe déjà.",
    recipeFormDescription = "Description",
    recipeFormPrepMin = "Préparation (min)",
    recipeFormServings = "Portions",
    recipeFormPhoto = "Photo",
    recipeFormAddIngredient = "+ Ajouter un ingrédient",
    recipeFormAddInstruction = "+ Ajouter une instruction",
    recipeFormSaving = "Enregistrement...",
    recipeFormSaveRecipe = "Enregistrer la Recette",
    recipeFormNoIngredients = "Aucun ingrédient pour l'instant.",
    recipeFormDiscardTitle = "Abandonner les modifications ?",
    recipeFormDiscardBody = "Ce que vous avez saisi pour cette recette sera perdu.",
    recipeFormKeepEditing = "Continuer",
    recipeFormDiscard = "Abandonner",
    ingredientSearchPlaceholder = "Rechercher des ingrédients",
    ingredientSearchHint = "Tapez un nom pour trouver un ingrédient, ou ajoutez le vôtre.",
    ingredientAddCustom = { name -> "Ajouter « $name »" },
    ingredientAddAndNext = "Ajouter et suivant",
    ingredientDone = "Terminé",
    instructionsSteps = "Étapes",
    instructionsWriteAll = "Tout écrire",
    instructionsWriteAllHint = "Une étape par ligne",
    moveUp = "Monter",
    moveDown = "Descendre",

    shoppingNoMealsPlannedPeriod = "Aucun repas prévu pour cette période.",
    shoppingAddItemButton = "Ajouter un Article",
    shoppingEditAmountCd = "Modifier la quantité",
    shoppingSelectedCount = { count -> if (count == 1) "1 sélectionné" else "$count sélectionnés" },
    shoppingCancelSelectionCd = "Annuler la sélection",
    shoppingSelectItemCd = "Sélectionner",
    shoppingAtHome = "Produits de base",
    shoppingShowSectionCd = "Afficher les articles",
    shoppingHideSectionCd = "Masquer les articles",
    shoppingGroupLabel = "Groupe",
    shoppingMoveToAtHomeCd = "Déplacer vers Produits de base",
    shoppingMoveToListCd = "Remettre dans la liste",
    shoppingRemoveSelectedTitle = "Supprimer les articles ?",
    shoppingRemoveSelectedBody = { count -> if (count == 1) "Supprimer 1 article de cette liste ?" else "Supprimer $count articles de cette liste ?" },
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
    settingsTheme = "Thème",
    themeSystem = "Système",
    themeLight = "Clair",
    themeDark = "Sombre",
    settingsLockPastDates = "Verrouiller les dates passées",
    settingsDefaultCalendarView = "Vue par défaut du plan de repas",
    settingsDefaultRecipeView = "Vue par défaut des recettes",
    settingsLocalization = "Localisation",
    settingsAbout = "À propos",
    settingsAboutMealPlanner = "À propos de Meal Planner",
    settingsPrivacyPolicy = "Politique de Confidentialité",
    settingsReadAloud = "Lire à voix haute",
    settingsYourData = "Vos données",
    settingsExportBackup = "Enregistrer une sauvegarde",
    settingsImportBackup = "Restaurer une sauvegarde",
    backupImportConfirmTitle = "Restaurer la sauvegarde ?",
    backupImportConfirmBody = "Les recettes, plans de repas, listes de courses, réglages et photos de cet appareil seront remplacés par ceux du fichier de sauvegarde. L'application redémarre ensuite.",
    backupImportConfirmAction = "Restaurer",
    backupWorking = "En cours…",
    backupExported = "Sauvegarde enregistrée",
    backupFailed = "L'opération a échoué. Vos données n'ont pas été modifiées.",
    backupInvalidFile = "Ce fichier n'est pas une sauvegarde Meal Planner.",

    notificationSettingsTitle = "Paramètres de notification",
    notificationSettingsDescription = "Choisissez les repas pour lesquels vous souhaitez un rappel.",
    settingsNotificationsPermissionDenied = "Activez les notifications dans les paramètres système pour recevoir des rappels de repas.",
    settingsNotificationsOpenSettings = "Ouvrir les paramètres",
    mealReminderPlannedTitle = { mealType, recipes -> "${frenchMealTypeName(mealType)} : $recipes" },
    mealReminderPlannedBody = "C'est l'heure de cuisiner.",
    mealReminderNothingPlannedTitle = { mealType -> "${frenchMealTypeName(mealType)} : rien de prévu" },
    mealReminderNothingPlannedBody = "Appuyez pour prévoir un repas.",

    aboutAppName = "Meal Planner",
    aboutVersion = { version -> "Version $version" },
    aboutBody = "Meal Planner vous aide à planifier vos repas de la semaine, organiser vos recettes, générer " +
        "automatiquement des listes de courses à partir de votre plan de repas, et suivre vos dépenses " +
        "alimentaires.\n\nParcourez et enregistrez des recettes, assignez-les aux créneaux petit-déjeuner, " +
        "déjeuner, dîner ou collation de votre calendrier hebdomadaire, et laissez Meal Planner générer une " +
        "liste de courses pour vous. Marquez des recettes comme favorites, suivez le prix des ingrédients " +
        "dans le temps, et consultez les rapports de dépenses pour voir où va votre budget courses.\n\n" +
        "Créé avec Kotlin Multiplatform et Jetpack Compose.",

    privacyPolicyParagraphs = listOf(
        "Dernière mise à jour : 18 novembre 2026",
        "Meal Planner est une application de planification de repas qui garde vos données sur votre appareil. Elle n'a pas de compte utilisateur, pas de publicité, ni d'outil d'analyse ou de rapport de plantage. Le développeur ne collecte, ne reçoit et ne vend aucune donnée personnelle.",
        "Informations stockées sur votre appareil\nL'application stocke vos recettes, plans de repas, listes de courses, dépenses, profil (nom, bio et photo) et réglages dans son espace privé sur votre appareil. Quand la sauvegarde Android est activée, Android inclut ces données, photos comprises, dans la sauvegarde de votre appareil sur votre compte Google, et les copie quand vous passez à un nouvel appareil. Google conserve cette sauvegarde selon sa propre politique de confidentialité, et le développeur n'y a pas accès. Désinstaller l'application ou effacer ses données les supprime de votre appareil. Vous pouvez gérer ou supprimer les sauvegardes dans les réglages de sauvegarde de votre appareil.",
        "Photos\nQuand vous ajoutez une photo de recette ou de profil, l'application utilise le sélecteur de photos d'Android et ne voit donc que les photos que vous choisissez. L'application enregistre une copie réduite de chaque photo, sans sa localisation ni ses autres métadonnées. Les photos ne sont jamais envoyées, sauf dans la sauvegarde de votre appareil décrite ci-dessus.",
        "Rappels de repas\nSi vous activez les rappels de repas, c'est votre appareil qui les planifie et les affiche. Rien les concernant n'est envoyé ailleurs.",
        "Partage\nQuand vous exportez un rapport de dépenses, l'application transmet le fichier à l'application que vous choisissez dans le menu de partage d'Android. La politique de confidentialité de cette application s'applique ensuite. Quand vous enregistrez une sauvegarde depuis les réglages, le fichier contient vos recettes, plans de repas, listes de courses, réglages et photos, et il est enregistré uniquement à l'endroit que vous choisissez ; l'application ne l'envoie nulle part.",
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
    spendingShareChooserTitle = "Partager le rapport de dépenses",

    // Walkthrough
    walkthroughSkip = "Passer",
    walkthroughNext = "Suivant",
    walkthroughGetStarted = "Commencer",
    walkthroughPageOf = { page, total -> "Page $page sur $total" },
    walkthroughAddMeal = WalkthroughText("Planifiez un repas", "Dans l'onglet Plan de repas, touchez Ajouter un repas pour mettre une recette sur n'importe quel jour."),
    walkthroughPickMeal = WalkthroughText("Choisissez le repas et le jour", "Choisissez une recette, puis le petit-déjeuner, le déjeuner, le dîner ou une collation. Touchez Changer pour choisir un autre jour."),
    walkthroughRemoveMeal = WalkthroughText("Supprimez un repas", "Balayez un repas vers la gauche pour le supprimer. L'app vous demande d'abord de confirmer."),
    walkthroughCreateRecipe = WalkthroughText("Créez une recette", "Dans l'onglet Recettes, touchez + pour ajouter votre recette avec une photo, des ingrédients et des étapes."),
    walkthroughFilterRecipes = WalkthroughText("Trouvez la bonne recette", "Touchez le bouton filtre pour afficher les recettes par type de repas, cuisine, catégorie ou temps de préparation."),
    walkthroughCheckItem = WalkthroughText("Cochez vos achats", "Votre liste de courses se remplit à partir de votre plan de repas. Touchez un article pour le cocher."),
    walkthroughRemoveItem = WalkthroughText("Retirez un article", "Balayez un article vers la gauche pour le retirer de la liste."),
    walkthroughGroupItem = WalkthroughText("Regroupez vos articles", "Balayez un article vers la droite pour le mettre dans un groupe, comme un magasin ou un rayon."),
    walkthroughRecordPrice = WalkthroughText("Notez ce que vous payez", "Touchez le prix d'un article pour noter ce que vous avez payé. Les rapports de dépenses font le total."),
    walkthroughPantryStaples = WalkthroughText("Produits de base", "Le sel, l'huile et les autres produits de base que vous avez sans doute sont rangés sous Produits de base. Touchez le titre pour les afficher."),
    walkthroughReminders = WalkthroughText("Recevez des rappels", "Sur l'accueil, touchez la cloche pour activer un rappel quotidien pour chaque repas."),

    // Home-screen widgets
    widget = WidgetStrings(
        nextMeal = "Prochain repas",
        noUpcomingMeals = "Aucun repas prévu dans les 7 prochains jours.",
        today = "Aujourd'hui",
        tomorrow = "Demain",
        mealNotes = "Notes de repas",
        noWeekNote = "Aucune note pour cette semaine.",
        shoppingListEmpty = "Rien à acheter cette semaine.",
        recipeOfTheDay = "Recette du jour",
        shuffleCd = "Afficher une autre recette",
        quickAddTitle = "Ajouter à la liste de courses",
        addedToShoppingList = { name -> "« $name » ajouté à la liste de courses." },
        settingsTitle = "Réglages du widget",
        showWeekNote = "Afficher les notes de la semaine"
    ),

    // Feature guide
    featureGuide = "Comment utiliser Meal Planner",
    featureGuideReplayWalkthrough = "Revoir la présentation",
    featureGuideTrySaying = "Essayez de dire",
    featureGuideSections = listOf(
        GuideSection(
            title = "Accueil",
            items = listOf(
                "Voyez les repas du jour dans l'ordre. Touchez un repas pour ouvrir sa recette, ou Tout voir pour ouvrir le plan de repas.",
                "Ajouter un repas ouvre le plan de repas, et Liste de courses ouvre votre liste de courses.",
                "La cloche ouvre les réglages des rappels de repas.",
                "Avec Lire à voix haute activé dans Paramètres, le bouton haut-parleur lit les repas du jour."
            )
        ),
        GuideSection(
            title = "Plan de repas",
            items = listOf(
                "Passez de Jour à Semaine en haut. Paramètres choisit la vue qui s'ouvre en premier.",
                "Utilisez les flèches pour changer de semaine, le bouton calendrier pour aller à une date, et le bouton aujourd'hui pour revenir.",
                "Touchez Ajouter un repas, ou + sur un jour en vue Semaine, choisissez une recette, puis le repas et la date.",
                "Balayez un repas vers la gauche pour le supprimer. L'app vous demande d'abord de confirmer.",
                "En vue Jour, le menu ⋮ d'un repas permet de voir sa recette, de la remplacer ou de la supprimer.",
                "Écrivez des notes pour la semaine dans la zone du bas. Elles sont enregistrées au fil de la saisie.",
                "Activez Verrouiller les dates passées dans Paramètres pour ne pas supprimer par erreur les repas des jours précédents."
            )
        ),
        GuideSection(
            title = "Recettes",
            items = listOf(
                "Cherchez par nom, description ou ingrédient.",
                "Le bouton filtre affine la liste par favoris, vos recettes, catégorie, type de repas, cuisine et temps de préparation.",
                "Passez de la grille à la liste avec le bouton à côté du filtre.",
                "Touchez + pour créer une recette avec une photo, des ingrédients, des types de repas et des étapes. Les étapes sont facultatives.",
                "Dans l'éditeur d'étapes, Tout écrire permet de taper toutes les étapes d'un coup, une par ligne.",
                "Sur une recette, touchez le cœur pour l'ajouter à vos favoris, ou Ajouter au plan de repas pour la planifier.",
                "Avec Lire à voix haute activé, le haut-parleur lit les ingrédients et le mode cuisine lit les étapes une à une.",
                "Vos favoris et les recettes que vous avez créées sont aussi dans l'onglet Profil."
            )
        ),
        GuideSection(
            title = "Liste de courses",
            items = listOf(
                "La liste se remplit à partir des repas planifiés et additionne le même ingrédient sur plusieurs recettes.",
                "Choisissez Semaine, Jour ou Plage en haut pour fixer les dates de la liste.",
                "Touchez un article pour le cocher, et touchez-le encore pour le décocher.",
                "Balayez un article vers la gauche pour le retirer de la liste.",
                "Balayez un article vers la droite pour le mettre dans un groupe, comme un magasin ou un rayon. Vous pouvez y créer un groupe.",
                "Touchez le bouton dossier pour renommer ou supprimer des groupes.",
                "Touchez Ajouter un article pour ce qui ne vient pas d'une recette. Laissez la quantité vide si elle n'est pas utile.",
                "Le sel, l'huile et les autres produits de base que vous avez sans doute sont rangés sous Produits de base, en bas de la liste. Touchez le titre pour les afficher ou les masquer.",
                "Appuyez longuement sur des articles pour les sélectionner, puis déplacez-les vers Produits de base ou remettez-les dans la liste.",
                "Touchez le prix d'un article pour noter ce que vous avez payé. La liste affiche le total dépensé."
            )
        ),
        GuideSection(
            title = "Dépenses",
            items = listOf(
                "Ouvrez les rapports de dépenses depuis l'onglet Profil.",
                "Ils additionnent les prix notés dans la liste de courses.",
                "Choisissez Semaine, Jour ou Plage pour voir le total, la moyenne et un graphique. Touchez une barre pour voir son montant.",
                "Touchez le bouton partager pour envoyer le rapport en PDF."
            )
        ),
        GuideSection(
            title = "Rappels",
            items = listOf(
                "Ouvrez Paramètres, puis Notifications, ou touchez la cloche sur l'accueil.",
                "Activez un rappel pour le petit-déjeuner, le déjeuner, le dîner ou une collation, et touchez l'heure pour la changer.",
                "La première fois, l'app demande l'autorisation d'envoyer des notifications.",
                "Les rappels se déclenchent chaque jour vers l'heure choisie."
            )
        ),
        GuideSection(
            title = "Sauvegarde et restauration",
            items = listOf(
                "Dans Paramètres, Enregistrer une sauvegarde garde vos recettes, plannings, listes de courses, paramètres et photos dans un seul fichier .zip.",
                "Restaurer une sauvegarde remplace tout le contenu de l'app par la sauvegarde, puis redémarre l'app.",
                "Gardez le fichier de sauvegarde en lieu sûr, par exemple dans votre stockage en ligne, pour passer vos données sur un nouveau téléphone."
            )
        ),
        GuideSection(
            title = "Paramètres",
            items = listOf(
                "Thème : système, clair ou sombre.",
                "Verrouiller les dates passées, Lire à voix haute, et les vues du planning et des recettes qui s'ouvrent en premier.",
                "Devise des prix dans la liste de courses et les rapports de dépenses.",
                "À propos de Meal Planner et la politique de confidentialité."
            )
        ),
        GuideSection(
            title = "Widgets de l'écran d'accueil",
            items = listOf(
                "Appuyez longuement sur un espace vide de l'écran d'accueil, touchez Widgets et cherchez Meal Planner.",
                "Plan du jour affiche les repas du jour. Appuyez longuement dessus, puis touchez le bouton des réglages pour afficher aussi les notes de la semaine.",
                "Notes de repas affiche les notes écrites pour cette semaine.",
                "Liste de courses affiche la liste de la semaine. Touchez un article pour le cocher.",
                "Prochain repas affiche le prochain repas prévu. Touchez-le pour ouvrir la recette.",
                "Recette du jour choisit chaque jour une recette parmi vos favoris. Touchez le bouton de mélange pour en voir une autre.",
                "Ajout rapide ouvre une case pour ajouter un article à la liste de courses sans ouvrir l'application."
            )
        ),
        GuideSection(
            title = "Demandez à Gemini",
            items = listOf(
                "Sur Android 16 et versions ultérieures, Gemini peut utiliser Meal Planner pour vous.",
                "Il peut lire votre liste de courses, y ajouter des articles et les cocher.",
                "Il peut chercher des recettes, lire une recette à voix haute et ajouter une recette.",
                "Il peut lire votre planning du jour ou de la semaine et y ajouter une recette.",
                "Il peut vous dire combien vous avez dépensé aujourd'hui, cette semaine ou ce mois-ci."
            ),
            examples = listOf(
                "« Hey Gemini, ajoute du lait à ma liste de courses Meal Planner »",
                "« Hey Gemini, qu'est-ce qu'il y a au menu aujourd'hui dans Meal Planner ? »",
                "« Hey Gemini, trouve une recette de poulet adobo dans Meal Planner »",
                "« Hey Gemini, combien ai-je dépensé cette semaine dans Meal Planner ? »"
            )
        )
    )
)
