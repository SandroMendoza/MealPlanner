---
name: verify
description: Build, install, and drive this app (com.meal.planner) on the Android emulator via adb to visually confirm a UI/behavioral change actually works end-to-end — not just that it compiles. Use for any change with a runtime surface (new screen, new flow, redesign, nav change) before reporting it done. Pair with kmp-verify (compiles) first; this is the runtime half.
---

# MealPlanner runtime verification (adb + emulator)

This is the project-specific recipe for driving the app on the emulator. It exists because the same mistakes (stale APK, mis-scaled tap coordinates, corrupted screenshots) got rediscovered from scratch across multiple sessions before this was written down.

Assumes an emulator is already running (check with `adb devices`; this machine has no `cmdline-tools`/`avdmanager`, so starting a fresh AVD has to go through the Android Studio GUI — see project memory for AVD setup notes, that's out of scope here).

## 0. Locate adb

Not on PATH by default in this environment. Find it once per session:

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb devices
```

Reuse `$adb` for every call below (PowerShell tool calls don't share shell state between invocations, so redefine it in each command block).

## 1. Build and install the debug APK

```powershell
# (run via Bash tool) ./gradlew :androidApp:assembleDebug -q
$apk = "C:\Users\Ghel\AndroidStudioProjects\MealPlanner\androidApp\build\outputs\apk\debug\androidApp-debug.apk"
& $adb install -r -t "$apk"
& $adb shell am force-stop com.meal.planner
& $adb shell am start -W -n com.meal.planner/.MainActivity
```

**Gotcha — wrong APK path:** `androidApp/build/intermediates/apk/debug/androidApp-debug.apk` also exists and often looks plausible, but it can be a stale copy left over from an earlier build in the same session. It does NOT get overwritten by every `assembleDebug` run the way `outputs/apk/debug/` does. Installing it silently runs old code and looks like a real regression (this cost a full debugging detour once — the "bug" vanished because it was never there). **Always install from `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.** If unsure, check both files' timestamps against your last source edit before installing.

**Gotcha — `-t` flag required:** this debug build is marked test-only; a plain `adb install -r` fails with `INSTALL_FAILED_TEST_ONLY: ... Did you forget to add -t?`. Always pass `-r -t`.

`am start -W` reports `LaunchState` and `TotalTime`. A cold start (`LaunchState: COLD`) taking 3–25s on this machine is normal for this app's dependency graph (Compose Multiplatform + Navigation + SQLDelight + Koin) — don't mistake it for a performance regression. A warm re-foreground is ~250ms.

## 2. Drive the UI — use uiautomator, not eyeballed screenshot coordinates

```powershell
& $adb shell input tap <x> <y>
& $adb shell uiautomator dump /sdcard/ui.xml
& $adb pull /sdcard/ui.xml ./ui.xml
```

Then read `ui.xml` (Grep for `bounds="\[...\]\[...\]"` alongside the `text=`/`content-desc=` you're looking for) to get exact element bounds in raw device pixels, and tap the center of that rect.

**Gotcha — do not estimate tap coordinates from a screenshot.** The `Read` tool reports screenshots as "original 1080x2400, displayed at 900x2000 — multiply by 1.2" (or whatever the scale factor is for the device). Tapping the coordinate you visually read off the *displayed* image, without applying that multiplier, lands on the wrong element — this happened twice in one session (once hit an unrelated button, once appeared to leave a field untouched). `uiautomator dump` bounds are already in raw device pixels with no scale-factor math required — prefer it over screenshot-based guessing every time, not just when a tap seems to miss.

For scrolling: `adb shell input swipe <x1> <y1> <x2> <y2> <durationMs>`.

## 3. Capture screenshots for visual confirmation

```powershell
& $adb shell screencap -p /sdcard/shot.png
& $adb pull /sdcard/shot.png ./shot.png
```

Then view it with the `Read` tool.

**Gotcha — never use `adb exec-out screencap -p > file.png` from the PowerShell tool.** PowerShell's stdout redirection re-encodes the binary PNG stream (observed as UTF-16-ish byte mangling — `Read` reports "unrecognized bytes"), producing a corrupt, unreadable file. Always screenshot to a file *on the device* first, then `adb pull` it — this sidesteps PowerShell's stdout entirely.

**Minor gotcha (noted in project memory, not re-confirmed every session):** `adb exec-out screencap -p` can occasionally return a stale frame even when captured correctly. If a screenshot looks suspiciously unchanged after a navigation/action, cross-check with a fresh `uiautomator dump` (reads the live accessibility tree) before concluding the UI itself is broken.

## 4. Clean up

Screenshots and `ui.xml` dumps pulled into the repo root are scratch files, not deliverables — `rm` them before finishing (`rm -f *.png ui*.xml`) and confirm with `git status --short` that only intended source changes remain staged/modified.

## Quick reference

- Package: `com.meal.planner` · Main activity: `.MainActivity`
- adb: `$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe`
- Correct APK: `androidApp/build/outputs/apk/debug/androidApp-debug.apk`
- DB inspection (favorites/meal-plan persistence bugs): `adb exec-out run-as com.meal.planner cat databases/mealplanner.db > local.db`, then query with the `sqlite3` CLI (see project memory for its install path) — SQLDelight does not auto-migrate existing on-device DBs when new tables/columns are added, so `adb uninstall com.meal.planner` before reinstalling if you've just changed the schema.

## When NOT to use this

- Pure compile/type checks — use `kmp-verify` instead; it's faster and doesn't need the emulator.
- Changes with no runtime surface (docs, build config with no behavioral diff) — nothing to drive.
