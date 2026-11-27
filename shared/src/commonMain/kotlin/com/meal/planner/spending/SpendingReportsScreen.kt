package com.meal.planner.spending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meal.domain.model.SpendingReport
import com.meal.planner.common.LocalWideLandscape
import com.meal.planner.common.LocalWideWidth
import com.meal.planner.common.WideContentWidth
import com.meal.planner.common.centeredMaxWidth
import com.meal.planner.common.formatPrice
import com.meal.planner.components.DateRangePickerDialog
import com.meal.planner.components.MealDatePickerDialog
import com.meal.planner.components.ScrollAwayHeader
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import com.meal.presentation.common.currentWeekSunday
import com.meal.presentation.common.formatAmount
import com.meal.presentation.common.today
import com.meal.presentation.shopping.ShoppingListRangeMode
import com.meal.presentation.spending.SpendingReportsUiState
import com.meal.presentation.spending.SpendingReportsViewModel
import org.koin.compose.viewmodel.koinViewModel

/** The chart is taller beside the breakdown on a tablet in landscape, where there is height to spare. */
private val WIDE_CHART_HEIGHT = 280.dp

@Composable
private fun ShoppingListRangeMode.label(): String = when (this) {
    ShoppingListRangeMode.WEEK -> LocalStrings.current.week
    ShoppingListRangeMode.RANGE -> LocalStrings.current.range
    ShoppingListRangeMode.DAY -> LocalStrings.current.day
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingReportsScreen(
    onBack: () -> Unit,
    viewModel: SpendingReportsViewModel = koinViewModel()
) {
    val spacing = MealPlannerTokens.spacing
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val canExport = remember { isPdfExportSupported() }
    val exportReport = rememberSpendingReportExporter()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.spendingReports, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = LocalStrings.current.back)
                    }
                },
                actions = {
                    if (canExport) {
                        IconButton(
                            onClick = { uiState.report?.let { exportReport(it, uiState.currencySymbol) } },
                            enabled = uiState.report?.lineItems?.isNotEmpty() == true
                        ) {
                            Icon(Icons.Default.Share, contentDescription = LocalStrings.current.spendingExportCd)
                        }
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { padding ->
        val report = uiState.report
        val showReport = !uiState.isLoading && uiState.errorMessage == null && !uiState.isEmpty && report != null
        if (LocalWideLandscape.current) {
            Row(
                modifier = Modifier.padding(padding).fillMaxSize().centeredMaxWidth(WideContentWidth),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Column(modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())) {
                    SpendingRangeControls(uiState, viewModel, onPickDate = { showDatePicker = true })
                    if (showReport && report != null) {
                        SpendingOverview(
                            report = report,
                            currencySymbol = uiState.currencySymbol,
                            chartHeight = WIDE_CHART_HEIGHT,
                            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm)
                        )
                    }
                }
                if (showReport && report != null) {
                    // The breakdown sits in its own card so each name stays visually tied to its
                    // price across the pane, and the card's top lines up with the range controls.
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(top = spacing.sm, end = spacing.lg, bottom = spacing.lg),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(spacing.cardPadding)
                        ) {
                            spendingBreakdown(report, uiState.currencySymbol)
                        }
                    }
                } else {
                    SpendingStateMessage(uiState, Modifier.weight(1f).fillMaxHeight())
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .then(if (LocalWideWidth.current) Modifier.centeredMaxWidth() else Modifier)
            ) {
                ScrollAwayHeader(
                    modifier = Modifier.weight(1f),
                    header = { SpendingRangeControls(uiState, viewModel, onPickDate = { showDatePicker = true }) }
                ) {
                    if (showReport && report != null) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm)
                        ) {
                            item {
                                SpendingOverview(report = report, currencySymbol = uiState.currencySymbol)
                                Spacer(Modifier.height(spacing.sectionGap))
                            }
                            spendingBreakdown(report, uiState.currencySymbol)
                        }
                    } else {
                        SpendingStateMessage(uiState, Modifier.fillMaxSize())
                    }
                }
            }
        }

        if (showDatePicker) {
            when (uiState.mode) {
                ShoppingListRangeMode.RANGE -> DateRangePickerDialog(
                    initialStart = uiState.rangeStart,
                    initialEnd = uiState.rangeEnd,
                    onRangeSelected = { start, end -> viewModel.selectRange(start, end) },
                    onDismiss = { showDatePicker = false }
                )
                else -> MealDatePickerDialog(
                    initialDate = uiState.rangeStart,
                    onDateSelected = { picked -> viewModel.goToDate(picked) },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpendingRangeControls(
    uiState: SpendingReportsUiState,
    viewModel: SpendingReportsViewModel,
    onPickDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MealPlannerTokens.spacing
    Column(modifier = modifier.padding(horizontal = spacing.lg, vertical = spacing.sm)) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ShoppingListRangeMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = uiState.mode == mode,
                    onClick = { viewModel.selectMode(mode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ShoppingListRangeMode.entries.size
                    )
                ) {
                    Text(mode.label())
                }
            }
        }

        Spacer(Modifier.height(spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (uiState.mode) {
                ShoppingListRangeMode.WEEK -> {
                    IconButton(onClick = { viewModel.previousWeek() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = LocalStrings.current.previousWeek)
                    }
                    Text(
                        text = "${uiState.rangeStart} - ${uiState.rangeEnd}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (uiState.rangeStart != currentWeekSunday()) {
                            IconButton(onClick = { viewModel.goToToday() }) {
                                Icon(Icons.Filled.Today, contentDescription = LocalStrings.current.goToCurrentWeek)
                            }
                        }
                        IconButton(onClick = onPickDate) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = LocalStrings.current.pickAWeek)
                        }
                        IconButton(onClick = { viewModel.nextWeek() }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = LocalStrings.current.nextWeek)
                        }
                    }
                }
                ShoppingListRangeMode.DAY -> {
                    IconButton(onClick = { viewModel.previousDay() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = LocalStrings.current.previousDay)
                    }
                    Text(
                        text = "${uiState.rangeStart}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (uiState.rangeStart != today()) {
                            IconButton(onClick = { viewModel.goToToday() }) {
                                Icon(Icons.Filled.Today, contentDescription = LocalStrings.current.goToToday)
                            }
                        }
                        IconButton(onClick = onPickDate) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = LocalStrings.current.pickADate)
                        }
                        IconButton(onClick = { viewModel.nextDay() }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = LocalStrings.current.nextDay)
                        }
                    }
                }
                ShoppingListRangeMode.RANGE -> {
                    Text(
                        text = "${uiState.rangeStart} - ${uiState.rangeEnd}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = onPickDate) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = LocalStrings.current.pickADateRange)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpendingStateMessage(uiState: SpendingReportsUiState, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.errorMessage != null -> Text(
                uiState.errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            else -> Text(
                LocalStrings.current.spendingNoPricedItems,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SpendingOverview(
    report: SpendingReport,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 160.dp
) {
    val spacing = MealPlannerTokens.spacing
    Column(modifier = modifier) {
        SpendingSummaryCard(
            totalSpent = report.totalSpent,
            bucketCount = report.buckets.size,
            currencySymbol = currencySymbol
        )
        Spacer(Modifier.height(spacing.md))
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            SpendingBarChart(
                buckets = report.buckets,
                currencySymbol = currencySymbol,
                modifier = Modifier.fillMaxWidth().padding(spacing.cardPadding),
                chartHeight = chartHeight
            )
        }
    }
}

private fun LazyListScope.spendingBreakdown(report: SpendingReport, currencySymbol: String) {
    item {
        Text(LocalStrings.current.spendingBreakdown, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(MealPlannerTokens.spacing.sm))
    }
    items(report.lineItems, key = { "${it.itemKey}_${it.rangeStart}_${it.rangeEnd}" }) { item ->
        SpendingLineItemRow(
            name = item.name,
            quantityLabel = formatAmount(item.totalQuantity, item.unit, LocalStrings.current),
            rangeLabel = "${item.rangeStart} - ${item.rangeEnd}",
            price = item.price,
            currencySymbol = currencySymbol
        )
    }
}

@Composable
private fun SpendingSummaryCard(totalSpent: Double, bucketCount: Int, currencySymbol: String) {
    val spacing = MealPlannerTokens.spacing
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SpendingStat(
                label = LocalStrings.current.totalSpent,
                value = "$currencySymbol${formatPrice(totalSpent)}",
                modifier = Modifier.weight(1f)
            )
            SpendingStat(
                label = LocalStrings.current.spendingAvgPerPeriod,
                value = "$currencySymbol${formatPrice(if (bucketCount > 0) totalSpent / bucketCount else 0.0)}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SpendingStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun SpendingLineItemRow(
    name: String,
    quantityLabel: String,
    rangeLabel: String,
    price: Double,
    currencySymbol: String
) {
    val spacing = MealPlannerTokens.spacing
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = spacing.sm)) {
            Text(text = name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "$quantityLabel  •  $rangeLabel",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$currencySymbol${formatPrice(price)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
