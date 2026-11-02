package com.meal.planner.spending

import androidx.compose.runtime.Composable
import com.meal.domain.model.SpendingReport

expect fun isPdfExportSupported(): Boolean

@Composable
expect fun rememberSpendingReportExporter(): (SpendingReport, String) -> Unit
