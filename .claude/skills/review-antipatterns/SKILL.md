---
name: review-antipatterns
description: Scan the currently changed Kotlin files in the ToDoApp for CLAUDE.md anti-patterns — hardcoded colors/strings, Activity Context leaks in ViewModels, missing asStateFlow(), unrecycled Bitmaps, singleton scopes without shutdown, custom top bars, and more. Read-only review. Invoke when the user says "review my changes", "check anti-patterns", or before pushing a PR.
---

# review-antipatterns — CLAUDE.md Compliance Review

Read-only. Reports findings, does not auto-fix.

## Scope

By default: files changed on the current branch vs `main`. Use `git diff --name-only main...HEAD` plus unstaged/staged changes from `git status --porcelain`.

The user may override scope: "review this file", "review all of :app", etc.

## Rules to enforce (each from CLAUDE.md)

For each rule: the regex/heuristic, the violation message, and which files to scan.

### 1. Hardcoded color literals in Composables
- **Pattern:** `Color\(0x[0-9A-Fa-f]{6,8}\)` in any `.kt` file under `app/src/main/java` or `uikit/src/main/java`.
- **Message:** "Use `TDTheme.colors.*` — never `Color(0xFF...)` literals in composables. See CLAUDE.md §Color Usage for the token table."
- **Exception:** Files under `uikit/.../theme/` (Color.kt definitions).

### 2. Raw string literals in user-visible ViewModel/Composable paths
- **Patterns:**
  - `ShowToast\s*\(\s*"` — toast with raw string.
  - `Text\s*\(\s*"[A-Za-z]` — Compose `Text("…")` with literal (not `stringResource`).
  - `UiState\.Error\s*\(\s*"` — error message as raw string from ViewModel.
- **Message:** "User-visible strings must use `stringResource(R.string.*)`. Add the key to BOTH `values/strings.xml` and `values-tr/strings.xml`."
- **Exception:** Timber/log lines (`Timber\.(d|e|i|w|v)\(`), file paths, URLs containing `://`.

### 3. Activity Context in a ViewModel
- **Pattern:** In any file tagged `@HiltViewModel`, look for a constructor parameter `context: Context` WITHOUT the `@ApplicationContext` annotation on the same line or preceding line.
- **Message:** "Activity Context in `@HiltViewModel` leaks on config change. Use `@ApplicationContext private val context: Context` or pass Context as a method parameter."

### 4. Public MutableStateFlow / missing asStateFlow()
- **Pattern:** `val\s+\w+\s*:\s*MutableStateFlow` (public, no `private` modifier) OR a `MutableStateFlow` whose matching public `val` exposes it without `.asStateFlow()`.
- **Message:** "ViewModel state must be `private val _x = MutableStateFlow(...)` + public `val x: StateFlow<...> = _x.asStateFlow()`."

### 5. Custom top bar
- **Pattern:** `TopAppBar\s*\(` or `CenterAlignedTopAppBar\s*\(` outside of `uikit/.../TDTopBar.kt`.
- **Message:** "Never build a custom top bar. Add an `AppDestination` entry and `ShowTopBar` handles it. See CLAUDE.md §Adding a New Screen #6."

### 6. remember { } holding a Bitmap without DisposableEffect
- **Pattern:** Look for `remember\s*\{[^}]*Bitmap` or `remember\s*\{[^}]*\.decodeBitmap` or `ImageBitmap`. If found, grep the same composable function body for `DisposableEffect`. If none → flag.
- **Message:** "Decoded Bitmap in `remember` leaks native memory. Wrap in `DisposableEffect(bitmap) { onDispose { bitmap?.asAndroidBitmap()?.recycle() } }`, or use `AsyncImage` with a ByteArray/URL model."

### 7. Singleton CoroutineScope without shutdown
- **Pattern:** File contains both `@Singleton` and `CoroutineScope(SupervisorJob()`. If so, grep same file for `cancel()` or `fun shutdown`. If neither → flag.
- **Message:** "Singleton scopes leak unless cancelled. Add `fun shutdown() { scope.cancel() }` and invoke from `Application.onDestroy()` via `ProcessLifecycleOwner`."

### 8. mutableStateOf holding app state
- **Pattern:** `remember\s*\{\s*mutableStateOf` in a Composable that's a screen entry point (grep for `fun \w+Screen\b`). Heuristic only — flag as "review".
- **Message:** "Screen-level state should live in a ViewModel, not `remember { mutableStateOf() }`."

### 9. mutableStateOf/default-arg hazards
- **Pattern:** `@Composable\s+fun\s+\w+\([^)]*=\s*mutableListOf\(` or similar mutable default.
- **Message:** "Mutable object as composable default param causes recomposition weirdness. Use `emptyList()` / `persistentListOf()`."

### 10. Hardcoded base URL / host
- **Pattern:** `https?://[a-zA-Z0-9.-]+` in Kotlin files, EXCEPT `local.properties`, `build.gradle.kts`, test fixtures, or files that reference `BuildConfig.BASE_URL`.
- **Message:** "Base URL must come from `BuildConfig.BASE_URL`, not hardcoded. See CLAUDE.md §Networking."

## Procedure

1. Determine scope: `git diff --name-only main...HEAD` + staged/unstaged. Filter to `.kt` under `app/src/main` or `uikit/src/main`.
2. For each rule, scan. Collect findings as `file:line — message`.
3. Group output by rule. Count findings per rule at the top:

   ```
   1. Hardcoded colors:        0
   2. Raw user-visible strings: 2
   3. Activity Context in VM:   0
   4. MutableStateFlow leak:    1
   5. Custom top bar:           0
   6. Unrecycled Bitmap:        0
   7. Singleton scope leak:     0
   8. remember mutableStateOf:  0
   9. Mutable default arg:      0
   10. Hardcoded URL:           0
   ```

4. Print findings in order. Each finding: `path/to/File.kt:123 — <message excerpt>` followed by the matched line.
5. If all zero, state "No anti-pattern hits in changed files." and stop.
6. End with: "Want me to fix these? I'll address each violation in order."

## What this skill does NOT do

- Does not run ktlint/detekt — those have their own Gradle tasks. Mention them at the end if any Kotlin file changed.
- Does not judge style/formatting (that's ktlint).
- Does not auto-fix. Ask first.
