---
name: kmp-verify
description: Compile this Kotlin Multiplatform project's common and Android targets to verify a change builds cleanly. Use after editing any .kt file under shared/, domain/, data/, or presentation/'s commonMain source sets, before reporting that work as done. Not a substitute for /verify (runtime behavior) — this only confirms compilation.
---

# KMP build verification (MealPlanner)

Follow the global `kmp-compile-check` skill (`~/.claude/skills/kmp-compile-check/SKILL.md`). This file only adds the MealPlanner facts, so the module discovery step can be skipped.

## Project facts

- Modules: `shared`, `domain`, `data`, `presentation`, `androidApp`.
- KMP module with iOS targets: `shared` (`iosArm64`, `iosSimulatorArm64`). Development is on Windows, so iOS can't be compiled here.
- App module: `androidApp`. It depends on all the other modules, so its compile covers them.

## Commands

Run both in parallel:

```
./gradlew :shared:compileKotlinMetadata -q
./gradlew :androidApp:compileDebugKotlin -q
```

Everything else (fixing failures, reporting what stayed unverified on iOS, when not to use this) is as in the global skill.
