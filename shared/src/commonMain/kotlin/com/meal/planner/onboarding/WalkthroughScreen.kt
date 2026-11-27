package com.meal.planner.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.meal.domain.strings.Strings
import com.meal.domain.strings.WalkthroughText
import com.meal.planner.common.LocalCompactHeight
import com.meal.planner.common.LocalWideWidth
import com.meal.planner.common.centeredMaxWidth
import com.meal.planner.strings.LocalStrings
import com.meal.planner.theme.MealPlannerTokens
import kotlinx.coroutines.launch
import mealplanner.shared.generated.resources.Res

private enum class Gesture { TAP, SWIPE_LEFT, SWIPE_RIGHT }

/**
 * One walkthrough page: a screenshot from `composeResources/files/walkthrough/`, and the part of it
 * to point at, as fractions of the screenshot's width and height.
 */
private class WalkthroughStep(
    val screenshot: String,
    val target: Rect,
    val gesture: Gesture,
    val text: (Strings) -> WalkthroughText
)

private val steps = listOf(
    WalkthroughStep("add_meal", Rect(0.0389f, 0.8395f, 0.9611f, 0.8842f), Gesture.TAP) { it.walkthroughAddMeal },
    WalkthroughStep("pick_meal", Rect(0.1991f, 0.4211f, 0.8380f, 0.4627f), Gesture.TAP) { it.walkthroughPickMeal },
    WalkthroughStep("swipe_meal", Rect(0.0389f, 0.3351f, 0.9611f, 0.4298f), Gesture.SWIPE_LEFT) { it.walkthroughRemoveMeal },
    WalkthroughStep("recipes", Rect(0.8380f, 0.8311f, 0.9491f, 0.8838f), Gesture.TAP) { it.walkthroughCreateRecipe },
    WalkthroughStep("recipes", Rect(0.7315f, 0.0395f, 0.8037f, 0.0737f), Gesture.TAP) { it.walkthroughFilterRecipes },
    WalkthroughStep("shopping", Rect(0.0278f, 0.3526f, 0.9722f, 0.3974f), Gesture.TAP) { it.walkthroughCheckItem },
    WalkthroughStep("swipe_left_item", Rect(0f, 0.3526f, 1f, 0.3974f), Gesture.SWIPE_LEFT) { it.walkthroughRemoveItem },
    WalkthroughStep("swipe_right_item", Rect(0f, 0.3526f, 1f, 0.3974f), Gesture.SWIPE_RIGHT) { it.walkthroughGroupItem },
    WalkthroughStep("shopping", Rect(0.9028f, 0.2259f, 0.9954f, 0.2654f), Gesture.TAP) { it.walkthroughRecordPrice },
    WalkthroughStep("pantry_staples", Rect(0.0278f, 0.7873f, 0.9722f, 0.8268f), Gesture.TAP) { it.walkthroughPantryStaples },
    WalkthroughStep("home", Rect(0.8704f, 0.0316f, 0.9454f, 0.0671f), Gesture.TAP) { it.walkthroughReminders }
)

/** Width over height of the bundled screenshots (a 1080x2400 screen without its status bar). */
private const val SCREENSHOT_ASPECT_RATIO = 1080f / 2280f

/**
 * Steps through screenshots of the app, each pointing at the button to tap or the row to swipe.
 * [onFinish] runs when the user skips it or taps the button on the last page.
 */
@Composable
fun WalkthroughScreen(onFinish: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = MealPlannerTokens.spacing
    val strings = LocalStrings.current
    val pagerState = rememberPagerState { steps.size }
    val scope = rememberCoroutineScope()
    val onLastPage = pagerState.currentPage == steps.lastIndex
    val theme = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) "dark" else "light"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.centeredMaxWidth().padding(horizontal = spacing.sm, vertical = spacing.xs),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinish) { Text(strings.walkthroughSkip) }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { index ->
            WalkthroughPage(steps[index], theme, strings)
        }

        Column(
            modifier = Modifier
                .centeredMaxWidth()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            PageIndicator(
                count = steps.size,
                current = pagerState.currentPage,
                description = strings.walkthroughPageOf(pagerState.currentPage + 1, steps.size)
            )
            Button(
                onClick = {
                    if (onLastPage) {
                        onFinish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (onLastPage) strings.walkthroughGetStarted else strings.walkthroughNext)
            }
        }
    }
}

@Composable
private fun WalkthroughPage(step: WalkthroughStep, theme: String, strings: Strings) {
    val spacing = MealPlannerTokens.spacing
    val text = step.text(strings)
    val sideBySide = LocalWideWidth.current || LocalCompactHeight.current

    if (sideBySide) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = spacing.xxl, vertical = spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(spacing.xxl, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                AnnotatedScreenshot(step, theme)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                StepText(text, TextAlign.Start)
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = spacing.xxl, vertical = spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                AnnotatedScreenshot(step, theme)
            }
            StepText(text, TextAlign.Center)
        }
    }
}

@Composable
private fun StepText(text: WalkthroughText, align: TextAlign) {
    val spacing = MealPlannerTokens.spacing
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        Text(
            text = text.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = align,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = text.body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = align,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** The screenshot, dimmed everywhere but the target, with a ring and a tap or swipe hint on it. */
@Composable
private fun AnnotatedScreenshot(step: WalkthroughStep, theme: String) {
    val shape = MaterialTheme.shapes.large
    val accent = MaterialTheme.colorScheme.primary
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1400, easing = LinearEasing), RepeatMode.Restart)
    )

    Box(
        modifier = Modifier
            .aspectRatio(SCREENSHOT_ASPECT_RATIO, matchHeightConstraintsFirst = true)
            .clip(shape)
            .border(MealPlannerTokens.spacing.xxs, MaterialTheme.colorScheme.outlineVariant, shape)
    ) {
        AsyncImage(
            model = Res.getUri("files/walkthrough/${step.screenshot}_$theme.webp"),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            val pad = 6.dp.toPx()
            val edge = 3.dp.toPx()
            val target = Rect(
                left = (step.target.left * size.width - pad).coerceAtLeast(edge),
                top = (step.target.top * size.height - pad).coerceAtLeast(edge),
                right = (step.target.right * size.width + pad).coerceAtMost(size.width - edge),
                bottom = (step.target.bottom * size.height + pad).coerceAtMost(size.height - edge)
            )
            val corner = CornerRadius(minOf(target.height, target.width) / 2f)
            drawRect(Color.Black.copy(alpha = 0.45f))
            drawRoundRect(Color.Transparent, target.topLeft, target.size, corner, blendMode = BlendMode.Clear)
            drawRoundRect(accent, target.topLeft, target.size, corner, style = Stroke(width = 3.dp.toPx()))
            when (step.gesture) {
                Gesture.TAP -> drawTapPulse(target, corner, accent, progress)
                Gesture.SWIPE_LEFT -> drawSwipeHint(target, accent, progress, towardsStart = true)
                Gesture.SWIPE_RIGHT -> drawSwipeHint(target, accent, progress, towardsStart = false)
            }
        }
    }
}

private fun DrawScope.drawTapPulse(target: Rect, corner: CornerRadius, color: Color, progress: Float) {
    val grow = progress * 14.dp.toPx()
    drawRoundRect(
        color = color.copy(alpha = 1f - progress),
        topLeft = Offset(target.left - grow, target.top - grow),
        size = Size(target.width + grow * 2, target.height + grow * 2),
        cornerRadius = CornerRadius(corner.x + grow),
        style = Stroke(width = 2.dp.toPx())
    )
}

/** A fingertip sliding across the target, trailing a line and leading with an arrow. */
private fun DrawScope.drawSwipeHint(target: Rect, color: Color, progress: Float, towardsStart: Boolean) {
    val y = target.center.y
    val from = target.left + target.width * if (towardsStart) 0.8f else 0.2f
    val to = target.left + target.width * if (towardsStart) 0.3f else 0.7f
    val x = from + (to - from) * progress
    val direction = if (towardsStart) -1f else 1f
    val fingertip = 10.dp.toPx()
    val head = 9.dp.toPx()
    val alpha = if (progress < 0.85f) 1f else (1f - progress) / 0.15f
    drawLine(color.copy(alpha = 0.5f * alpha), Offset(from, y), Offset(x, y), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
    drawCircle(Color.White.copy(alpha = alpha), radius = fingertip, center = Offset(x, y))
    drawCircle(color.copy(alpha = alpha), radius = fingertip, center = Offset(x, y), style = Stroke(width = 2.dp.toPx()))
    val base = x + direction * (fingertip + 4.dp.toPx())
    drawPath(
        path = Path().apply {
            moveTo(base + direction * head, y)
            lineTo(base, y - head * 0.8f)
            lineTo(base, y + head * 0.8f)
            close()
        },
        color = color.copy(alpha = alpha)
    )
}

@Composable
private fun PageIndicator(count: Int, current: Int, description: String) {
    val spacing = MealPlannerTokens.spacing
    Row(
        modifier = Modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(spacing.sm)
                    .background(
                        color = if (index == current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
            )
        }
    }
}
