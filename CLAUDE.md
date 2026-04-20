# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :app:test
./gradlew :uikit:test

# Run a single test class
./gradlew :app:test --tests "com.todoapp.mobile.SomeTest"

# Lint & code quality
./gradlew ktlintCheck       # Check formatting
./gradlew ktlintFormat      # Auto-fix formatting
./gradlew detekt            # Static analysis
./gradlew detektMain        # Static analysis on main sources only

# Install on connected device
./gradlew installDebug
```

## Module Structure

The project has two Gradle modules:

- **`:app`** — Application module (`com.todoapp.mobile`). Contains all features, navigation, DI, data, and domain
  layers.
- **`:uikit`** — Library module (`com.example.uikit`). All reusable Compose components (prefixed `TD*`) and the design
  system live here. `app` depends on `uikit`.

## Architecture

### Clean Architecture + MVI

The app follows clean architecture with three layers inside `:app`:

**Domain** (`domain/`) — Interfaces and models only. No Android dependencies.

- Repository interfaces, use cases, domain models, preferences interfaces.

**Data** (`data/`) — Implements domain interfaces.

- `source/local/` — Room database (`AppDatabase`), DAOs, local data sources.
- `source/remote/` — Retrofit APIs (`ToDoApi`, `TodoAuthApi`), auth interceptor, token refresh authenticator.
- `repository/` — Repository implementations, `DataStoreHelper`, preferences implementations.

**Presentation** (`ui/`) — MVI pattern. Each screen folder contains exactly three files:

- `*Contract.kt` — Defines `UiState` (sealed: Loading/Success/Error), `UiAction` (user events), and `UiEffect` (one-time
  effects like navigation or toasts).
- `*ViewModel.kt` — Annotated `@HiltViewModel`. Holds `MutableStateFlow<UiState>` and `Channel<UiEffect>`. Processes
  `UiAction` via `onAction(action: UiAction)`.
- `*Screen.kt` — Composable. Collects state, delegates events to `onAction`. Handles multiple `UiState` branches.

### Navigation

- `navigation/Screen.kt` — `@Serializable` sealed interface defining all destinations.
- `navigation/NavGraph.kt` — Compose Navigation graph; each route calls its screen composable.
- `navigation/AppDestination.kt` — Metadata (icon, label) for bottom nav items.
- Navigation effects are emitted via `Channel<NavigationEffect>` in ViewModels and collected in `MainActivity`.

### Dependency Injection

Hilt modules in `di/`:

- `LocalStorageModule` — Room database, DAOs, DataStore, encrypted SharedPreferences, `GoogleSignInManager`,
  `AuthTokenManager`.
- `NetworkModule` — Retrofit instance (base URL: `https://api.candroid.dev/todos/`), OkHttpClient with auth interceptor
  and token refresh authenticator.
- `RepositoryModule` — `@Binds` abstract module wiring all domain interfaces to their data implementations.
- `AlarmManagerModule` / `NotificationServiceModule` — Alarm scheduler and notification service singletons.

### UIKit Design System

All shared UI components are in `uikit/src/main/java/com/todoapp/uikit/components/` and follow the `TD*` naming
convention. Theming is in `uikit/.../theme/` — use `TDTheme.colors`, `TDTheme.typography`, and `TDTheme.icons` inside
composables. Never hardcode colors or text styles.

### Color Usage

**Always use `TDTheme.colors.*` — never `Color(0xFF...)`** literals in composables. All colors are defined in
`uikit/src/main/java/com/todoapp/uikit/theme/Color.kt` and support both light and dark mode automatically.

Semantic color guide:

| Token | Light | Use for |
|---|---|---|
| `background` | `#F8F9FC` | Screen backgrounds |
| `onBackground` | `#090E23` | Primary text |
| `surface` | `#FFFAF0` | Card/sheet backgrounds |
| `purple` / `primary` | `#4566EC` | Primary actions, active state |
| `lightPurple` | `#A9BAFF` | Card backgrounds (session/structure) |
| `darkPurple` | `#1C3082` | Deep accent |
| `bgColorPurple` | `#EFF2FF` | Subtle purple-tinted surfaces |
| `lightGreen` | `#E8F5E9` | Card background (focus/productive) |
| `darkGreen` | `#2E7D32` | Icon/text tint on green cards |
| `lightOrange` | `#FFE2CD` | Card background (short break / warm) |
| `orange` | `#EF8829` | Icon/text tint on orange cards |
| `lightPending` | `#EDF4FF` | Card background (long break / calm) |
| `darkPending` | `#3D6A9E` | Icon/text tint on blue cards |
| `pendingGray` | `#7A9CC6` | Secondary icons, muted values |
| `lightRed` | `#FFE6E7` | Error/destructive card backgrounds |
| `crossRed` | `#B2282D` | Error text/icons |
| `infoCardBgColor` | `#EEF2FF` | Info/hint card backgrounds (`TDInfoCard`) |
| `lightYellow` | `#FFF8E1` | Warning surfaces |
| `white` | `#FFFAF0` | Explicit white (e.g. shadow colors) |
| `gray` | `#717171` | Disabled/placeholder text |
| `lightGray` | `#C0C0C0` | Dividers, borders |

### Icons & Drawables

**Always check project drawables before using Material default icons.** The project has custom icons in:

- `uikit/src/main/res/drawable/` — shared icons used across screens (e.g. `ic_delete.xml`, `ic_arrow_back.xml`,
  `ic_error.xml`)
- `app/src/main/res/drawable/` — app-specific icons (e.g. navigation tab icons)
- `app/src/main/res/drawable-nodpi/` and `uikit/src/main/res/drawable-nodpi/` — illustration assets

Use `painterResource(com.example.uikit.R.drawable.ic_*)` to reference uikit drawables from the app module. Only fall
back to `Icons.Default.*` or `Icons.Filled.*` when no suitable custom drawable exists.

### Key Technologies

| Area        | Library                                                           |
|-------------|-------------------------------------------------------------------|
| UI          | Jetpack Compose + Material3                                       |
| DI          | Hilt                                                              |
| Navigation  | Compose Navigation (type-safe, serializable routes)               |
| Local DB    | Room (encrypted SQLite)                                           |
| Preferences | DataStore + encrypted SharedPreferences                           |
| Network     | Retrofit + OkHttp + Kotlinx Serialization                         |
| Background  | WorkManager (`SyncWorker`, `FetchTasksWorker`)                    |
| Push        | Firebase Cloud Messaging                                          |
| Auth        | Google Sign-In, Facebook SDK, Biometric                           |
| Logging     | Timber                                                            |
| Code style  | KtLint + Detekt (configs at `app/detekt.yml`, `uikit/detekt.yml`) |

## Localization

**All user-visible text must use string resources — never hardcode strings in Kotlin or Composable code.**

- Every label, button text, title, error message, toast, dialog text, hint, or status string must be defined in `app/src/main/res/values/strings.xml` and referenced with `stringResource(R.string.*)`.
- **Always update both** `values/strings.xml` (English) **and** `values-tr/strings.xml` (Turkish) in the same change. Never add strings to one without the other.
- When adding a new screen, component, or feature, add all required strings to both files as part of the same change. Do not defer string extraction.
- String keys must be `snake_case` and descriptive: `remove_from_group`, `member_profile`, `invite_sent`.
- Never use raw string literals in `UiEffect.ShowToast`, ViewModel error messages, or Composable text params. Define the string in XML and pass the resource ID or resolved string from the ViewModel (via `context.getString`).
- The only exception is logging (Timber) and crash/debug-only messages — those may use raw strings.

## Adding a New Screen

1. Create a new package under `app/.../ui/<featurename>/`.
2. Add `<Feature>Contract.kt`, `<Feature>ViewModel.kt`, `<Feature>Screen.kt` following the MVI pattern.
3. Add a `@Serializable` data object/class to `Screen.kt`.
4. Register the route in `NavGraph.kt`.
5. Inject the ViewModel with `hiltViewModel()` in the screen's composable call site.
6. **Always use `TDTopBar` — never build a custom top bar.** Add an `AppDestination` entry for the new screen in `AppDestination.kt` (with a title string resource, `icon = null`, `selectedIcon = null`) and add it to `AppDestination.topBarItems`. The shared `ShowTopBar` composable in the Scaffold then renders the correct back-arrow + title automatically. Do not place any back button or title row inside the screen composable itself.
7. **Add all user-visible strings to `strings.xml`** — screen title, button labels, error messages, toasts, dialog text. See the Localization section above.

## Kotlin & Compose Code Style

- **Naming**: PascalCase (classes/functions), camelCase (variables/params)
- **Composables**: No trailing underscores, trailing lambdas mandatory
- **StateFlow**: Always private `MutableStateFlow<>`, public `StateFlow<>`
- **LaunchedEffect/Collections**: Use for side effects only, never state management
- **Imports**: Keep organized, no wildcard imports
- **Line length**: Max 120 chars (KtLint enforces)
- **Threshold**: A single `.kt` UI file should not exceed ~300 lines. When it does, split it.
- **How to split** (Compose screens): Extract self-contained composables into their own files in the same package. Good
  candidates:
    - Repeating/reusable sections (e.g. `HomeFabMenu.kt`, `HomeTaskList.kt`)
    - Bottom sheet / dialog content (e.g. `AddTaskSheet.kt`)
    - The main screen orchestrator (`HomeContent.kt`) separate from the entry point (`HomeScreen.kt`)
- **Naming**: New files follow the screen name prefix — `HomeTaskList.kt`, `HomeFabMenu.kt`, not generic names like
  `Components.kt`.
- **Visibility**: Composables moved to new files within the same package can drop `private`; use `internal` if they
  should not be visible outside the feature package.
- **What stays together**: `*Contract.kt`, `*ViewModel.kt`, and the screen entry point (`*Screen.kt`) always remain as
  the three core files. Extracted composables are additions, not replacements.

## ViewModel & Contract Templates

### ViewModel Template

```kotlin
@HiltViewModel
class XyzViewModel @Inject constructor(
    private val useCase: XyzUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<XyzContract.UiState>(XyzContract.UiState.Loading)
    val state: StateFlow<XyzContract.UiState> = _state.asStateFlow()

    private val _effect = Channel<XyzContract.UiEffect>()
    val effect: Flow<XyzContract.UiEffect> = _effect.receiveAsFlow()

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    fun onAction(action: XyzContract.UiAction) {
        when (action) {
            // handle actions
            // navigate: _navEffect.trySend(NavigationEffect.Navigate(Screen.SomeScreen))
            // go back:  _navEffect.trySend(NavigationEffect.Back)
        }
    }
}
```

### Contract Template

```kotlin
object XyzContract {
    sealed class UiState {
        data object Loading : UiState()
        data class Success(val data: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class UiAction {
        data object Load : UiAction()
    }

    sealed class UiEffect {
        data class ShowToast(val message: String) : UiEffect()
    }
}
```

## ❌ Anti-Patterns (Avoid!)

- **Don't** use `remember { mutableStateOf() }` for app state (use ViewModel)
- **Don't** hardcode colors/text sizes (use TDTheme)
- **Don't** hardcode user-visible strings — always use `stringResource` / `strings.xml`
- **Don't** make Composables with side effects in body (use LaunchedEffect)
- **Don't** pass ViewModel as @Composable param (inject with hiltViewModel())
- **Don't** use mutable objects as default params in Composables
- **Don't** forget `.asStateFlow()` after `MutableStateFlow`

## Testing Conventions

- **Unit Tests**: `*Test.kt` in same package as source
- **ViewModel Tests**: Mock repositories, test `onAction()` → state changes
- **Composable Tests**: Use `ComposeTestRule`, test interactions not styling
- **Test Location**: `app/src/test/` (unit) vs `app/src/androidTest/` (instrumented)
