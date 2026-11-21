---
name: verify
description: Build, install, and drive this app (sandragon.inc.mealplanner) on the Android emulator via adb to visually confirm a UI/behavioral change actually works end-to-end — not just that it compiles. Use for any change with a runtime surface (new screen, new flow, redesign, nav change) before reporting it done. Pair with kmp-verify (compiles) first; this is the runtime half.
---

# MealPlanner runtime verification (adb + emulator)

Follow the global `android-emulator-check` skill (`~/.claude/skills/android-emulator-check/SKILL.md`). This file only adds the MealPlanner facts, so the project-facts step can be skipped.

## Project facts

- Application ID: `sandragon.inc.mealplanner`
- Launcher activity: `com.meal.planner.MainActivity`. The Kotlin namespace is `com.meal.planner`, which differs from the application ID, so always use the full component name: `sandragon.inc.mealplanner/com.meal.planner.MainActivity`.
- adb: `$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe`
- Build: `./gradlew :androidApp:assembleDebug -q`
- APK: `androidApp/build/outputs/apk/debug/androidApp-debug.apk` (never the copy under `build/intermediates/`)
- This machine has no `cmdline-tools`/`avdmanager`, so a new AVD has to be started from the Android Studio GUI.

## Launch

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb install -r -t "androidApp\build\outputs\apk\debug\androidApp-debug.apk"
& $adb shell am force-stop sandragon.inc.mealplanner
& $adb shell am start -W -n sandragon.inc.mealplanner/com.meal.planner.MainActivity
```

A cold start (`LaunchState: COLD`) of 3–25 s is normal on this machine for this app's dependency graph (Compose Multiplatform, Navigation, SQLDelight, Koin). A warm re-foreground takes about 250 ms.

## Database

```bash
adb exec-out run-as sandragon.inc.mealplanner cat databases/mealplanner.db > "<scratch-dir>/local.db"
```

Query it with the `sqlite3` CLI (install path in project memory). SQLDelight doesn't migrate an existing on-device database when tables or columns are added, so run `adb uninstall sandragon.inc.mealplanner` before reinstalling after a schema change.
