package com.meal.domain.strings

import com.meal.domain.model.enums.FoodCategory
import com.meal.domain.model.enums.MealType
import com.meal.domain.model.enums.MeasurementUnit
import kotlinx.datetime.DayOfWeek

private val SPANISH_WEEKDAY_SHORT = mapOf(
    DayOfWeek.MONDAY to "Lun", DayOfWeek.TUESDAY to "Mar", DayOfWeek.WEDNESDAY to "Mié",
    DayOfWeek.THURSDAY to "Jue", DayOfWeek.FRIDAY to "Vie", DayOfWeek.SATURDAY to "Sáb", DayOfWeek.SUNDAY to "Dom"
)

private val SPANISH_WEEKDAY_INITIAL = mapOf(
    DayOfWeek.MONDAY to "L", DayOfWeek.TUESDAY to "M", DayOfWeek.WEDNESDAY to "X",
    DayOfWeek.THURSDAY to "J", DayOfWeek.FRIDAY to "V", DayOfWeek.SATURDAY to "S", DayOfWeek.SUNDAY to "D"
)

private val SPANISH_MONTH_ABBREVIATIONS = listOf(
    "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
)

private fun spanishMealTypeName(mealType: MealType): String = when (mealType) {
    MealType.BREAKFAST -> "Desayuno"
    MealType.LUNCH -> "Almuerzo"
    MealType.DINNER -> "Cena"
    MealType.SNACK -> "Merienda"
}

private fun spanishFoodCategoryName(category: FoodCategory): String = when (category) {
    FoodCategory.MAIN_DISH -> "Plato principal"
    FoodCategory.SIDE_DISH -> "Guarnición"
    FoodCategory.SOUP -> "Sopa"
    FoodCategory.SALAD -> "Ensalada"
    FoodCategory.DESSERT -> "Postre"
    FoodCategory.BEVERAGE -> "Bebida"
}

private fun spanishMeasurementUnitName(unit: MeasurementUnit): String {
    val name = when (unit) {
        MeasurementUnit.GRAMS -> "Gramos"
        MeasurementUnit.KILOGRAMS -> "Kilogramos"
        MeasurementUnit.MILLILITER -> "Mililitro"
        MeasurementUnit.LITER -> "Litro"
        MeasurementUnit.TEASPOON -> "Cucharadita"
        MeasurementUnit.TABLESPOON -> "Cucharada"
        MeasurementUnit.CUP -> "Taza"
        MeasurementUnit.PIECE -> "Pieza"
    }
    return "$name (${unit.unit})"
}

val spanishStrings = Strings(
    back = "Atrás",
    cancel = "Cancelar",
    save = "Guardar",
    remove = "Eliminar",
    ok = "Aceptar",
    add = "Añadir",
    name = "Nombre",
    qty = "Cant.",
    unit = "Unidad",
    languageLabel = "Idioma",
    currencyLabel = "Moneda",
    week = "Semana",
    range = "Rango",
    day = "Día",
    previousWeek = "Semana anterior",
    nextWeek = "Semana siguiente",
    previousDay = "Día anterior",
    nextDay = "Día siguiente",
    goToToday = "Ir a hoy",
    goToCurrentWeek = "Ir a la semana actual",
    pickAWeek = "Elegir una semana",
    pickADate = "Elegir una fecha",
    pickADateRange = "Elegir un rango de fechas",
    searchRecipesPlaceholder = "Buscar recetas...",
    clearSearch = "Borrar búsqueda",
    choosePhoto = "Elegir foto",
    changePhoto = "Cambiar foto",
    category = "Categoría",
    ingredients = "Ingredientes",
    instructions = "Instrucciones",
    totalSpent = "Total gastado",
    all = "Todo",
    settings = "Ajustes",
    favorites = "Favoritos",
    myRecipes = "Mis recetas",
    spendingReports = "Informes de gastos",
    minutesLabel = { minutes -> "$minutes min" },
    weekdayShort = { dayOfWeek -> SPANISH_WEEKDAY_SHORT.getValue(dayOfWeek) },
    weekdayInitial = { dayOfWeek -> SPANISH_WEEKDAY_INITIAL.getValue(dayOfWeek) },
    monthAbbreviation = { month -> SPANISH_MONTH_ABBREVIATIONS[month - 1] },
    mealTypeName = ::spanishMealTypeName,
    foodCategoryName = ::spanishFoodCategoryName,
    measurementUnitName = ::spanishMeasurementUnitName,

    navHome = "Inicio",
    navMealPlan = "Plan de comidas",
    navRecipes = "Recetas",
    navShopping = "Compras",
    navProfile = "Perfil",

    homeGreetingDefault = "¡Buenos días!",
    homeGreetingWithName = { name -> "¡Buenos días, $name!" },
    homeSubtitle = "Planifiquemos tus comidas.",
    homeNotificationsCd = "Notificaciones",
    homeTodaysPlan = "Plan de hoy",
    homeViewAll = "Ver todo",
    homeNoMealsPlannedToday = "No hay comidas planificadas para hoy.",
    homeQuickActions = "Acciones rápidas",
    homeAddAMeal = "Añadir una comida",
    homeShoppingList = "Lista de compras",

    calendarTitle = "Plan de Comidas",
    calendarAddMeal = "Añadir Comida",
    calendarNoMealTypePlanned = { mealType -> "No hay $mealType planificado" },
    calendarAddMealTypeCd = { mealType -> "Añadir $mealType" },
    calendarMealOptionsCd = "Opciones de comida",
    calendarViewRecipe = "Ver receta",
    calendarChangeRecipe = "Cambiar receta",

    recipePickerTitle = "Elegir una receta",
    recipePickerAddRecipeNameTo = { recipeName -> "Añadir \"$recipeName\" a..." },
    recipePickerChange = "Cambiar",

    recipesAddCd = "Añadir receta",
    recipesFilters = "Filtros",
    recipesFiltersActiveCd = "Filtros (activos)",
    recipesSnacks = "Meriendas",
    recipesFavoritesOnly = "Solo favoritos",
    recipesCreatedByMe = "Creadas por mí",
    recipesPrepTimeRange = { start, end -> "Tiempo de preparación: $start–$end min" },
    recipesCalorieRange = { start, end -> "Calorías: $start–$end" },
    recipesClearFilters = "Borrar filtros",
    recipesNoRecipesYet = "Aún no hay recetas.",
    recipesNoFavoritesYet = "Aún no hay favoritos. Toca el corazón de una receta para añadirla.",
    recipesNoCreatedYet = "Aún no has creado recetas. Toca + para añadir la tuya.",
    recipesNoMatchesFilters = "Ninguna receta coincide con tus filtros.",

    recipeDetailDeleteTitle = "¿Eliminar receta?",
    recipeDetailDeleteBody = { count, dates ->
        "Esta receta está programada en $count fecha(s) futura(s): $dates. Eliminarla la quitará de esos días."
    },
    recipeDetailDelete = "Eliminar",
    recipeDetailNotFound = "Receta no encontrada.",
    recipeDetailEditCd = "Editar receta",
    recipeDetailDeleteCd = "Eliminar receta",
    recipeDetailRemoveFavoriteCd = "Quitar de favoritos",
    recipeDetailAddFavoriteCd = "Añadir a favoritos",
    caloriesLabel = { count -> "$count calorías" },
    servingsLabel = { count -> if (count == 1) "$count porción" else "$count porciones" },
    recipeDetailAddToMealPlan = "Añadir al Plan de Comidas",

    recipeFormEditTitle = "Editar Receta",
    recipeFormNewTitle = "Nueva Receta",
    recipeFormNameExists = "Ya existe una receta con este nombre.",
    recipeFormDescription = "Descripción",
    recipeFormMealTypes = "Tipos de comida",
    recipeFormPrepMin = "Preparación (min)",
    recipeFormServings = "Porciones",
    recipeFormCalories = "Calorías",
    recipeFormPhoto = "Foto",
    recipeFormAddIngredient = "+ Añadir ingrediente",
    recipeFormAddInstruction = "+ Añadir instrucción",
    recipeFormSaving = "Guardando...",
    recipeFormSaveRecipe = "Guardar Receta",

    shoppingNoMealsPlannedPeriod = "No hay comidas planificadas para este período.",
    shoppingAddPrice = "Añadir precio",
    shoppingAddItemButton = "Añadir Artículo",
    shoppingRemoveItemTitle = "¿Eliminar artículo?",
    shoppingRemoveItemBody = { itemName -> "¿Eliminar $itemName de la lista de compras?" },
    recordPriceTitle = { itemName -> "¿Cuánto costó $itemName?" },
    recordPricePriceLabel = { currencySymbol -> "Precio ($currencySymbol)" },

    profileTitle = "Perfil",
    profileEditProfile = "Editar perfil",
    profileYourNamePlaceholder = "Tu nombre",
    profileRecipesStat = "Recetas",
    profileCreatedStat = "Creadas",
    profileThisWeekStat = "Esta Semana",
    editProfileBio = "Biografía (opcional)",

    settingsPreferences = "Preferencias",
    settingsNotifications = "Notificaciones",
    settingsDarkMode = "Modo oscuro",
    settingsLocalization = "Localización",
    settingsAbout = "Acerca de",
    settingsAboutMealPlanner = "Acerca de Meal Planner",
    settingsPrivacyPolicy = "Política de Privacidad",

    aboutAppName = "Meal Planner",
    aboutVersion = "Versión 1.0",
    aboutBody = "Meal Planner te ayuda a planificar comidas semanales, organizar recetas, generar listas de " +
        "compras automáticamente a partir de tu plan de comidas, y llevar un control de lo que gastas en el " +
        "supermercado.\n\nExplora y guarda recetas, asígnalas a los horarios de desayuno, almuerzo, cena o " +
        "merienda en tu calendario semanal, y deja que Meal Planner genere una lista de compras por ti. Marca " +
        "recetas como favoritas, registra el precio de los ingredientes a lo largo del tiempo, y consulta los " +
        "informes de gastos para ver en qué se va tu presupuesto de comida.\n\nCreado con Kotlin Multiplatform " +
        "y Jetpack Compose.",

    privacyDisclaimer = "Este es un texto de política provisional y no ha sido revisado por un asesor legal. " +
        "Reemplázalo antes de pasar a producción.",
    privacyPolicyParagraphs = listOf(
        "Última actualización: [FECHA — MARCADOR DE POSICIÓN]",
        "Esta Política de Privacidad provisional describe, en términos generales, qué información puede " +
            "recopilar Meal Planner y cómo puede utilizarse. Se proporciona solo como punto de partida y debe " +
            "revisarse y reemplazarse por una política precisa y revisada legalmente antes de distribuir esta " +
            "app a usuarios reales.",
        "Información que almacenamos\nMeal Planner almacena tus recetas, planes de comidas, listas de " +
            "compras, información de perfil (nombre, biografía, avatar) y preferencias de la app " +
            "(notificaciones, tema, idioma, moneda) localmente en tu dispositivo. En esta versión, no existe " +
            "un sistema de cuentas ni un servidor remoto, y ninguno de los datos mencionados es transmitido " +
            "fuera de tu dispositivo por la propia app.",
        "Imágenes\nSi eliges una foto de perfil o una imagen de receta desde tu dispositivo, esa imagen se " +
            "referencia localmente y la app no la sube a ningún lugar.",
        "Servicios de terceros\nEsta política provisional aún no contempla ningún servicio de terceros, " +
            "análisis, informes de fallos o SDK publicitarios. Si en el futuro se añaden dichos servicios a " +
            "la app, esta política deberá actualizarse en consecuencia antes del lanzamiento.",
        "Privacidad de menores\nMeal Planner no está dirigida a menores, y esta política provisional no " +
            "incluye lenguaje específico sobre privacidad infantil.",
        "Cambios a esta política\nComo este es un texto provisional, se espera que cambie. Reemplaza este " +
            "documento completo con un texto final y revisado, adecuado a las prácticas reales de datos de " +
            "esta app, antes de publicarla.",
        "Contacto\n[MARCADOR DE POSICIÓN — inserta un correo o dirección de contacto real antes del lanzamiento]"
    ),

    spendingExportCd = "Exportar informe como PDF",
    spendingNoPricedItems = "No hay artículos con precio en este período.",
    spendingBreakdown = "Desglose",
    spendingAvgPerPeriod = "Prom. / período",
    spendingPdfTitle = "Informe de Gastos",
    spendingPdfDateRange = { start, end -> "$start al $end" },
    spendingPdfTotalSpent = { amount -> "Total gastado: $amount" },
    spendingShareChooserTitle = "Compartir informe de gastos"
)
