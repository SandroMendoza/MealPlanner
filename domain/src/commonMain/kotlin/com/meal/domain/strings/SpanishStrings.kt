package com.meal.domain.strings

import com.meal.domain.model.enums.Cuisine
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

private fun spanishCuisineName(cuisine: Cuisine): String = when (cuisine) {
    Cuisine.FILIPINO -> "Filipina"
    Cuisine.CHINESE -> "China"
    Cuisine.JAPANESE -> "Japonesa"
    Cuisine.KOREAN -> "Coreana"
    Cuisine.THAI -> "Tailandesa"
    Cuisine.VIETNAMESE -> "Vietnamita"
    Cuisine.INDIAN -> "India"
    Cuisine.ITALIAN -> "Italiana"
    Cuisine.MEXICAN -> "Mexicana"
    Cuisine.AMERICAN -> "Estadounidense"
    Cuisine.OTHER -> "Otra"
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
        MeasurementUnit.OUNCE -> "Onza"
        MeasurementUnit.POUND -> "Libra"
        MeasurementUnit.FLUID_OUNCE -> "Onza líquida"
        MeasurementUnit.PINT -> "Pinta"
        MeasurementUnit.QUART -> "Cuarto de galón"
        MeasurementUnit.GALLON -> "Galón"
        MeasurementUnit.PINCH -> "Pizca"
        MeasurementUnit.DASH -> "Chorrito"
        MeasurementUnit.DROP -> "Gota"
        MeasurementUnit.HANDFUL -> "Puñado"
        MeasurementUnit.DOZEN -> "Docena"
        MeasurementUnit.SLICE -> "Rebanada"
        MeasurementUnit.CUBE -> "Cubo"
        MeasurementUnit.STICK -> "Barra"
        MeasurementUnit.FILLET -> "Filete"
        MeasurementUnit.CLOVE -> "Diente"
        MeasurementUnit.HEAD -> "Cabeza"
        MeasurementUnit.BUNCH -> "Manojo"
        MeasurementUnit.STALK -> "Tallo"
        MeasurementUnit.SPRIG -> "Ramita"
        MeasurementUnit.LEAF -> "Hoja"
        MeasurementUnit.PACK -> "Paquete"
        MeasurementUnit.SACHET -> "Sobre"
        MeasurementUnit.CAN -> "Lata"
        MeasurementUnit.BOTTLE -> "Botella"
        MeasurementUnit.JAR -> "Frasco"
        MeasurementUnit.BOX -> "Caja"
        MeasurementUnit.BAG -> "Bolsa"
        MeasurementUnit.CARTON -> "Cartón"
        MeasurementUnit.TRAY -> "Bandeja"
        MeasurementUnit.LOAF -> "Hogaza"
        MeasurementUnit.CENTIMETER -> "Centímetro"
        MeasurementUnit.INCH -> "Pulgada"
        MeasurementUnit.TO_TASTE -> "Al gusto"
    }
    return if (unit == MeasurementUnit.TO_TASTE || name.equals(unit.unit, ignoreCase = true)) name else "$name (${unit.unit})"
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
    moreUnits = "Más unidades…",
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
    hideKeyboard = "Ocultar teclado",
    choosePhoto = "Elegir foto",
    changePhoto = "Cambiar foto",
    takePhoto = "Tomar foto",
    chooseFromGallery = "Elegir de la galería",
    category = "Categoría",
    cuisine = "Cocina",
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
    cuisineName = ::spanishCuisineName,
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
    homeReadAloudCd = "Leer el plan de hoy en voz alta",
    homeStopReadAloudCd = "Detener la lectura del plan de hoy",

    calendarTitle = "Plan de Comidas",
    calendarAddMeal = "Añadir Comida",
    calendarNoMealsPlannedDay = "No hay comidas planificadas para este día.",
    calendarNoMealsPlanned = "Sin comidas planificadas",
    calendarRemoveMealTitle = "¿Eliminar comida?",
    calendarRemoveMealBody = { recipeName -> "¿Eliminar $recipeName del plan de comidas?" },
    calendarMealOptionsCd = "Opciones de comida",
    calendarViewRecipe = "Ver receta",
    calendarChangeRecipe = "Cambiar receta",
    calendarWeekNotes = "Notas",
    calendarWeekNotesPlaceholder = "Escribe una nota para esta semana...",
    calendarWeekNumber = { weekNumber -> "Semana $weekNumber" },

    recipePickerTitle = "Elegir una receta",
    recipePickerAddRecipeNameTo = { recipeName -> "Añadir \"$recipeName\" a..." },
    recipePickerChange = "Cambiar",
    recipePickerCreateRecipeCd = "Crear nueva receta",

    recipesAddCd = "Añadir receta",
    recipesFilters = "Filtros",
    recipesFiltersActiveCd = "Filtros (activos)",
    recipesShowAsListCd = "Ver como lista",
    recipesShowAsGridCd = "Ver como cuadrícula",
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
    recipeDetailReadIngredientsCd = "Leer ingredientes en voz alta",
    recipeDetailStopIngredientsCd = "Detener la lectura de ingredientes",
    recipeDetailCookMode = "Modo cocina",
    recipeDetailCookModePlayCd = "Reproducir paso",
    recipeDetailCookModePauseCd = "Pausar paso",
    recipeDetailCookModeNextCd = "Paso siguiente",
    recipeDetailCookModePreviousCd = "Paso anterior",
    recipeDetailCookModeStopCd = "Salir del modo cocina",
    caloriesLabel = { count -> "$count calorías" },
    servingsLabel = { count -> if (count == 1) "$count porción" else "$count porciones" },
    recipeDetailAddToMealPlan = "Añadir al Plan de Comidas",

    recipeFormEditTitle = "Editar Receta",
    recipeFormNewTitle = "Nueva Receta",
    recipeFormNameExists = "Ya existe una receta con este nombre.",
    recipeFormDescription = "Descripción",
    recipeFormPrepMin = "Preparación (min)",
    recipeFormServings = "Porciones",
    recipeFormCalories = "Calorías",
    recipeFormPhoto = "Foto",
    recipeFormAddIngredient = "+ Añadir ingrediente",
    recipeFormAddInstruction = "+ Añadir instrucción",
    recipeFormSaving = "Guardando...",
    recipeFormSaveRecipe = "Guardar Receta",
    recipeFormNoIngredients = "Aún no hay ingredientes.",
    ingredientSearchPlaceholder = "Buscar ingredientes",
    ingredientSearchHint = "Escribe un nombre para buscar un ingrediente o añadir uno propio.",
    ingredientAddCustom = { name -> "Añadir «$name»" },
    ingredientAddAndNext = "Añadir y otro",
    ingredientDone = "Listo",
    instructionsSteps = "Pasos",
    instructionsWriteAll = "Escribir todo",
    instructionsWriteAllHint = "Un paso por línea",
    moveUp = "Subir",
    moveDown = "Bajar",

    shoppingNoMealsPlannedPeriod = "No hay comidas planificadas para este período.",
    shoppingAddItemButton = "Añadir Artículo",
    shoppingRemoveItemTitle = "¿Eliminar artículo?",
    shoppingRemoveItemBody = { itemName -> "¿Eliminar $itemName de la lista de compras?" },
    recordPriceTitle = { itemName -> "¿Cuánto costó $itemName?" },
    recordPricePriceLabel = { currencySymbol -> "Precio ($currencySymbol)" },
    shoppingUngrouped = "Sin grupo",
    shoppingAssignGroupTitle = "Mover a un grupo",
    shoppingAssignGroupCd = "Asignar a un grupo",
    shoppingNewGroupPlaceholder = "Nombre del nuevo grupo",
    shoppingCreateGroup = "Crear",
    shoppingManageGroupsCd = "Gestionar grupos",
    shoppingReadAloudCd = "Leer la lista de compras en voz alta",
    shoppingStopReadAloudCd = "Detener la lectura de la lista de compras",
    shoppingManageGroupsTitle = "Gestionar grupos",
    shoppingDeleteGroupTitle = "¿Eliminar grupo?",
    shoppingDeleteGroupBody = { groupName -> "Los artículos de \"$groupName\" volverán a Sin grupo." },

    profileTitle = "Perfil",
    profileEditProfile = "Editar perfil",
    profileYourNamePlaceholder = "Tu nombre",
    profileRecipesStat = "Recetas",
    profileCreatedStat = "Creadas",
    profileThisWeekStat = "Esta Semana",
    editProfileBio = "Biografía (opcional)",

    settingsPreferences = "Preferencias",
    settingsNotifications = "Notificaciones",
    settingsTheme = "Tema",
    themeSystem = "Sistema",
    themeLight = "Claro",
    themeDark = "Oscuro",
    settingsLockPastDates = "Bloquear fechas pasadas",
    settingsDefaultCalendarView = "Vista predeterminada del plan de comidas",
    settingsDefaultRecipeView = "Vista predeterminada de recetas",
    settingsLocalization = "Localización",
    settingsAbout = "Acerca de",
    settingsAboutMealPlanner = "Acerca de Meal Planner",
    settingsPrivacyPolicy = "Política de Privacidad",
    settingsReadAloud = "Leer en voz alta",
    settingsYourData = "Tus datos",
    settingsExportBackup = "Guardar una copia de seguridad",
    settingsImportBackup = "Restaurar una copia de seguridad",
    backupImportConfirmTitle = "¿Restaurar la copia de seguridad?",
    backupImportConfirmBody = "Se reemplazarán las recetas, planes de comidas, listas de compras, ajustes y fotos de este dispositivo por los del archivo de copia de seguridad. La app se reiniciará al terminar.",
    backupImportConfirmAction = "Restaurar",
    backupWorking = "Procesando…",
    backupExported = "Copia de seguridad guardada",
    backupFailed = "No funcionó. Tus datos no se modificaron.",
    backupInvalidFile = "Este archivo no es una copia de seguridad de Meal Planner.",

    notificationSettingsTitle = "Configuración de notificaciones",
    notificationSettingsDescription = "Elige para qué comidas quieres recibir recordatorios.",
    settingsNotificationsPermissionDenied = "Activa las notificaciones en la configuración del sistema para recibir recordatorios de comidas.",
    settingsNotificationsOpenSettings = "Abrir configuración",
    mealReminderNotificationTitle = { mealType -> "Recordatorio de ${spanishMealTypeName(mealType).lowercase()}" },
    mealReminderNotificationBody = "Toca para ver lo que tienes planeado.",

    aboutAppName = "Meal Planner",
    aboutVersion = { version -> "Versión $version" },
    aboutBody = "Meal Planner te ayuda a planificar comidas semanales, organizar recetas, generar listas de " +
        "compras automáticamente a partir de tu plan de comidas, y llevar un control de lo que gastas en el " +
        "supermercado.\n\nExplora y guarda recetas, asígnalas a los horarios de desayuno, almuerzo, cena o " +
        "merienda en tu calendario semanal, y deja que Meal Planner genere una lista de compras por ti. Marca " +
        "recetas como favoritas, registra el precio de los ingredientes a lo largo del tiempo, y consulta los " +
        "informes de gastos para ver en qué se va tu presupuesto de comida.\n\nCreado con Kotlin Multiplatform " +
        "y Jetpack Compose.",

    privacyPolicyParagraphs = listOf(
        "Última actualización: 18 de noviembre de 2026",
        "Meal Planner es una app de planificación de comidas que guarda tus datos en tu dispositivo. No tiene cuentas de usuario, ni anuncios, ni herramientas de análisis o informes de fallos. El desarrollador no recopila, recibe ni vende ningún dato personal.",
        "Información guardada en tu dispositivo\nLa app guarda tus recetas, planes de comidas, listas de compras, gastos, perfil (nombre, biografía y foto) y ajustes en su almacenamiento privado de tu dispositivo. Cuando la copia de seguridad de Android está activada, Android incluye estos datos, fotos incluidas, en la copia de seguridad de tu dispositivo en tu cuenta de Google, y los copia cuando pasas a un dispositivo nuevo. Google guarda la copia según su propia política de privacidad, y el desarrollador no tiene acceso a ella. Desinstalar la app o borrar sus datos los elimina de tu dispositivo. Puedes gestionar o eliminar las copias de seguridad en los ajustes de copia de seguridad de tu dispositivo.",
        "Fotos\nCuando añades una foto de receta o de perfil, la app usa el selector de fotos de Android, así que solo ve las fotos que eliges. La app guarda una copia más pequeña de cada foto, sin su ubicación ni otros metadatos. Las fotos nunca se suben, salvo en la copia de seguridad de tu dispositivo descrita arriba.",
        "Recordatorios de comidas\nSi activas los recordatorios de comidas, tu dispositivo los programa y los muestra. No se envía nada sobre ellos a ningún sitio.",
        "Compartir\nCuando exportas un informe de gastos, la app pasa el archivo a la app que elijas en el menú de compartir de Android. A partir de ahí se aplica la política de privacidad de esa app. Cuando guardas una copia de seguridad desde los ajustes, el archivo contiene tus recetas, planes de comidas, listas de compras, ajustes y fotos, y se guarda solo donde tú elijas; la app no lo envía a ningún sitio.",
        "Lectura en voz alta\nLa lectura en voz alta usa el servicio de texto a voz instalado en tu dispositivo. Ese servicio procesa el texto según la política de privacidad de su proveedor.",
        "Asistentes de voz\nEn las versiones de Android compatibles, las apps de asistente de tu dispositivo pueden leer y modificar tu lista de compras, recetas, plan de comidas e informe de gastos cuando se lo pides. El asistente trata esos datos según su propia política de privacidad.",
        "Menores\nMeal Planner no está dirigida a menores de 13 años y no recopila datos de nadie a sabiendas.",
        "Cambios en esta política\nSi cambian las prácticas de datos de la app, esta política se actualiza y la fecha de arriba también.",
        "Contacto\nPreguntas sobre esta política: sandragon.inc@gmail.com"
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
