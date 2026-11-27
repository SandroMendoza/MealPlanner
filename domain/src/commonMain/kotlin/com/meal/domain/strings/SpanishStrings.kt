package com.meal.domain.strings

import com.meal.domain.model.enums.Cuisine
import com.meal.domain.model.enums.DayPeriod
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

private fun spanishGreeting(period: DayPeriod): String = when (period) {
    DayPeriod.MORNING -> "Buenos días"
    DayPeriod.AFTERNOON -> "Buenas tardes"
    DayPeriod.EVENING -> "Buenas noches"
}

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

/** Metric symbols never change; word units take the plural for any amount other than 1. */
private fun spanishMeasurementUnitLabel(unit: MeasurementUnit, quantity: Double): String {
    fun word(one: String, many: String = "${one}s") = if (quantity == 1.0) one else many
    return when (unit) {
        MeasurementUnit.TEASPOON -> "cdta."
        MeasurementUnit.TABLESPOON -> "cda."
        MeasurementUnit.CUP -> word("taza")
        MeasurementUnit.PINT -> word("pinta")
        MeasurementUnit.QUART -> word("cuarto de galón", "cuartos de galón")
        MeasurementUnit.GALLON -> word("galón", "galones")
        MeasurementUnit.PINCH -> word("pizca")
        MeasurementUnit.DASH -> word("chorrito")
        MeasurementUnit.DROP -> word("gota")
        MeasurementUnit.HANDFUL -> word("puñado")
        MeasurementUnit.PIECE -> word("pieza")
        MeasurementUnit.DOZEN -> word("docena")
        MeasurementUnit.SLICE -> word("rebanada")
        MeasurementUnit.CUBE -> word("cubo")
        MeasurementUnit.STICK -> word("barra")
        MeasurementUnit.FILLET -> word("filete")
        MeasurementUnit.CLOVE -> word("diente")
        MeasurementUnit.HEAD -> word("cabeza")
        MeasurementUnit.BUNCH -> word("manojo")
        MeasurementUnit.STALK -> word("tallo")
        MeasurementUnit.SPRIG -> word("ramita")
        MeasurementUnit.LEAF -> word("hoja")
        MeasurementUnit.PACK -> word("paquete")
        MeasurementUnit.SACHET -> word("sobre")
        MeasurementUnit.CAN -> word("lata")
        MeasurementUnit.BOTTLE -> word("botella")
        MeasurementUnit.JAR -> word("frasco")
        MeasurementUnit.BOX -> word("caja")
        MeasurementUnit.BAG -> word("bolsa")
        MeasurementUnit.CARTON -> word("cartón", "cartones")
        MeasurementUnit.TRAY -> word("bandeja")
        MeasurementUnit.LOAF -> word("hogaza")
        MeasurementUnit.INCH -> "pulg."
        MeasurementUnit.TO_TASTE -> "al gusto"
        else -> unit.unit
    }
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
    mealType = "Tipo de comida",
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
    measurementUnitLabel = ::spanishMeasurementUnitLabel,

    navHome = "Inicio",
    navMealPlan = "Plan de comidas",
    navRecipes = "Recetas",
    navShopping = "Compras",
    navProfile = "Perfil",

    homeGreetingDefault = { period -> "¡${spanishGreeting(period)}!" },
    homeGreetingWithName = { period, name -> "¡${spanishGreeting(period)}, $name!" },
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
    recipesPrepTimeRangeNoMax = { start, end -> "Tiempo de preparación: $start–$end+ min" },
    recipesClearFilters = "Borrar filtros",
    recipesNoRecipesYet = "Aún no hay recetas.",
    recipesNoFavoritesYet = "Aún no hay favoritos. Toca el corazón de una receta para añadirla.",
    recipesNoCreatedYet = "Aún no has creado recetas. Toca + para añadir la tuya.",
    recipesNoMatchesFilters = "Ninguna receta coincide con tus filtros.",

    recipeDetailDeleteTitle = "¿Eliminar receta?",
    recipeDetailDeleteBody = { count, dates ->
        "Esta receta está programada en $count fecha(s) futura(s): $dates. Eliminarla la quitará de esos días."
    },
    recipeDetailDeleteUnplannedBody = { recipeName -> "Se eliminará «$recipeName». No se puede deshacer." },
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
    servingsLabel = { count -> if (count == 1) "$count porción" else "$count porciones" },
    recipeDetailAddToMealPlan = "Añadir al Plan de Comidas",

    recipeFormEditTitle = "Editar Receta",
    recipeFormNewTitle = "Nueva Receta",
    recipeFormNameExists = "Ya existe una receta con este nombre.",
    recipeFormDescription = "Descripción",
    recipeFormPrepMin = "Preparación (min)",
    recipeFormServings = "Porciones",
    recipeFormPhoto = "Foto",
    recipeFormAddIngredient = "+ Añadir ingrediente",
    recipeFormAddInstruction = "+ Añadir instrucción",
    recipeFormSaving = "Guardando...",
    recipeFormSaveRecipe = "Guardar Receta",
    recipeFormNoIngredients = "Aún no hay ingredientes.",
    recipeFormDiscardTitle = "¿Descartar los cambios?",
    recipeFormDiscardBody = "Se perderá lo que escribiste en esta receta.",
    recipeFormKeepEditing = "Seguir editando",
    recipeFormDiscard = "Descartar",
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
    shoppingEditAmountCd = "Editar cantidad",
    shoppingSelectedCount = { count -> if (count == 1) "1 seleccionado" else "$count seleccionados" },
    shoppingCancelSelectionCd = "Cancelar selección",
    shoppingSelectItemCd = "Seleccionar",
    shoppingRemoveSelectedTitle = "¿Eliminar artículos?",
    shoppingRemoveSelectedBody = { count -> if (count == 1) "¿Eliminar 1 artículo de esta lista?" else "¿Eliminar $count artículos de esta lista?" },
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
    spendingShareChooserTitle = "Compartir informe de gastos",

    // Walkthrough
    walkthroughSkip = "Omitir",
    walkthroughNext = "Siguiente",
    walkthroughGetStarted = "Empezar",
    walkthroughPageOf = { page, total -> "Página $page de $total" },
    walkthroughAddMeal = WalkthroughText("Planifica una comida", "En la pestaña Plan de comidas, toca Añadir Comida para poner una receta en cualquier día."),
    walkthroughPickMeal = WalkthroughText("Elige la comida y el día", "Elige una receta y luego el desayuno, la comida, la cena o un snack. Toca Cambiar para elegir otro día."),
    walkthroughRemoveMeal = WalkthroughText("Quita una comida", "Desliza una comida hacia la izquierda para quitarla. La app te pide confirmarlo antes."),
    walkthroughCreateRecipe = WalkthroughText("Crea una receta", "En la pestaña Recetas, toca + para añadir tu propia receta con foto, ingredientes y pasos."),
    walkthroughFilterRecipes = WalkthroughText("Encuentra la receta adecuada", "Toca el botón de filtro para ver recetas por tipo de comida, cocina, categoría o tiempo de preparación."),
    walkthroughCheckItem = WalkthroughText("Marca lo que compras", "Tu lista de compras se llena sola con tu plan de comidas. Toca un artículo para marcarlo."),
    walkthroughRemoveItem = WalkthroughText("Quita un artículo", "Desliza un artículo hacia la izquierda para quitarlo de la lista."),
    walkthroughGroupItem = WalkthroughText("Agrupa tus artículos", "Desliza un artículo hacia la derecha para ponerlo en un grupo, como una tienda o un pasillo."),
    walkthroughRecordPrice = WalkthroughText("Anota lo que pagaste", "Toca el precio de un artículo para anotar lo que pagaste. Los informes de gastos lo suman."),
    walkthroughReminders = WalkthroughText("Recibe recordatorios", "En Inicio, toca la campana para activar un recordatorio diario para cada comida."),

    // Feature guide
    featureGuide = "Cómo usar Meal Planner",
    featureGuideReplayWalkthrough = "Ver la presentación de nuevo",
    featureGuideTrySaying = "Prueba a decir",
    featureGuideSections = listOf(
        GuideSection(
            title = "Inicio",
            items = listOf(
                "Mira las comidas de hoy en orden. Toca una comida para abrir su receta, o Ver todo para abrir el plan de comidas.",
                "Añadir una comida abre el plan de comidas, y Lista de compras abre tu lista de compras.",
                "La campana abre los ajustes de recordatorios.",
                "Con Leer en voz alta activado en Ajustes, el botón del altavoz lee las comidas de hoy."
            )
        ),
        GuideSection(
            title = "Plan de comidas",
            items = listOf(
                "Cambia entre Día y Semana arriba. Ajustes decide cuál se abre primero.",
                "Usa las flechas para cambiar de semana, el botón de calendario para ir a una fecha y el botón de hoy para volver.",
                "Toca Añadir comida, o + en un día en la vista Semana, elige una receta y luego la comida y la fecha.",
                "Desliza una comida hacia la izquierda para quitarla. La app te pide confirmarlo antes.",
                "En la vista Día, el menú ⋮ de una comida permite ver su receta, cambiarla por otra o quitarla.",
                "Escribe notas para la semana en el cuadro de abajo. Se guardan mientras escribes.",
                "Activa Bloquear fechas pasadas en Ajustes para no quitar por error comidas de días anteriores."
            )
        ),
        GuideSection(
            title = "Recetas",
            items = listOf(
                "Busca por nombre, descripción o ingrediente.",
                "El botón de filtro acota la lista por favoritas, tus recetas, categoría, tipo de comida, cocina y tiempo de preparación.",
                "Cambia entre cuadrícula y lista con el botón junto al filtro.",
                "Toca + para crear una receta con foto, ingredientes, tipos de comida y pasos. Los pasos son opcionales.",
                "En el editor de pasos, Escribir todo te deja escribir todos los pasos de una vez, uno por línea.",
                "En una receta, toca el corazón para añadirla a tus favoritas, o Añadir al plan de comidas para planificarla.",
                "Con Leer en voz alta activado, el altavoz lee los ingredientes y el modo cocina lee los pasos uno a uno.",
                "Tus favoritas y las recetas que creaste también están en la pestaña Perfil."
            )
        ),
        GuideSection(
            title = "Lista de compras",
            items = listOf(
                "La lista se llena sola con las comidas planificadas y suma el mismo ingrediente de varias recetas.",
                "Elige Semana, Día o Rango arriba para fijar las fechas de la lista.",
                "Toca un artículo para marcarlo, y tócalo otra vez para desmarcarlo.",
                "Desliza un artículo hacia la izquierda para quitarlo de la lista.",
                "Desliza un artículo hacia la derecha para ponerlo en un grupo, como una tienda o un pasillo. Ahí puedes crear un grupo nuevo.",
                "Toca el botón de carpeta para renombrar o borrar grupos.",
                "Toca Añadir artículo para lo que no viene de una receta.",
                "Toca el precio de un artículo para anotar lo que pagaste. La lista muestra el total gastado."
            )
        ),
        GuideSection(
            title = "Gastos",
            items = listOf(
                "Abre los informes de gastos desde la pestaña Perfil.",
                "Suman los precios que anotas en la lista de compras.",
                "Elige Semana, Día o Rango para ver el total, el promedio y un gráfico de barras. Toca una barra para ver su importe.",
                "Toca el botón de compartir para enviar el informe en PDF."
            )
        ),
        GuideSection(
            title = "Recordatorios",
            items = listOf(
                "Abre Ajustes y luego Notificaciones, o toca la campana en Inicio.",
                "Activa un recordatorio para el desayuno, la comida, la cena o un snack, y toca su hora para cambiarla.",
                "La primera vez, la app pide permiso para enviar notificaciones.",
                "Los recordatorios suenan cada día hacia la hora que elegiste."
            )
        ),
        GuideSection(
            title = "Copia de seguridad y restauración",
            items = listOf(
                "En Ajustes, Guardar una copia de seguridad guarda tus recetas, planes, listas de compras, ajustes y fotos en un solo archivo .zip.",
                "Restaurar una copia de seguridad reemplaza todo en la app por la copia y luego reinicia la app.",
                "Guarda el archivo en un lugar seguro, como tu almacenamiento en la nube, para pasar tus datos a un teléfono nuevo."
            )
        ),
        GuideSection(
            title = "Ajustes",
            items = listOf(
                "Tema: sistema, claro u oscuro.",
                "Bloquear fechas pasadas, Leer en voz alta y las vistas del plan y de las recetas que se abren primero.",
                "Moneda de los precios en la lista de compras y en los informes de gastos.",
                "Acerca de Meal Planner y la política de privacidad."
            )
        ),
        GuideSection(
            title = "Pregúntale a Gemini",
            items = listOf(
                "En Android 16 y versiones posteriores, Gemini puede usar Meal Planner por ti.",
                "Puede leer tu lista de compras, añadir artículos y marcarlos.",
                "Puede buscar recetas, leer una receta en voz alta y añadir una receta nueva.",
                "Puede leer tu plan de comidas de hoy o de esta semana y añadirle una receta.",
                "Puede decirte cuánto gastaste hoy, esta semana o este mes."
            ),
            examples = listOf(
                "“Hey Gemini, añade leche a mi lista de compras de Meal Planner”",
                "“Hey Gemini, ¿qué hay hoy en mi plan de Meal Planner?”",
                "“Hey Gemini, busca una receta de pollo adobo en Meal Planner”",
                "“Hey Gemini, ¿cuánto gasté esta semana en Meal Planner?”"
            )
        )
    )
)
