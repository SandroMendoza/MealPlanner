---
name: apply-design-tokens
description: Convert a Compose screen in this app from hardcoded dp/Color/FontWeight literals to the shared design-system tokens (shared/src/commonMain/kotlin/com/meal/planner/theme/). Use when building a new screen or editing an existing one, so every screen stays visually consistent and style updates only need to happen in one place.
---

# Apply design tokens to a Compose screen

This project has a global design system under `shared/src/commonMain/kotlin/com/meal/planner/theme/`:
- `Color.kt` — `MealPlannerPalette` (raw tokens) + `MealPlannerColorScheme` (the Material3 `ColorScheme`, already wired into `MealPlannerTheme`)
- `Typography.kt` — `MealPlannerTypography` (full Material3 `Typography`) + `MealPlannerFontSize` (raw sp constants for one-off cases)
- `Spacing.kt` — `MealPlannerSpacing`, `MealPlannerRadius`, `MealPlannerIconSize`, exposed via `CompositionLocal`
- `MealPlannerTheme.kt` — wires all of the above into `MaterialTheme`, exposes `MealPlannerTokens.spacing` / `.radius` / `.iconSize`, and provides `activeFilterChipColors()` for selected-chip styling (see rule 8)

Any screen with raw `.dp` literals, `Color(0x...)` literals, or manual `fontWeight =` overrides on `Text` is out of sync with this system. Bring it into line as follows.

## Conversion rules

1. **Spacing** — replace every `X.dp` used for padding/gaps/sizes with the closest `MealPlannerTokens.spacing.*` value (`xxs`=2, `xs`=4, `sm`=8, `md`=12, `lg`=16, `xl`=20, `xxl`=24, `xxxl`=32, `huge`=40; plus semantic `screenHorizontal`/`screenVertical`/`sectionGap`/`cardPadding`). Add `val spacing = MealPlannerTokens.spacing` near the top of the composable (or inside a private helper composable that needs it) rather than calling `MealPlannerTokens.spacing` repeatedly inline.
2. **Icon/avatar sizes** — replace `.size(NN.dp)` on icon containers/avatars with `MealPlannerTokens.iconSize.{sm,md,lg,xl}` (16/24/32/48dp). If the mockup size doesn't land exactly on one of these, use the nearest token and adjust with `.minus()`/`.plus()` rather than introducing a new raw literal.
3. **Corner radius** — prefer `MaterialTheme.shapes.{extraSmall,small,medium,large,extraLarge}` on `Card`/`Button`/custom `Modifier.background(color, shape)` calls. Use `MealPlannerTokens.radius.*` only when you need a raw `Dp`/`RoundedCornerShape` outside a component that accepts a `Shape` directly (e.g. `MealPlannerTokens.radius.pill` for a fully-rounded custom shape).
4. **Color** — never introduce a new `Color(0x...)` literal in a screen file. Use `MaterialTheme.colorScheme.*` roles (`primary`, `onSurfaceVariant`, `error`, `outlineVariant`, etc.) for anything semantic. If a genuinely new raw color is needed (not covered by any role), add it to `MealPlannerPalette` in `theme/Color.kt` first, then reference it from the screen — don't inline it.
5. **Typography** — never pass `fontWeight = FontWeight.Bold` (or similar) alongside a `MaterialTheme.typography.*` style as a bolt-on override. Instead pick the `MaterialTheme.typography.*` role that already has the right weight baked in (see `Typography.kt` — e.g. `headlineSmall`/`titleLarge`/`titleMedium` are already bold/semibold). If no existing role fits, that's a signal to extend `MealPlannerTypography`, not to override inline.
6. **Container/background colors** — top-level containers (`Scaffold`, screen-root `Column`/`Box`) should set `containerColor = MaterialTheme.colorScheme.background` (for `Scaffold`) or `.background(MaterialTheme.colorScheme.background)` explicitly if the screen doesn't already inherit it, so screens don't silently rely on Compose's default white.
7. List/empty/error states — style with `MaterialTheme.typography.bodyMedium` and `MaterialTheme.colorScheme.onSurfaceVariant` (empty state) or `.error` (error state) rather than default `Text` styling, for consistency across screens.
8. **Active/selected filter chips** — any `FilterChip` representing a live selection (category, meal type, favorites, etc.) must use `colors = activeFilterChipColors()` (from `theme/MealPlannerTheme.kt`): a solid `primary`-green fill with `onPrimary` (white) label, not Material3's default pale `secondaryContainer`. Don't hand-roll `FilterChipDefaults.filterChipColors(...)` with ad hoc colors for a new chip — call the shared helper, and only extend it (not bypass it) if a genuinely new selected-chip look is needed.
9. **Photo list/grid cards** — for recipe-style image cards, follow the pattern in `RecipeGridCard` (`recipes/RecipeScreen.kt`): a `Card` (`shape = MaterialTheme.shapes.large`) containing an image at `Modifier.fillMaxWidth().aspectRatio(1f)` (the card's own clip already rounds the top corners — don't add a second `Modifier.clip(...)` on the image), followed by a `Column` padded with `spacing.sm` holding a `titleSmall` name (`maxLines = 2`, `TextOverflow.Ellipsis`) and a `bodySmall` row of a clock icon (`Icons.Filled.AccessTime`, sized `MealPlannerTokens.iconSize.sm`, tinted `onSurfaceVariant`) plus the time text. Reuse or extend `RecipeGridCard` rather than writing a new card shape from scratch when another list needs photo cards.
10. **Overlay icon buttons on full-bleed images** — on hero-image screens (e.g. a detail screen's header image), floating back/edit/delete/favorite-style buttons should be a circular `IconButton` sized `40.dp` with `colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), contentColor = <role>)`, matching `RecipeDetailOverlayButton` in `recipes/RecipeDetailScreen.kt`.
11. **Nested `Scaffold` insets** — every screen-level `Scaffold` must pass `contentWindowInsets = WindowInsets(0)`, and any `TopAppBar` inside it must pass `windowInsets = WindowInsets(0)`. `AppNavHost`'s outer `Scaffold` already consumes the top status-bar inset by default; a screen's own `Scaffold`/`TopAppBar` reserving it again doubles the inset and leaves a large blank gap at the top of the screen. Apply this to every new screen-level `Scaffold` as it's created — it's a correctness rule, not something to add reactively only when a gap is spotted.
12. **Filter affordance icon** — use `Icons.Filled.FilterAlt` (funnel) for "open filters" actions. Don't use `Icons.Default.Menu` (hamburger — reads as a navigation drawer) or `Icons.Filled.FilterList` (ambiguous bars) for this; `FilterAlt` is the established convention on the Recipes screen.

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
