# Meal Planner

Meal Planner is a meal planning app for Android and iOS. You plan meals for the week,
keep your recipes, build a shopping list from the plan and track what you spend on food.
All data stays on the device: there are no accounts, no ads and no analytics. A backup
can be exported and restored by hand.

It is a Kotlin Multiplatform project with a shared Compose Multiplatform UI.

## Modules

| Module | What it holds |
|---|---|
| [`androidApp`](./androidApp) | The Android entry point (application ID `sandragon.inc.mealplanner`). |
| [`iosApp`](./iosApp) | The iOS entry point, an Xcode project that hosts the shared UI. |
| [`shared`](./shared/src) | The Compose Multiplatform UI: screens, navigation, components and the design tokens in `theme/`. |
| [`presentation`](./presentation/src) | View models and screen state for each feature. |
| [`domain`](./domain/src) | Models, use cases, repository interfaces and the app's strings. No platform code. |
| [`data`](./data/src) | Repository implementations on a SQLDelight database, with Android and iOS drivers. |

`shared` brings the other modules together and Koin wires them up. `presentation` and
`data` both build on `domain`, and `domain` depends on nothing else in the project.

## Building and running

- **Android:** `./gradlew :androidApp:assembleDebug`, or the run configuration in Android
  Studio. The app needs Android 7.0 (API 24) or later.
- **iOS:** open [`iosApp`](./iosApp) in Xcode and run it from there.

## Tests

`./run-tests.sh` runs the unit tests of `domain`, `data` and `presentation` on the JVM,
prints a summary per module, and exits non-zero if anything fails. On Windows, run it from
Git Bash. HTML reports land in `<module>/build/reports/tests/testAndroidHostTest/`.

## Privacy

The privacy policy is in [`docs/privacy-policy.html`](./docs/privacy-policy.html).

## Project tracking

- Notion: https://app.notion.com/p/MealPlanner-41f2582d747381d6ae45c0b80f6f5018
- Jira: https://sandromendoza.atlassian.net/jira/software/projects/MEAL/boards (key `MEAL`)
