package com.meal.planner.spending

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.meal.domain.model.SpendingBucket
import com.meal.planner.common.formatPrice
import com.meal.planner.theme.MealPlannerTokens
import kotlinx.datetime.DayOfWeek

private val MONTH_ABBREVIATIONS = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

private fun dayLabel(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "M"
    DayOfWeek.TUESDAY -> "T"
    DayOfWeek.WEDNESDAY -> "W"
    DayOfWeek.THURSDAY -> "T"
    DayOfWeek.FRIDAY -> "F"
    DayOfWeek.SATURDAY -> "S"
    DayOfWeek.SUNDAY -> "S"
    else -> "?"
}

private fun bucketAxisLabel(bucket: SpendingBucket): String =
    if (bucket.bucketStart == bucket.bucketEnd) {
        dayLabel(bucket.bucketStart.dayOfWeek)
    } else {
        "${MONTH_ABBREVIATIONS[bucket.bucketStart.monthNumber - 1]} ${bucket.bucketStart.dayOfMonth}"
    }

/**
 * A single-series bar chart of spending per bucket. One hue (MaterialTheme.colorScheme.primary)
 * carries the data, so no legend is drawn - the screen's own summary card already states what's
 * plotted. Values aren't labeled on every bar (that reads as chaos); tapping a bar - or the
 * tallest bar by default - surfaces its exact amount above the chart.
 */
@Composable
fun SpendingBarChart(buckets: List<SpendingBucket>, currencySymbol: String = "$", modifier: Modifier = Modifier) {
    if (buckets.isEmpty()) return

    val spacing = MealPlannerTokens.spacing
    val barColor = MaterialTheme.colorScheme.primary
    val dimBarColor = barColor.copy(alpha = 0.45f)
    val baselineColor = MaterialTheme.colorScheme.outlineVariant
    val valueLabelColor = MaterialTheme.colorScheme.onSurface
    val textMeasurer = rememberTextMeasurer()
    val axisLabelStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    var selectedIndex by remember(buckets) {
        mutableStateOf(buckets.indices.maxByOrNull { buckets[it].amount } ?: 0)
    }
    val selectedBucket = buckets.getOrNull(selectedIndex)
    val maxAmount = buckets.maxOf { it.amount }.coerceAtLeast(0.01)

    Column(modifier = modifier) {
        Text(
            text = selectedBucket?.let { "${bucketAxisLabel(it)}  •  $currencySymbol${formatPrice(it.amount)}" }.orEmpty(),
            style = MaterialTheme.typography.labelLarge,
            color = valueLabelColor
        )
        Spacer(Modifier.height(spacing.sm))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(buckets) {
                    detectTapGestures { offset ->
                        val slotWidth = size.width / buckets.size.toFloat()
                        selectedIndex = (offset.x / slotWidth).toInt().coerceIn(0, buckets.size - 1)
                    }
                }
        ) {
            val axisLabelBandHeight = 18.dp.toPx()
            val baselineY = size.height - axisLabelBandHeight
            val barAreaHeight = baselineY - 4.dp.toPx()
            val slotWidth = size.width / buckets.size
            val barWidth = (slotWidth * 0.5f).coerceAtMost(24.dp.toPx())
            val cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())

            drawLine(
                color = baselineColor,
                start = Offset(0f, baselineY),
                end = Offset(size.width, baselineY),
                strokeWidth = 1.dp.toPx()
            )

            buckets.forEachIndexed { index, bucket ->
                val centerX = slotWidth * index + slotWidth / 2f
                val barHeight = (bucket.amount / maxAmount).toFloat() * barAreaHeight

                if (barHeight > 0f) {
                    // Rounded data-end (top), square where the bar meets the baseline.
                    val barRect = Rect(
                        left = centerX - barWidth / 2f,
                        top = baselineY - barHeight,
                        right = centerX + barWidth / 2f,
                        bottom = baselineY
                    )
                    val roundRect = RoundRect(
                        rect = barRect,
                        topLeft = cornerRadius,
                        topRight = cornerRadius,
                        bottomLeft = CornerRadius.Zero,
                        bottomRight = CornerRadius.Zero
                    )
                    drawPath(
                        path = Path().apply { addRoundRect(roundRect) },
                        color = if (index == selectedIndex) barColor else dimBarColor
                    )
                } else {
                    // Zero-spend bucket: a flat baseline tick, not a fake sliver of a bar.
                    drawLine(
                        color = baselineColor,
                        start = Offset(centerX - barWidth / 2f, baselineY),
                        end = Offset(centerX + barWidth / 2f, baselineY),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                val label = textMeasurer.measure(bucketAxisLabel(bucket), axisLabelStyle)
                drawText(
                    textLayoutResult = label,
                    topLeft = Offset(centerX - label.size.width / 2f, baselineY + 2.dp.toPx())
                )
            }
        }
    }
}
