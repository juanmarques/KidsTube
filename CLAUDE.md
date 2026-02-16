# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build specific module
./gradlew :feature:feature-player:assembleDebug

# Compile Kotlin only (faster verification)
./gradlew :feature:feature-player:compileDebugKotlin

# Clean build
./gradlew clean assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Install on specific device (when multiple connected)
adb -s <DEVICE_ID> install -r app/build/outputs/apk/debug/app-debug.apk
```

**Note**: If using GraalVM JDK and encountering `jlink` errors, switch to Temurin:
```bash
JAVA_HOME=/path/to/temurin-21 ./gradlew assembleDebug
```

There are no tests in this project yet. The version catalog (`gradle/libs.versions.toml`) includes test dependencies (JUnit, MockK, Turbine, coroutines-test) but no test files exist.

## Project Architecture

KidsTube is a toddler-friendly YouTube viewing app using Clean Architecture with a multi-module structure. Kotlin 2.0, Java 17, compileSdk/targetSdk 35, minSdk 26.

### Module Dependency Flow

```
app
 ├── feature-* (all feature modules)
 └── core-* (all core modules)

feature-* modules
 ├── core-domain (use cases, models)
 ├── core-ui (shared components)
 └── core-common (coroutines)

core-data
 ├── core-domain
 ├── core-network
 ├── core-database
 └── core-datastore

core-network, core-database, core-datastore
 └── core-domain (models only)
```

Feature modules do NOT depend on `core-data` — they only access data through `core-domain` use cases. The `app` module wires everything together via Hilt.

### Core Modules

| Module | Responsibility |
|--------|----------------|
| `core-domain` | Business models (`Video`, `Category`), repository interfaces, use cases |
| `core-data` | Repository implementations, channel allowlist, quota tracking, video filtering |
| `core-network` | YouTube Data API v3 client (Retrofit + Moshi), base URL: `googleapis.com` |
| `core-database` | Room database for search/video caching (6-hour TTL via `SearchCacheEntity.CACHE_TTL_MS`) |
| `core-datastore` | DataStore preferences (PIN, languages, quota tracking, settings) |
| `core-common` | Shared coroutine utilities |
| `core-ui` | Shared Compose components (`VideoCard`, `LoadingState`, `ErrorState`), theme |

### Feature Modules

| Module | Screens |
|--------|---------|
| `feature-home` | Video grid with pull-to-refresh, infinite scroll |
| `feature-player` | Full-screen WebView player with controls overlay |
| `feature-search` | Search functionality (parent-only access, behind PIN) |
| `feature-parental` | PIN entry, settings screen |
| `feature-onboarding` | First-run wizard |

### Dependency Injection (Hilt)

All DI uses `@InstallIn(SingletonComponent::class)`:
- `app/di/AppModule.kt` — provides `@Named("youtube_api_key")` from `BuildConfig.YOUTUBE_API_KEY`
- `core-data/di/DataModule.kt` — binds `VideoRepositoryImpl` → `VideoRepository`, `SettingsRepositoryImpl` → `SettingsRepository`
- `core-network/di/NetworkModule.kt` — provides Moshi, OkHttpClient, Retrofit, YouTubeApiService
- `core-database/di/DatabaseModule.kt` — provides Room DB and DAOs

### Key Architectural Patterns

**Content Curation** (`core-domain/model/Category.kt`, `core-domain/usecase/GetCuratedFeedUseCase.kt`):
- Categories are internal query pools, not user-facing
- `Categories.priority` = real kids channels (always included, 2 per load)
- `Categories.dutch` + `Categories.general` = supplementary content
- Feed loads 4 pools per request (2 priority + 2 others), tracks used pool IDs to rotate
- When all pools exhausted, resets and reshuffles

**Video Safety Filtering** (`core-data/repository/VideoRepositoryImpl.kt`):
- Videos must be `madeForKids` OR from `AllowlistedChannels.channelIds`
- Blocked channels filtered out
- Language filtering against user preferences with `allowUnknownLanguage` fallback
- Cache-first strategy: checks Room cache before API calls

**API Quota Management** (`core-data/quota/QuotaTracker.kt`):
- Daily limit: 10,000 units, warning threshold at 95%
- Search costs: 100 units per search.list + 1 unit per 50 videos for videos.list
- Falls back to expired cache when quota exhausted (`QuotaExhaustedException`)
- Quota resets tracked via DataStore preferences

**Video Player** (`feature-player/PlayerScreen.kt`):
- Uses WebView loading YouTube mobile site (`m.youtube.com/watch?v=`)
- JavaScript injection hides YouTube UI (header, footer, nav, metadata) and auto-unmutes
- Transparent touch interceptor overlay sits on top of WebView to capture taps for controls toggle
- Related videos in horizontal LazyRow, hidden during playback, appear on tap with controls
- Play/Pause uses `evaluateJavascript` to control the `<video>` element directly

**Navigation** (`app/navigation/NavGraph.kt`):
- Routes: `onboarding`, `home`, `search`, `player/{videoId}`, `pin_entry`, `settings`
- Start destination determined by `StartDestinationViewModel` (checks if onboarding complete)
- Settings access: Home → PIN Entry → Settings (PIN guards all parental features)
- Player navigation pops back to Home to prevent deep back stacks

**State Management**:
- ViewModels expose `StateFlow<UiState>` for UI state
- Navigation events via `SharedFlow` (e.g., `navigateToVideo` for auto-play)
- Settings persisted in DataStore with Flow-based observation

## Configuration

YouTube API key must be in `local.properties`:
```properties
YOUTUBE_API_KEY=your_key_here
```

This is read at build time by `app/build.gradle.kts` and exposed via `BuildConfig.YOUTUBE_API_KEY`.

## Key Files

- `app/navigation/NavGraph.kt` — navigation routes, graph, and route helper functions
- `core/core-domain/model/Category.kt` — all content categories, search queries, and pool definitions
- `core/core-datastore/KidsTubePreferences.kt` — all app preferences (PIN, languages, quota, settings)
- `feature/feature-player/PlayerScreen.kt` — video player with WebView, JS injection, and controls overlay
- `core/core-data/repository/VideoRepositoryImpl.kt` — API calls, caching, and safety filtering logic
- `core/core-data/repository/AllowlistedChannels.kt` — trusted channel IDs that bypass madeForKids check
- `gradle/libs.versions.toml` — all dependency versions (version catalog)
