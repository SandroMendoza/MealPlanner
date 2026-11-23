package com.meal.planner.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.meal.planner.common.LocalCompactHeight
import kotlin.math.roundToInt

/**
 * Stacks [header] above [content], with [content] filling the remaining height.
 *
 * When [scrollAway] is true (by default, when the window is short), scrolling the list
 * inside [content] up slides the header off the top so the list gets the whole height,
 * and scrolling back to the top of the list brings the header back. Otherwise the header
 * stays pinned.
 */
@Composable
fun ScrollAwayHeader(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scrollAway: Boolean = LocalCompactHeight.current,
    content: @Composable () -> Unit
) {
    var headerHeight by remember { mutableFloatStateOf(0f) }
    var hidden by remember { mutableFloatStateOf(0f) }

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
                if (available.y < 0f) slide(available.y) else Offset.Zero

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
                if (available.y > 0f) slide(available.y) else Offset.Zero

            private fun slide(deltaY: Float): Offset {
                val newHidden = (hidden - deltaY).coerceIn(0f, headerHeight)
                val used = hidden - newHidden
                hidden = newHidden
                return Offset(0f, used)
            }
        }
    }

    Layout(
        contents = listOf(header, { Box { content() } }),
        modifier = if (scrollAway) modifier.clipToBounds().nestedScroll(connection) else modifier
    ) { (headerMeasurables, contentMeasurables), constraints ->
        val loose = constraints.copy(minHeight = 0)
        val headerPlaceables = headerMeasurables.map { it.measure(loose) }
        val fullHeader = headerPlaceables.sumOf { it.height }
        headerHeight = fullHeader.toFloat()
        val hiddenPx = if (scrollAway) hidden.coerceAtMost(headerHeight).roundToInt() else 0
        val visibleHeader = fullHeader - hiddenPx

        val width = constraints.maxWidth
        val height = if (constraints.hasBoundedHeight) constraints.maxHeight else fullHeader
        val contentHeight = (height - visibleHeader).coerceAtLeast(0)
        val contentPlaceables = contentMeasurables.map {
            it.measure(Constraints(minWidth = width, maxWidth = width, minHeight = contentHeight, maxHeight = contentHeight))
        }

        layout(width, height) {
            var y = -hiddenPx
            headerPlaceables.forEach {
                it.placeRelative(0, y)
                y += it.height
            }
            contentPlaceables.forEach { it.placeRelative(0, visibleHeader) }
        }
    }
}
