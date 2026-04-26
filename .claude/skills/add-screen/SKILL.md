---
name: add-screen
description: Scaffold a new MVI screen in the ToDoApp end-to-end. Creates Contract/ViewModel/Screen files under app/.../ui/<name>/, registers the route in Screen.kt + NavGraph.kt + AppDestination.kt, and adds title string to both values/strings.xml and values-tr/strings.xml. Invoke when the user says "add a new screen", "scaffold screen X", or "create a new feature screen".
---

# add-screen — Full MVI Screen Scaffolding

## Inputs to collect (use AskUserQuestion if missing)

1. **Feature name** — lowercase, no spaces (e.g. `editprofile`). Used for:
   - Package: `com.todoapp.mobile.ui.<featurename>`
   - Class prefix: PascalCase (e.g. `EditProfile`)
2. **Screen title** (EN) — displayed in `TDTopBar`.
3. **Screen title (TR)** — Turkish translation. If omitted, ask; never skip.
4. **Does it take route parameters?** — if yes, collect name + type (e.g. `taskId: Long`).

## All files to touch (NONE can be skipped)

| # | File | Action |
|---|------|--------|
| 1 | `app/src/main/java/com/todoapp/mobile/ui/<name>/<Name>Contract.kt` | Create |
| 2 | `app/src/main/java/com/todoapp/mobile/ui/<name>/<Name>ViewModel.kt` | Create |
| 3 | `app/src/main/java/com/todoapp/mobile/ui/<name>/<Name>Screen.kt` | Create |
| 4 | `app/src/main/java/com/todoapp/mobile/navigation/Screen.kt` | Append `@Serializable data object/class` |
| 5 | `app/src/main/java/com/todoapp/mobile/navigation/NavGraph.kt` | Append `composable<Screen.<Name>> { ... }` |
| 6 | `app/src/main/java/com/todoapp/mobile/navigation/AppDestination.kt` | Add destination + add to `topBarItems` list |
| 7 | `app/src/main/res/values/strings.xml` | Add `<name>_title` string |
| 8 | `app/src/main/res/values-tr/strings.xml` | Add same key with TR value |

## Templates

### Contract.kt
```kotlin
package com.todoapp.mobile.ui.<name>

object <Name>Contract {
    sealed interface UiState {
        data object Loading : UiState
        data class Success(val data: String = "") : UiState
        data class Error(val message: String) : UiState
    }

    sealed interface UiAction {
        data object OnBack : UiAction
        data object Load : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(val messageRes: Int) : UiEffect
    }
}
```

### ViewModel.kt
```kotlin
package com.todoapp.mobile.ui.<name>

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.<name>.<Name>Contract.UiAction
import com.todoapp.mobile.ui.<name>.<Name>Contract.UiEffect
import com.todoapp.mobile.ui.<name>.<Name>Contract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class <Name>ViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnBack -> _navEffect.trySend(NavigationEffect.Back)
            is UiAction.Load -> Unit
        }
    }
}
```

### Screen.kt
```kotlin
package com.todoapp.mobile.ui.<name>

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.PaddingValues
import com.todoapp.mobile.ui.<name>.<Name>Contract.UiAction
import com.todoapp.mobile.ui.<name>.<Name>Contract.UiEffect
import com.todoapp.mobile.ui.<name>.<Name>Contract.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import android.widget.Toast

@Composable
fun <Name>Screen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        uiEffect.collectLatest { effect ->
            when (effect) {
                is UiEffect.ShowToast ->
                    Toast.makeText(context, context.getString(effect.messageRes), Toast.LENGTH_SHORT).show()
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
        when (uiState) {
            is UiState.Loading -> Unit // TODO: TDLoadingBar
            is UiState.Success -> Unit // TODO: render
            is UiState.Error -> Unit   // TODO: error state
        }
    }
}
```

### Screen.kt entry (navigation)
```kotlin
@Serializable
data object <Name> : Screen
```
(or `data class` if it has route params)

### NavGraph.kt entry
```kotlin
composable<Screen.<Name>> {
    val viewModel: <Name>ViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationEffectController(viewModel.navEffect)
    <Name>Screen(
        uiState = uiState,
        uiEffect = viewModel.uiEffect,
        onAction = viewModel::onAction,
    )
}
```

### AppDestination.kt entry
```kotlin
data object <Name> : AppDestination(
    title = R.string.<name>_title,
    route = Screen.<Name>::class.qualifiedName!!,
    icon = null,
    selectedIcon = null,
)
```
Then add `<Name>` to the `topBarItems` list in the companion object.

### strings.xml entries (BOTH files)
```xml
<!-- values/strings.xml -->
<string name="<name>_title"><EN title></string>

<!-- values-tr/strings.xml -->
<string name="<name>_title"><TR title></string>
```

## Non-negotiable rules

- **Never build a custom top bar.** `TDTopBar` is rendered automatically from `AppDestination` — that's why step 6 is required.
- **Never hardcode the title string.** Use `R.string.<name>_title` in the AppDestination.
- **Both strings.xml files change in the same turn.** If the user forgets TR, ask and wait.
- **The three-file MVI contract stays three files.** Extracted helper composables go in the same package with a feature-prefixed name (e.g. `<Name>List.kt`), not in a shared `components/` dir.
- **Don't inject Activity `Context` into the ViewModel.** If you need `Context`, use `@ApplicationContext`.

## Verification

After scaffolding:
1. `./gradlew :app:ktlintCheck :app:detekt` — must pass.
2. `./gradlew :app:assembleDebug` — must build.
3. Report: "Added `<Name>Screen`. Navigate via `Screen.<Name>` from any NavigationEffect.Navigate."
