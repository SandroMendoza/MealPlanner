package com.meal.planner.spending

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.meal.domain.model.SpendingReport
import com.meal.domain.strings.Strings
import com.meal.planner.common.formatPrice
import com.meal.planner.strings.LocalStrings
import java.io.File
import java.io.FileOutputStream

private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN = 40f
private const val LINE_HEIGHT = 20f

actual fun isPdfExportSupported(): Boolean = true

@Composable
actual fun rememberSpendingReportExporter(): (SpendingReport, String) -> Unit {
    val context = LocalContext.current
    val strings = LocalStrings.current
    return { report, currencySymbol ->
        val file = generateReportPdf(context, report, currencySymbol, strings)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(sendIntent, strings.spendingShareChooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

private fun generateReportPdf(context: Context, report: SpendingReport, currencySymbol: String, strings: Strings): File {
    val document = PdfDocument()
    val titlePaint = Paint().apply { textSize = 20f; isFakeBoldText = true }
    val subtitlePaint = Paint().apply { textSize = 12f; color = Color.DKGRAY }
    val totalPaint = Paint().apply { textSize = 14f; isFakeBoldText = true }
    val bodyPaint = Paint().apply { textSize = 12f }

    var pageNumber = 1
    var page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
    var canvas = page.canvas
    var y = MARGIN

    fun startNewPageIfNeeded() {
        if (y + LINE_HEIGHT > PAGE_HEIGHT - MARGIN) {
            document.finishPage(page)
            pageNumber += 1
            page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
            canvas = page.canvas
            y = MARGIN
        }
    }

    canvas.drawText(strings.spendingPdfTitle, MARGIN, y, titlePaint)
    y += LINE_HEIGHT * 1.5f
    canvas.drawText(strings.spendingPdfDateRange(report.rangeStart.toString(), report.rangeEnd.toString()), MARGIN, y, subtitlePaint)
    y += LINE_HEIGHT * 1.5f
    canvas.drawText(strings.spendingPdfTotalSpent("$currencySymbol${formatPrice(report.totalSpent)}"), MARGIN, y, totalPaint)
    y += LINE_HEIGHT * 2

    report.lineItems.forEach { item ->
        startNewPageIfNeeded()
        val line = "${item.name}  (${item.rangeStart} - ${item.rangeEnd})   $currencySymbol${formatPrice(item.price)}"
        canvas.drawText(line, MARGIN, y, bodyPaint)
        y += LINE_HEIGHT
    }

    document.finishPage(page)

    val file = File(context.cacheDir, "spending_report_${report.rangeStart}_${report.rangeEnd}.pdf")
    FileOutputStream(file).use { out -> document.writeTo(out) }
    document.close()
    return file
}
