---
name: add-uikit-component
description: Scaffold a new TD* Compose component in the :uikit module with theme tokens, preview, and correct param order. Enforces TDTheme-only styling, TD prefix, and (if the component loads images) adds the Coil dependency to uikit/build.gradle.kts. Invoke when the user says "add a new uikit component", "create a TD component", or "scaffold TDFoo".
---

# add-uikit-component — New TD* Component Scaffolding

## Inputs

1. **Component name** — ask if not given. Auto-prefixed with `TD`. Input `Banner` → file `TDBanner.kt` and function `TDBanner`.
2. **One-line purpose** — used in the KDoc.
3. **Loads images?** — yes/no. If yes, we may need to add Coil to `:uikit`.

## File to create

`uikit/src/main/java/com/todoapp/uikit/components/TD<Name>.kt`

## Template

```kotlin
package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.theme.TDTheme
import com.todoapp.uikit.theme.ToDoAppTheme

/**
 * <one-line purpose>.
 */
@Composable
fun TD<Name>(
    modifier: Modifier = Modifier,
    // ...parameters here...
    content: @Composable () -> Unit = {},
) {
    // NOTE: pick a theme color intentionally — do NOT default to `TDTheme.colors.surface`.
    // Choose from the semantic token table in CLAUDE.md §Color Usage (e.g. `background`,
    // `lightPurple`, `lightGreen`, `infoCardBgColor`) based on the component's purpose.
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(TDTheme.colors.background) // TODO: replace with the intended semantic token
            .padding(16.dp),
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun TD<Name>Preview() {
    ToDoAppTheme {
        TD<Name>()
    }
}
```

## Non-negotiable rules

- **Prefix `TD`.** No exceptions. `TDBanner`, never `Banner`.
- **TDTheme tokens only.** `TDTheme.colors.*`, `TDTheme.typography.*`, `TDTheme.icons.*`. Never `Color(0xFF...)`, never `MaterialTheme.colorScheme.*`.
- **Do NOT default to `TDTheme.colors.surface`.** Pick a semantic token deliberately based on the component's purpose — see the color table in CLAUDE.md §Color Usage (e.g. `lightPurple` for session cards, `lightGreen` for focus cards, `infoCardBgColor` for hint cards, `background` for neutral screen surfaces).
- **`Modifier` parameter first**, with `= Modifier` default. Trailing lambda last.
- **Every component has a `@Preview`** private composable at the bottom wrapped in `ToDoAppTheme { ... }`.
- **No Android resource lookups via `LocalContext.current` for text** — take `String` parameters; resolve via `stringResource` at the call site in `:app`.
- **If the component needs strings**, add them to `uikit/src/main/res/values/strings.xml` AND `uikit/src/main/res/values-tr/strings.xml`. This is a separate strings file from `:app`'s.

## If the component loads images

1. Check `uikit/build.gradle.kts` for `implementation(libs.coil.compose)`. If missing:
   ```kotlin
   implementation(libs.coil.compose)
   ```
2. Use `AsyncImage(model = ..., contentDescription = ...)` — Coil is wired app-wide with the auth OkHttpClient, so `BuildConfig.BASE_URL + relativePath` sends the Bearer token automatically.
3. Follow the reusable-avatar pattern in `MemberAvatar` (GroupDetailMembersTab.kt) / `TDFamilyGroupCard` / `AvatarChip`: render `AsyncImage` when URL is non-blank, otherwise fall back to initials/placeholder.

## Verification

1. `./gradlew :uikit:ktlintCheck :uikit:detekt` — must pass.
2. `./gradlew :uikit:assembleDebug` — must build.
3. Show the `@Preview` in Android Studio — visual sanity check.
4. Add a usage site in `:app` as part of the feature work (this skill stops at the component itself).
