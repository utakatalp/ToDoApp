---
name: write-previews
description: Audit and fill Compose preview coverage across the ToDoApp. Finds every uikit component (TD*) and screen (*Screen.kt) that lacks @TDPreview functions covering all reachable UiState branches, then writes them in a consistent style (Light + Dark via @TDPreview, sample data via the *PreviewData helpers, all wrapped in TDTheme). Invoke when the user says "write previews", "audit previews", "add missing previews", or "preview coverage".
---

# write-previews — Compose Preview Audit & Backfill

## When to invoke

- User asks to add or audit previews for one or more components/screens.
- User asks "where is the X preview?" — call this skill scoped to that file before doing anything else.
- After scaffolding a new screen or component (the `add-screen` / `add-uikit-component` skills should hand off here for the preview block).

This skill is **mandatory** for any newly added uikit component or screen — see CLAUDE.md → "Compose Previews (mandatory for new components & screens)" and the `feedback_previews` memory.

## What "good preview coverage" means here

For every public composable that takes a `UiState` (sealed: `Loading` / `Error` / `Success` / `Empty`), there is **one preview per branch**, plus at least two `Success` variants (populated + empty) where `Success` has list-shaped content. For lower-level components, every visually distinct combination of inputs (selected vs unselected, completed vs pending, with-icon vs without, error-state field, etc.) appears in at least one preview.

Every preview function must:

1. Be `private fun` named `<Component><Variant>Preview` (component) or `<Screen><State>Preview` (screen).
2. Be annotated with **one of**:
   - `@com.todoapp.uikit.previews.TDPreview` — the default (renders Light + Dark, 360dp).
   - `@com.todoapp.uikit.previews.TDPreviewWide` — full-width screens (411dp).
   - `@com.todoapp.uikit.previews.TDPreviewDialog` — modal/dialog content.
   - `@com.todoapp.uikit.previews.TDPreviewForm` — form-heavy screens that should mimic AddTaskSheet density.
   - **Never use raw `@Preview(uiMode = ...)` pairs in new code.** If you find them, migrate.
3. Wrap its body in `TDTheme { ... }` — without it colors and typography don't resolve and the preview is silently broken.
4. Use sample data from existing helpers (`HomePreviewData.successState(...)`, `HomePreviewData.sampleTasks`, `GroupsPreviewProvider`, etc.) when they exist. If a screen needs new sample data, create or extend a `<Feature>PreviewData.kt` object — never inline copy-paste model fields.
5. Compile against the **current** model. If a contract field has been renamed/removed since the preview was last touched, fix the preview, don't ignore the build error.

## Procedure

### 1. Audit (read-only)

```bash
# Components in :uikit — count @TDPreview / @Preview annotations
cd /Users/beratbaran/AndroidStudioProjects/ToDoApp/uikit/src/main/java/com/todoapp/uikit/components
for f in *.kt; do
  c=$(grep -cE "^@TDPreview|^@TDPreviewDialog|^@TDPreviewWide|^@TDPreviewForm|^@TDPreviewNoBg|^@Preview|^@com\.todoapp\.uikit\.previews\." "$f")
  echo "$c  $f"
done | sort -n
```

```bash
# Screens in :app — count previews
find /Users/beratbaran/AndroidStudioProjects/ToDoApp/app/src/main/java/com/todoapp/mobile/ui -name "*Screen.kt" | while read f; do
  c=$(grep -cE "^@TDPreview|^@TDPreviewDialog|^@TDPreviewWide|^@TDPreviewForm|^@TDPreviewNoBg|^@Preview|^@com\.todoapp\.uikit\.previews\." "$f")
  echo "$c  $(basename $f)"
done | sort -n
```

Anything with **0** is a definite gap. Anything with **1** likely missing variants — read the file, look at its `UiState` to see how many branches exist.

### 2. Migrate stale `@Preview` to `@TDPreview`

Files that still use plain `androidx.compose.ui.tooling.preview.Preview` should be migrated. Replace:

```kotlin
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
```

with a single `@com.todoapp.uikit.previews.TDPreview`. Then drop the now-unused imports of `Preview` / `Configuration` / `AndroidUiModes`.

### 3. Add missing branches

Read the relevant `*Contract.kt` to find the `UiState` shape. For each subtype, write a preview function:

```kotlin
@com.todoapp.uikit.previews.TDPreview
@Composable
private fun <Screen>LoadingPreview() {
    TDTheme {
        <Screen>LoadingContent()
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun <Screen>ErrorPreview() {
    TDTheme {
        <Screen>ErrorContent(message = "Something went wrong", onAction = {})
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun <Screen>SuccessPreview() {
    TDTheme {
        <Screen>SuccessContent(uiState = <FeaturePreviewData>.successState(...), onAction = {})
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun <Screen>EmptyPreview() {
    TDTheme {
        <Screen>SuccessContent(uiState = <FeaturePreviewData>.successState(items = emptyList()), onAction = {})
    }
}
```

For a new screen with no PreviewData helper yet, create one alongside the screen (`<Feature>PreviewData.kt`). Mirror `HomePreviewData` — an `object` exposing a `successState(...)` factory plus reusable `sample*` lists.

For components, prefer **composing variants in one preview function** when there are many flags:

```kotlin
@com.todoapp.uikit.previews.TDPreview
@Composable
private fun TDFooPreview() {
    TDTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TDFoo(state = State.Default)
            TDFoo(state = State.Selected)
            TDFoo(state = State.Disabled)
        }
    }
}
```

But split into separate previews when each variant needs distinct sample data or screen sizing.

### 4. Common mistakes to avoid

- **Non-null fields lurking** — e.g. `AlarmSoundsContract.UiState.Success.selectedUri: Uri` is non-null. Use `Uri.EMPTY` for a "nothing selected" preview, not `null`.
- **Sample data containing real user emails/PII** — use generic placeholders.
- **Forgetting `TDTheme {}` wrapper** — every preview body must start with it.
- **Dropping unused imports after migration** — remove `import androidx.compose.ui.tooling.preview.Preview` etc. when no plain `@Preview` remains in the file.
- **Re-using `@Preview` parameters** like `widthDp` or `heightDp` — use `TDPreviewWide` for wide layouts; the height usually sorts itself out.
- **Mocking long-lived state** (e.g. coroutine flows, real ViewModels). Previews should call the *Content composable directly with a constructed `UiState`, never the top-level `*Screen(viewModel)` entry point.

### 5. Verification

After each batch:

```bash
./gradlew :app:assembleDebug :app:ktlintCheck
./gradlew :app:ktlintFormat   # if lint complains about import order
```

Build must be green and lint must be clean before declaring the batch done. Open the modified files in Android Studio and use the Compose preview pane to spot-check one Light + one Dark of each new preview — broken theming silently passes the build.

## Output shape

When done, summarize what changed:

```
Filled preview gaps:
  AlarmSoundsScreen — 0 → 5 (Loading / Error / Empty / Success populated / Success no selection)
  TDCategoryPicker  — 0 → 3 (selected / none-selected / single-option)
  TDLoadingBar      — migrated @Preview → @TDPreview

Migrated to @TDPreview (Light + Dark):
  HomeScreen, CalendarScreen, ActivityScreen, …

Already covered (skipped):
  LoginScreen (5 previews), RegisterScreen (9 previews), GroupSettingsScreen (8 previews)
```
