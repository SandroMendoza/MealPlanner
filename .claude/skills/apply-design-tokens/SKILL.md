---
name: apply-design-tokens
description: Convert a Compose screen in this app from hardcoded dp/Color/FontWeight literals to the shared design-system tokens (shared/src/commonMain/kotlin/com/meal/planner/theme/). Use when building a new screen or editing an existing one, so every screen stays visually consistent and style updates only need to happen in one place.
---

# Apply design tokens to a Compose screen

This project has a global design system under `shared/src/commonMain/kotlin/com/meal/planner/theme/`:
- `Color.kt` — `MealPlannerPalette` (raw tokens) + `MealPlannerColorScheme` (the Material3 `ColorScheme`, already wired into `MealPlannerTheme`)
- `Typography.kt` — `MealPlannerTypography` (full Material3 `Typography`) + `MealPlannerFontSize` (raw sp constants for one-off cases)
- `Spacing.kt` — `MealPlannerSpacing`, `MealPlannerRadius`, `MealPlannerIconSize`, exposed via `CompositionLocal`
- `MealPlannerTheme.kt` — wires all of the above into `MaterialTheme` and exposes `MealPlannerTokens.spacing` / `.radius` / `.iconSize`

Any screen with raw `.dp` literals, `Color(0x...)` literals, or manual `fontWeight =` overrides on `Text` is out of sync with this system. Bring it into line as follows.

## Conversion rules

1. **Spacing** — replace every `X.dp` used for padding/gaps/sizes with the closest `MealPlannerTokens.spacing.*` value (`xxs`=2, `xs`=4, `sm`=8, `md`=12, `lg`=16, `xl`=20, `xxl`=24, `xxxl`=32, `huge`=40; plus semantic `screenHorizontal`/`screenVertical`/`sectionGap`/`cardPadding`). Add `val spacing = MealPlannerTokens.spacing` near the top of the composable (or inside a private helper composable that needs it) rather than calling `MealPlannerTokens.spacing` repeatedly inline.
2. **Icon/avatar sizes** — replace `.size(NN.dp)` on icon containers/avatars with `MealPlannerTokens.iconSize.{sm,md,lg,xl}` (16/24/32/48dp). If the mockup size doesn't land exactly on one of these, use the nearest token and adjust with `.minus()`/`.plus()` rather than introducing a new raw literal.
3. **Corner radius** — prefer `MaterialTheme.shapes.{extraSmall,small,medium,large,extraLarge}` on `Card`/`Button`/custom `Modifier.background(color, shape)` calls. Use `MealPlannerTokens.radius.*` only when you need a raw `Dp`/`RoundedCornerShape` outside a component that accepts a `Shape` directly (e.g. `MealPlannerTokens.radius.pill` for a fully-rounded custom shape).
4. **Color** — never introduce a new `Color(0x...)` literal in a screen file. Use `MaterialTheme.colorScheme.*` roles (`primary`, `onSurfaceVariant`, `error`, `outlineVariant`, etc.) for anything semantic. If a genuinely new raw color is needed (not covered by any role), add it to `MealPlannerPalette` in `theme/Color.kt` first, then reference it from the screen — don't inline it.
5. **Typography** — never pass `fontWeight = FontWeight.Bold` (or similar) alongside a `MaterialTheme.typography.*` style as a bolt-on override. Instead pick the `MaterialTheme.typography.*` role that already has the right weight baked in (see `Typography.kt` — e.g. `headlineSmall`/`titleLarge`/`titleMedium` are already bold/semibold). If no existing role fits, that's a signal to extend `MealPlannerTypography`, not to override inline.
6. **Container/background colors** — top-level containers (`Scaffold`, screen-root `Column`/`Box`) should set `containerColor = MaterialTheme.colorScheme.background` (for `Scaffold`) or `.background(MaterialTheme.colorScheme.background)` explicitly if the screen doesn't already inherit it, so screens don't silently rely on Compose's default white.
7. List/empty/error states — style with `MaterialTheme.typography.bodyMedium` and `MaterialTheme.colorScheme.onSurfaceVariant` (empty state) or `.error` (error state) rather than default `Text` styling, for consistency across screens.

## Process

1. Read the target screen file fully before editing.
2. Add `import com.meal.planner.theme.MealPlannerTokens` (and `MaterialTheme` if not already imported).
3. Apply the conversion rules above. Prefer `Edit` over rewriting the whole file unless most of the file changes.
4. Remove now-unused imports (e.g. `androidx.compose.ui.text.font.FontWeight` if no raw override remains, `androidx.compose.ui.unit.dp` if no raw `Dp` math remains).
5. Run the `kmp-verify` skill (or at minimum `./gradlew :androidApp:compileDebugKotlin -q`) to confirm the screen still compiles.
6. If the mockup calls for a visual element that has no token yet (a new color, a new type scale, a new spacing value), extend the relevant `theme/*.kt` file first — don't add a one-off literal to the screen to route around it.

## When NOT to use this

- Don't invent new semantic color roles or typography styles speculatively "just in case" — only add to `theme/*.kt` when a screen actually needs it.
- Don't restructure a screen's layout/components as part of this pass unless asked — this skill is about token usage, not redesign. Structural gaps (e.g. "this screen needs a day-selector strip it doesn't have") belong in the roadmap, not this skill.
