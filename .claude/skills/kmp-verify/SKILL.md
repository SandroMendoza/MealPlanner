---
name: kmp-verify
description: Compile this Kotlin Multiplatform project's common and Android targets to verify a change builds cleanly. Use after editing any .kt file under shared/, domain/, data/, or presentation/'s commonMain source sets, before reporting that work as done. Not a substitute for /verify (runtime behavior) — this only confirms compilation.
---

# KMP build verification

This project (`MealPlanner`) is a Kotlin Multiplatform app with modules `shared`, `domain`, `data`, `presentation`, and `androidApp`, plus iOS targets on `shared` (`iosArm64`, `iosSimulatorArm64`). Development happens on Windows, so iOS cannot be compiled locally — don't attempt `:shared:compileKotlinIosArm64` or similar here; flag iOS-specific risk in your summary instead (see below).

## Steps

1. Run both of the following in parallel (they're independent):
   ```
   ./gradlew :shared:compileKotlinMetadata -q
   ./gradlew :androidApp:compileDebugKotlin -q
   ```
   - `:shared:compileKotlinMetadata` compiles `commonMain` in isolation, catching platform-leaking APIs (e.g. accidentally using a JVM-only or Android-only class from shared common code) that an Android-only compile would miss.
   - `:androidApp:compileDebugKotlin` transitively compiles `domain`, `data`, `presentation`, and `shared`'s Android source sets, since `androidApp` depends on all of them. This is the broadest single-command check available on this machine.

2. If either fails, read the full error output (don't truncate), fix the root cause, and re-run just the command(s) that failed. Don't re-run the one that already passed unless your fix touched code it compiles.

3. If a change touches `expect`/`actual` declarations or anything under an `iosMain`/`androidMain`-specific source set, say so explicitly in your summary — e.g. "not verified on iOS; this machine has no Xcode toolchain." Don't claim full cross-platform verification when only common+Android were checked.

4. Report results concisely: which commands ran, pass/fail, and anything unverified (iOS).

## When NOT to use this

- Pure Gradle/config changes with no `.kt` changes — a full build is more appropriate, ask or use judgment.
- UI/behavioral changes that need to be seen running — compilation success doesn't mean the feature works. Pair with manual verification (emulator/run) or the `/verify` skill for that.
