package com.meal.planner.spending

import androidx.compose.runtime.Composable
import com.meal.domain.model.SpendingReport

// Not implemented: this machine has no Xcode toolchain to build/verify a PDF/share
// implementation. Export is hidden on iOS (see isPdfExportSupported) until an actual
// implementation lands.
actual fun isPdfExportSupported(): Boolean = false

@Composable
actual fun rememberSpendingReportExporter(): (SpendingReport, String) -> Unit {
    return { _, _ -> }
}
