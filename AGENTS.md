# Weather — Android

Single-Activity Android weather app built with Kotlin and Jetpack Compose. Data comes from the [Open-Meteo](https://open-meteo.com/en/docs) REST API (no API key).

- Package root: `com.jcbsh.weather`
- Domain vocabulary lives in [GLOSSARY.md](GLOSSARY.md). Read it before naming any class, function, screen or string, and use its terms exactly.

## Stack

| Concern         | Choice                                                         |
|-----------------|----------------------------------------------------------------|
| Platform        | minSdk 26, compileSdk/targetSdk = latest stable, JDK 21        |
| Language        | Kotlin only                                                    |
| UI              | Jetpack Compose + Material 3 (+ Material 3 Adaptive), one `MainActivity`, edge-to-edge |
| Navigation      | Navigation Compose with type-safe `@Serializable` routes       |
| Architecture    | MVVM + Clean Architecture (data / domain / presentation)       |
| Async           | Coroutines + `Flow`; UI state as `StateFlow`                   |
| DI              | Hilt (KSP)                                                     |
| Networking      | Retrofit + kotlinx.serialization + OkHttp                      |
| Persistence     | Room (KSP) for cached Forecasts and Saved Locations, DataStore for Units and prefs |
| Location        | Fused Location Provider                                        |
| Time            | `java.time`                                                    |
| Testing         | JUnit, kotlinx-coroutines-test, Turbine, MockWebServer, Paparazzi, Robolectric |
| Performance     | R8, Baseline Profiles, Macrobenchmark                          |
| Static analysis | Detekt (`detekt-config.yml`), Android Lint, Slack Compose lint checks |
| Build           | Gradle Kotlin DSL, version catalog, convention plugins in `build-logic/`, configuration cache, type-safe project accessors |

Tools that belong to older Android stacks have no place here: Java, XML layouts, Fragments, LiveData, RxJava, Epoxy, kapt, `R.dimen` sizing.

## Commands

```bash
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest                 # or :module:testDebugUnitTest
./gradlew :module:recordPaparazzi
./gradlew :module:verifyPaparazzi
./gradlew detekt                            # or :module:detekt
./gradlew lintDebug
./gradlew :app:generateBaselineProfile
```

## Modules

```
app/                 MainActivity, NavHost, Application, app-level Hilt wiring
feature/<name>/      One user-facing area (forecast, locations, settings): screens, ViewModels, routes
core/network/        Retrofit services, Open-Meteo DTOs, OkHttp setup
core/database/       Room database, DAOs, entities, exported schemas
core/datastore/      DataStore preferences
core/location/       Fused Location Provider wrapper, permission state
core/data/           Repository implementations (combine network + database)
core/domain/         Domain models, repository interfaces, DataResult, use cases (pure Kotlin)
core/common/         Logger, dispatcher qualifiers, shared utilities
core/testing/        Fakes, test rules, JSON fixtures
design/              WeatherTheme, tokens, reusable components, WeatherCondition → string/icon
benchmark/           Macrobenchmark + Baseline Profile generator
build-logic/         Convention plugins (android-library, android-feature, compose, hilt, room, detekt)
```

Dependency rules:
- `feature:*` depends on `core:domain`, `core:common` and `design`, never on another `feature:*` or on data-layer modules directly (Hilt binds implementations in `:app`/`core:data`).
- `core:domain` is pure Kotlin/JVM and depends on nothing else in the project. `core:data` depends on `core:domain`.
- `:app` is the only module that knows every feature; it wires their routes into the `NavHost`.
- Every new module applies a convention plugin from `build-logic/`; every new dependency goes in `gradle/libs.versions.toml`.

Package layout:

```
feature module  com.jcbsh.weather.feature.<name>/
├── presentation/  ViewModels, UiState, Composable screens
├── navigation/    Route + NavGraphBuilder / NavController extensions
└── di/            Hilt modules (if any)

core module     com.jcbsh.weather.core.<name>/
                Flat by concern: model/, repository/, dao/, entity/, dto/, di/ …
```

Data and domain code lives in `core:*`; feature modules hold presentation only.

## Architecture rules

### Data flow
Open-Meteo → `core:network` DTO → mapper → Room entity → repository `Flow<DomainModel>` → (optional use case) → ViewModel `StateFlow<UiState>` → Composable.

- **Offline-first:** repositories expose `Flow` from Room as the single source of truth and refresh from the network into Room. Screens render cached Forecasts immediately, then update.
- DTOs stay inside `core:network`; entities stay inside `core:database`. Map both to domain models in `core:data`.
- One-shot operations (refresh, save) return `DataResult<T>` (sealed `Success` / `Error`, defined in `core:domain`); the ViewModel maps it to UI state. Exceptions stop at the repository.
- Every catch-all (`catch (e: Exception)`, `runCatching`) rethrows `CancellationException` so structured concurrency keeps working.
- Add a use case only when logic is reused across ViewModels or combines several repositories; otherwise the ViewModel calls the repository interface directly.
- Inject `CoroutineDispatcher`s via `core:common` qualifiers so tests can swap them.

### Networking (Open-Meteo)
- Forecast: `https://api.open-meteo.com/v1/forecast`. Request `current`, `hourly`, `daily` fields explicitly, plus `timezone=auto`.
- Geocoding: `https://geocoding-api.open-meteo.com/v1/search`.
- Use `@Serializable` DTOs with `@SerialName` for snake_case fields; configure `Json { ignoreUnknownKeys = true }`.
- Request unit parameters (`temperature_unit`, `wind_speed_unit`, `precipitation_unit`) from the user's Units preference.
- HTTPS only; cleartext traffic stays disabled.

### Time
- Open-Meteo returns times in the Location's local time when `timezone=auto`. Parse them as `LocalDateTime` and pair with the response's `utc_offset_seconds` (store as `ZoneOffset`/`OffsetDateTime`).
- Display a Forecast in the Location's own timezone, independent of the device's timezone.

### Weather Codes
- `core:domain` maps the WMO Weather Code to a `WeatherCondition` enum, in one place.
- `design` maps `WeatherCondition` to a string resource and icon.

### Persistence
- Room databases set `exportSchema = true`; commit the schema JSON under `core/database/schemas/`.
- Every schema version bump ships a `Migration` and a migration test (`MigrationTestHelper`).

### Current Location
- `core:location` exposes current position as a `Flow` plus a permission state.
- Request `ACCESS_COARSE_LOCATION` first and fine only if a feature needs it. Explain why before the system prompt.
- Handle denied and permanently denied states with a path to settings, and fall back to Saved Locations.

### ViewModels and UI
- One ViewModel per screen, `@HiltViewModel`, exposing a single `StateFlow<XxxUiState>` built with `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)`.
- `XxxUiState` is a sealed interface or data class covering Loading / Content / Error / Empty.
- User actions flow in as functions on the ViewModel (`onRefresh()`, `onLocationSelected(id)`).
- Split each screen into `XxxRoute` (gets the ViewModel via `hiltViewModel()`, collects state with `collectAsStateWithLifecycle`) and stateless `XxxScreen(uiState, onAction…)` that previews and tests use directly.
- Read route arguments in the ViewModel with `savedStateHandle.toRoute<XxxRoute>()`; keep transient UI state in `rememberSaveable` so it survives process death.

### Edge-to-edge and adaptive layout
- `MainActivity` calls `enableEdgeToEdge()`. Every screen consumes insets through `Scaffold` padding or `WindowInsets` modifiers.
- Lay out with window size classes. Locations → Forecast uses a list-detail layout (`ListDetailPaneScaffold`) that shows both panes on expanded widths.

### Accessibility
- Every weather icon and icon-only button has a meaningful `contentDescription`; decorative images use `null`.
- Interactive elements are at least 48dp.
- Layouts remain usable at 200% font scale; values read in full by TalkBack (e.g. "18 degrees", not "18°").

### Navigation
- Each feature exposes a `@Serializable` route and `fun NavGraphBuilder.xxxScreen(onNavigate…)` plus `fun NavController.navigateToXxx()`.
- Pass IDs in routes; load full objects from repositories in the destination ViewModel.

### Design system
- Wrap all UI in `WeatherTheme`; use `WeatherThemeForPreview` in `@Preview`s and Paparazzi tests.
- Read colours, typography, spacing and shapes from theme tokens in `design/`.
- Size with `dp` literals in code.
- New shared components start in `design/.../experimental/` annotated with `@ExperimentalWeatherDesignApi`; move to `stable/` once the design is final.
- Put user-facing strings in `strings.xml`, using plurals and locale-aware number formatting.

### Release and performance
- Release builds enable R8 (`isMinifyEnabled`, `isShrinkResources`). Keep rules cover kotlinx.serialization DTOs.
- The `benchmark` module generates a Baseline Profile covering startup and opening a Forecast; regenerate it when those flows change.

### Logging
- Log through the `Logger` interface in `core:common` (Timber-backed in debug, no-op in release).

## Testing

- Every ViewModel, repository, use case and mapper gets unit tests.
- Write hand-written fakes in `core:testing` (`FakeForecastRepository`, `FakeLocationDao`) and use them in place of mocking libraries.
- Test Flows with Turbine and `runTest`; use a `MainDispatcherRule`.
- Test Retrofit services against MockWebServer with recorded Open-Meteo JSON in `core:testing/src/main/resources/`.
- Every screen state (Loading / Content / Error / Empty) gets a Paparazzi snapshot of the stateless `XxxScreen` in light, dark, 200% font scale and an expanded-width device.
- Interaction tests use Compose UI tests on Robolectric.
- Time-dependent code takes a `Clock`; tests pass a fixed one.

## Done checklist

A task is done when, for every module touched:

1. `./gradlew :module:detekt` and `./gradlew :module:lintDebug` pass with zero new issues.
2. `./gradlew :module:testDebugUnitTest` passes, with new tests covering the change.
3. UI changes have updated `@Preview`s and re-recorded, verified Paparazzi snapshots.
4. Room schema changes have an exported schema, a `Migration` and a migration test.
5. New terms are added to [GLOSSARY.md](GLOSSARY.md).

CI (GitHub Actions) runs the same commands on every pull request.

## Boundaries

- Keep secrets out of git: signing keys and tokens live in `local.properties` / environment variables.
- Failing tests get fixed or flagged to the human; ask before deleting or `@Ignore`-ing any test.
- Ask before adding a new third-party library.
