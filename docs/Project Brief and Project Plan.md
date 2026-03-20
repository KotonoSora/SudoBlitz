---
**Document:** Project Brief and Technical Plan — SudoBlitz
**Version:** 1.2
**Status:** Active
**Last Updated:** 2026-03-20
**Roles:** Software Architect (SA) · Technical Writer (TW)

---

# Table of Contents

1. [Project Brief](#1-project-brief)
2. [Project Plan (Technical Implementation)](#2-project-plan-technical-implementation)
   - [2.1 System Overview](#21-system-overview)
   - [2.2 Technology Stack](#22-technology-stack)
   - [2.3 Architecture Design](#23-architecture-design)
   - [2.4 Module and Feature Mapping](#24-module-and-feature-mapping)
   - [2.5 Screen and Navigation Design](#25-screen-and-navigation-design)
   - [2.6 Data and State Management](#26-data-and-state-management)
   - [2.7 Google Play Billing Integration](#27-google-play-billing-integration)
   - [2.8 Core Workflows](#28-core-workflows)
   - [2.9 Non-Functional Requirements](#29-non-functional-requirements)
   - [2.10 Technical Constraints and Compliance](#210-technical-constraints-and-compliance)
   - [2.11 Implementation Guidelines](#211-implementation-guidelines)
   - [2.12 Risks and Technical Considerations](#212-risks-and-technical-considerations)
   - [2.13 Architectural Decision Records](#213-architectural-decision-records)
3. [Glossary](#3-glossary)

---

# 1. Project Brief

## Overview

SudoBlitz is a native Android arcade-style Sudoku game built with Kotlin and Jetpack Compose. It delivers short-session puzzle gameplay enhanced with time pressure, streak mechanics, and optional coin-based boosts. The product targets broad casual audiences while maintaining depth for puzzle enthusiasts.

**Application ID:** `com.kotonosora.sudoblitz`
**Minimum Android Version:** API 24 (Android 7.0 Nougat)
**Target Android Version:** API 36

---

## Target Users

| Segment | Behavior Pattern | Value Expectation |
|---|---|---|
| Casual mobile players | 1–5 minute sessions | Quick, rewarding, low friction |
| Puzzle enthusiasts | 5–15 minute sessions | Challenge variation, progression |
| IAP-open users | Recurring engagement | Meaningful boosts, no pay-to-win |

---

## Core Gameplay

* Dynamic Sudoku puzzle generation with selectable grid size (4×4 → 9×9)
* Real-time input validation with visual feedback
* Countdown timer creating time pressure per difficulty
* Mistake counter with session-end enforcement
* Streak system rewarding consecutive correct inputs
* Score-based progression with per-session coin rewards

---

## Monetization

The game uses a fully coin-based consumable IAP model. All gameplay features remain accessible without payment; purchases accelerate progression.

**Coins are spent on:**

* Extra time extension
* Cell hints
* Undo last move

> **Implementation note:** The Revive boost (continue after failure) is not implemented in the current codebase. The three active in-game boosts are: Extra Time (20 coins, +30s), Hint (30 coins), and Undo (15 coins).

**Coins are earned by:**

* Completing game sessions
* Speed bonus — faster completion yields more coins

**Coin earning formula:** `coinsEarned = 10 + (timeRemaining / 10) + gridSize`

**Starting balance:** 100 coins on first install.

**IAP products (consumable):**

| Product ID | Price (USD) | Coins Granted | Type |
|---|---|---|---|
| `coins_100` | $0.29 | 100 | Consumable |
| `coins_500` | $0.49 | 500 | Consumable |
| `coins_1000` | $0.69 | 1,000 | Consumable |
| `coins_1500` | $0.99 | 1,500 | Consumable |
| `coins_2000` | $1.99 | 2,000 | Consumable |
| `coins_2500` | $3.99 | 2,500 | Consumable |
| `coins_3000` | $4.99 | 3,000 | Consumable |
| `coins_3500` | $7.99 | 3,500 | Consumable |
| `coins_4000` | $9.99 | 4,000 | Consumable |

---

## Key Features

* Core Sudoku gameplay engine (generation + validation)
* Real-time scoring and combo system
* Coin economy (earn and spend)
* **Coin Shop with Google Play Billing — critical path**
* Boost mechanics (Extra Time, Hint, Undo)
* Player statistics tracking
* Daily challenge mode

---

## Success Criteria

| Metric | Target |
|---|---|
| Billing transaction reliability | 0% lost transactions |
| Input response latency | < 100ms |
| Session length | 2–4 minutes average |
| Coin spend rate | ≥ 1 boost per session (engaged users) |
| Crash-free sessions | ≥ 99% |

---

# 2. Project Plan (Technical Implementation)

## 2.1 System Overview

SudoBlitz is a native Android application built on MVVM architecture with reactive state driven by Kotlin StateFlow. The system is structured in four distinct layers with a uni-directional data flow. All inter-layer communication is mediated through well-defined interfaces, ensuring testability and separation of concerns.

**Layer Responsibilities**

| Layer | Responsibility |
|---|---|
| UI (Compose) | Render state, emit user events |
| ViewModel | Transform events into domain calls; expose `StateFlow` |
| Domain (`SudokuEngine` + ViewModel methods) | Puzzle generation, game logic, boost effects |
| Data (Repositories) | Abstract storage, billing, and external data access |

**Data Flow**

```
User Action
    ↓
Compose UI  →  ViewModel  →  Use Case  →  Repository  →  Data Source
                  ↑                              |
              StateFlow  ←─────────────────── Result
```

> All state flows are `StateFlow<UiState>` objects. ViewModels hold the single source of truth for screen state. Shared state (e.g., coin balance) is read from a shared `CoinRepository` by any ViewModel that requires it, rather than passed between ViewModels.

---

## 2.2 Technology Stack

### Core Platform

| Technology | Version | Purpose |
|---|---|---|
| Kotlin | 2.2.10 | Primary language |
| Android Gradle Plugin | 9.1.0 | Build tooling |
| Min SDK | 24 | Android 7.0+ coverage |
| Target / Compile SDK | 36 | Latest platform features |

### UI

| Library | Version | Purpose |
|---|---|---|
| Jetpack Compose BOM | 2024.09.00 | Compose version alignment |
| Material3 | (BOM-managed) | UI component system |
| Navigation Compose | 2.8.9 | Screen routing |
| Lifecycle ViewModel Compose | 2.8.7 | `viewModel()` in Compose |
| Lifecycle Runtime Compose | 2.8.7 | `collectAsStateWithLifecycle()` |

### Data & Persistence

| Library | Version | Purpose |
|---|---|---|
| DataStore Preferences | 1.1.7 | Coin balance, settings |
| Room Runtime + KTX | 2.7.0 | Stats history (optional) |
| KSP | 2.3.5 | Code generation (Room, Moshi) |

### Concurrency

| Library | Version | Purpose |
|---|---|---|
| kotlinx-coroutines-core | 1.10.2 | Structured concurrency |
| kotlinx-coroutines-android | 1.10.2 | `Dispatchers.Main` integration |

### Billing

| Library | Version | Purpose |
|---|---|---|
| Play Billing KTX | 7.1.1 | Google Play IAP |

### Dependency Injection

No DI framework (Hilt/Koin) is used. Dependencies are manually wired via:

* A top-level `AppContainer` object (or singleton repository instances) held in `Application`
* `ViewModelProvider.Factory` implementations per ViewModel to inject repositories

This keeps the setup lightweight and avoids plugin/annotation-processor complexity at MVP stage.

---

## 2.3 Architecture Design

### Pattern: MVVM with Clean Use Cases

```
┌────────────────────────────────────────┐
│             UI Layer                   │
│  Composable screens (stateless)        │
│  collectAsStateWithLifecycle()         │
└────────────────┬───────────────────────┘
                 │ events / callbacks
┌────────────────▼───────────────────────┐
│          ViewModel Layer               │
│  StateFlow<GameState/UiState>          │
│  viewModelScope (coroutine lifecycle)  │
└────────────────┬───────────────────────┘
                 │ direct calls
┌────────────────▼───────────────────────┐
│     Domain: SudokuEngine (object)      │
│  generateBoard(size, difficulty)        │
│  Pure Kotlin — no Android imports      │
└────────────────┬───────────────────────┘
                 │
┌────────────────▼───────────────────────┐
│          Data Layer                    │
│  UserPreferencesRepository (DataStore) │
│  AppDatabase / GameRecordDao (Room)    │
│  BillingManager (in ShopViewModel)     │
└────────────────────────────────────────┘
```

### UI Layer

* All Composables are **stateless** — they receive `UiState` and emit lambda callbacks
* State is collected via `collectAsStateWithLifecycle()` to respect lifecycle bounds and avoid resource leaks
* Navigation is handled through a single `NavHost` in `MainActivity`

### ViewModel Layer

* Four ViewModels: `GameViewModel`, `ShopViewModel`, `ProgressViewModel`, `SettingsViewModel`
* `GameViewModel` is a **single shared instance** used by `GameScreen`, `ResultScreen`, and `DailyChallengeScreen`
* ViewModels expose `StateFlow` for state; they use `viewModelScope` for all coroutines
* `SavedStateHandle` is **not used** in the current codebase — game state is lost on process death (post-MVP item)

### Domain Layer

* `SudokuEngine` is a Kotlin **`object`** singleton in `engine/SudokuEngine.kt`
* No Android framework imports — fully unit-testable on JVM
* Business logic (scoring, boost effects, combo, timer, game end) lives in `GameViewModel` methods

**Engine & ViewModel Method Inventory**

| Component | Method | Responsibility |
|---|---|---|
| `SudokuEngine` | `generateBoard(size, difficulty)` | Backtracking with shuffled candidates + unique-solution verification |
| `GameViewModel` | `startNewGame(size, difficulty)` | Generates board via `SudokuEngine`; sets timer; resets state |
| `GameViewModel` | `inputNumber(number)` | Validates input; updates score (+10 × combo), combo, mistakes |
| `GameViewModel` | `addTime()` | 20 coins → +30 seconds to `timeRemaining` |
| `GameViewModel` | `useHint()` | 30 coins → fills first empty/error cell with `correctValue` |
| `GameViewModel` | `undoMistake()` | 15 coins → clears last error cell; `mistakes - 1` |
| `GameViewModel` | `nextLevel()` | Same size/difficulty; `streak + 1`; bonus time = `startingTime / 2` |
| `GameViewModel` | `endGame(victory)` | Saves `GameRecord` to Room; grants coins on victory; updates DataStore |

### Data Layer

**Repository Interfaces**

```kotlin
// UserPreferencesRepository (DataStore — "user_stats")
class UserPreferencesRepository(context: Context) {
    val coinsFlow: Flow<Int>            // key: "coins", default: 100
    val highScoreFlow: Flow<Int>        // key: "high_score", default: 0
    val bestStreakFlow: Flow<Int>        // key: "best_streak", default: 0
    val soundEnabledFlow: Flow<Boolean> // key: "sound_enabled", default: true
    val musicEnabledFlow: Flow<Boolean> // key: "music_enabled", default: true
    val hapticEnabledFlow: Flow<Boolean>// key: "haptic_enabled", default: true

    suspend fun updateCoins(delta: Int)       // coerceAtLeast(0) — no negatives
    suspend fun updateHighScore(score: Int)
    suspend fun updateBestStreak(streak: Int)
    suspend fun updateSoundEnabled(Boolean)
    suspend fun updateMusicEnabled(Boolean)
    suspend fun updateHapticEnabled(Boolean)
}

// GameRecordDao (Room — "game_records" table)
@Dao interface GameRecordDao {
    @Query("SELECT * FROM game_records ORDER BY timestamp DESC LIMIT 50")
    fun getRecentRecords(): Flow<List<GameRecord>>

    @Insert suspend fun insertRecord(record: GameRecord)

    @Query("SELECT MAX(score) FROM game_records")
    fun getHighScore(): Flow<Int?>
}

// BillingManager (held by ShopViewModel : AndroidViewModel)
class BillingManager(context, preferencesRepository) : PurchasesUpdatedListener {
    val products: StateFlow<List<StoreProduct>>
    fun launchBillingFlow(activity: Activity, product: StoreProduct)
    fun endConnection()   // called in ShopViewModel.onCleared()
    // debug mode: mock products, instant coin grant — skips BillingClient
}
```

**Threading Model**

| Operation | Dispatcher |
|---|---|
| DataStore reads/writes | `Dispatchers.IO` |
| Room queries | `Dispatchers.IO` |
| Sudoku generation | `Dispatchers.Default` |
| BillingClient callbacks | `Dispatchers.Main` (Play enforces this) |
| StateFlow collection | `Dispatchers.Main` via `collectAsStateWithLifecycle` |

---

## 2.4 Module and Feature Mapping

### Game Module (`feature/game`)

| Component | Responsibility |
|---|---|
| `SudokuEngine` (object) | Backtracking generation; unique-solution verification; sizes 4×4, 6×6, 9×9 |
| `GameViewModel` | Timer (coroutine), mistake tracking, combo multiplier, scoring, boost logic |
| `SudokuModels.kt` | `Cell`, `Board`, `Difficulty` (EASY/MEDIUM/HARD/VERY_HARD) |

### Economy Module (`feature/economy`)

| Component | Responsibility |
|---|---|
| `UserPreferencesRepository` | Coin balance reads/writes via DataStore; `coerceAtLeast(0)` prevents negatives |
| `GameViewModel.addTime()` | 20 coins → +30 seconds |
| `GameViewModel.useHint()` | 30 coins → reveal first empty/error cell |
| `GameViewModel.undoMistake()` | 15 coins → clear last error cell; `mistakes - 1` |

### Billing Module — Critical Path (`feature/billing`)

| Component | Responsibility |
|---|---|
| `BillingManager` | `PurchasesUpdatedListener`; `BillingClient` lifecycle; product query; consumeAsync; coin grant |
| `ShopViewModel` | `AndroidViewModel`; holds `BillingManager`; disconnects in `onCleared()` |

> **Lifecycle note:** `BillingManager` is initialized in `ShopViewModel` and disconnected via `onCleared()`. Reconnect on `onBillingServiceDisconnected` is **not implemented** — this is post-MVP work needed before production scale.

### Persistence Module (`data/persistence`)

| Component | Responsibility |
|---|---|
| `UserPreferencesRepository` | Single `PreferencesDataStore` (name: `"user_stats"`); manages 6 preference keys |
| `AppDatabase` v1 (Room) | Singleton via double-checked locking; entity: `GameRecord`; DAO: `GameRecordDao` |

### UI Module (`ui/`)

* 8 Compose screens; 4 have dedicated ViewModels (`GameViewModel`, `ShopViewModel`, `ProgressViewModel`, `SettingsViewModel`)
* `SoundManager` + `HapticManager` provided globally via `CompositionLocal` (`LocalSoundManager`, `LocalHapticManager`)
* Shared composables: `SudokuGrid`, `Numpad`, `NeonButton`, `NeonTitle`, `NeonText`

---

### Module Dependency Graph

```
UI Module
    ├─→ GameViewModel → SudokuEngine (generateBoard)
    │       ├─→ UserPreferencesRepository (coins, high score, streak) → DataStore
    │       └─→ GameRecordDao (insert record) → Room
    ├─→ ShopViewModel → BillingManager → BillingClient
    │       └─→ on purchase success → UserPreferencesRepository.updateCoins()
    ├─→ ProgressViewModel → GameRecordDao (getRecentRecords, getHighScore) → Room
    │                     → UserPreferencesRepository (bestStreak) → DataStore
    └─→ SettingsViewModel → UserPreferencesRepository (sound/music/haptic) → DataStore
```

* `BillingModule` → depends on → `CoinRepository`
* `EconomyModule` → depends on → `DataStore`
* `GameModule` → no external dependencies (pure logic)

---

## 2.5 Screen and Navigation Design

**Navigation implementation:** Jetpack Compose Navigation (`NavHost`) with string route constants defined in `sealed class Screen` (`ui/navigation/Screen.kt`). The `NavHost` lives inside the `SudoBlitzApp` composable in `MainActivity`.

### Screen Inventory

| # | Composable | Route String | Entry Point | ViewModel |
|---|---|---|---|---|
| 1 | `HomeScreen` | `"home"` | App launch, back-stack root | None (stateless; receives `coins: Int`) |
| 2 | `BoostSelectionScreen` | `"boost_selection"` | Home → "PLAY GAME" | None (stateless) |
| 3 | `GameScreen` | `"game"` | BoostSelection → level select; DailyChallenge → start | `GameViewModel` (shared) |
| 4 | `ResultScreen` | `"result"` | Game → `isGameOver == true` or `isVictory == true` | None (reads `GameViewModel.gameState`) |
| 5 | `ShopScreen` | `"shop"` | Home → cart icon in top bar | `ShopViewModel` |
| 6 | `DailyChallengeScreen` | `"daily_challenge"` | Home → "DAILY CHALLENGE" | None |
| 7 | `ProgressScreen` | `"progress"` | Home → "LEADERBOARD" | `ProgressViewModel` |
| 8 | `SettingsScreen` | `"settings"` | Home → "SETTINGS" | `SettingsViewModel` |

### Navigation Flow

```
Home ──→ BoostSelection ──→ [startNewGame(size, difficulty)] ──→ Game
  │
    ├──→ Shop              (back: popBackStack)
    ├──→ DailyChallenge    (back: popBackStack) ──→ [startNewGame] ──→ Game
    ├──→ Progress          (back: popBackStack)
    └──→ Settings          (back: popBackStack)

Game ──────────────────────────────────→ Result
                                                                                    (Screen.Game popped inclusive)

Result:
    ├──→ "NEXT LEVEL" (victory) → nextLevel() → navigate to Game
    ├──→ "RETRY" (loss)         → startNewGame(same params) → navigate to Game
    └──→ "HOME"                 → navigate to Home
```

**Back-stack rules:**

* `Screen.Game` is popped inclusive when navigating to `Screen.Result` — back button on Result goes to Home, not Game
* `GameViewModel` is shared by `GameScreen`, `ResultScreen`, and `DailyChallengeScreen`; it is NOT recreated on navigation between these
* No navigation arguments are passed to `Screen.Game` — `GameViewModel.startNewGame(size, difficulty)` is called before `navController.navigate("game")`
* Deep links are not required for MVP

---

## 2.6 Data and State Management

### UI State Models (sealed classes per screen)

**`GameState`** (actual data class in `viewmodel/GameViewModel.kt`)

```kotlin
data class GameState(
    val board: Board? = null,
    val selectedCell: Cell? = null,
    val timeRemaining: Int = 180,       // seconds; overridden by startNewGame() per grid size
    val score: Int = 0,
    val comboMultiplier: Int = 1,       // +1 per correct input; max 5; resets on mistake
    val mistakes: Int = 0,
    val maxMistakes: Int = 3,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val currentDifficulty: Difficulty = Difficulty.EASY,
    val currentSize: Int = 4,
    val streak: Int = 0                 // incremented by nextLevel(); reset to 0 on endGame(victory=false)
)

// Cell model (SudokuModels.kt)
data class Cell(
    val row: Int, val col: Int,
    val value: Int = 0,             // 0 = empty
    val correctValue: Int = 0,
    val isGiven: Boolean = false,   // pre-filled; immutable by user
    val isError: Boolean = false    // true when value != correctValue after input
)
```

### Persistence Schema

**DataStore (Preferences)**

DataStore name: `"user_stats"` (via `preferencesDataStore` delegate).

| Preferences Key | Type | Default | Description |
|---|---|---|---|
| `coins` | `Int` | **100** | Current coin balance; new install starts with 100 |
| `high_score` | `Int` | `0` | All-time highest score across all sessions |
| `best_streak` | `Int` | `0` | All-time best consecutive level streak |
| `sound_enabled` | `Boolean` | `true` | Sound effects toggle |
| `music_enabled` | `Boolean` | `true` | Music toggle (stored; player not yet implemented) |
| `haptic_enabled` | `Boolean` | `true` | Haptic feedback toggle |

**Room Database — `AppDatabase` v1**

`game_records` table (entity class: `GameRecord`):

| Column | Type | Notes |
|---|---|---|
| `id` | `INTEGER PRIMARY KEY AUTOINCREMENT` | |
| `timestamp` | `INTEGER` | Epoch milliseconds (`System.currentTimeMillis()`) |
| `difficulty` | `TEXT` | `EASY / MEDIUM / HARD / VERY_HARD` |
| `size` | `INTEGER` | 4, 6, or 9 |
| `score` | `INTEGER` | Final session score |
| `isVictory` | `INTEGER` | Boolean: 1 = victory, 0 = loss |

### State Management Principles

* `StateFlow` is the primary mechanism for exposing observable state from ViewModels
* Navigation events are triggered directly via lambda callbacks to `navController` — no `SharedFlow` event bus
* `SavedStateHandle` is **not used** in the current codebase; active `GameState` is held in-memory only. Game state is lost if the OS kills the process. This is documented as post-MVP work.

---

## 2.7 Google Play Billing Integration

> **Billing Library version:** 7.1.1 (Play Billing KTX)
> This section is the **critical path** for the product. Errors here cause financial and reputational risk.

### BillingClient Lifecycle

```
ShopViewModel.init:
    ├─→ [debug build] → load mock products immediately; skip BillingClient
    └─→ [release build] → billingClient.startConnection()
            └─→ onBillingSetupFinished(OK) → queryProductDetailsAsync()
                      → products emitted via StateFlow (sorted by coin value)

ShopViewModel.onCleared():
    └─→ BillingManager.endConnection() → billingClient.endConnection()
```

> **Known gap:** Reconnect logic on `onBillingServiceDisconnected` is **not yet implemented**. `queryPurchasesAsync` on reconnect (for pending purchase recovery) is also absent. Both are required before production scale.

### Product Query

1. On `BillingSetupFinished`, query `ProductType.INAPP` for all known product IDs
2. Cache `ProductDetails` in `ProductRepository`
3. Emit updated product list via `StateFlow`

**Product ID to coin mapping:**

| Product ID | Price (USD) | Coins |
|---|---|---|
| `coins_100` | $0.29 | 100 |
| `coins_500` | $0.49 | 500 |
| `coins_1000` | $0.69 | 1,000 |
| `coins_1500` | $0.99 | 1,500 |
| `coins_2000` | $1.99 | 2,000 |
| `coins_2500` | $3.99 | 2,500 |
| `coins_3000` | $4.99 | 3,000 |
| `coins_3500` | $7.99 | 3,500 |
| `coins_4000` | $9.99 | 4,000 |

### Purchase Flow (State Machine)

```
User taps product in ShopScreen
    ↓
ShopViewModel.buyProduct(activity, product)
    ├─→ [debug] BillingManager.grantCoins(productId) → updateCoins(+amount)
    └─→ [release] BillingManager.launchBillingFlow(activity, product)
        └─→ onPurchasesUpdated(OK, purchases)
            └─→ handlePurchase(purchase):
                1. Check purchaseState == PURCHASED
                2. billingClient.consumeAsync(token)
                   └─→ ConsumeResult.OK → grantCoins(products)
                       → preferencesRepository.updateCoins(+amount)
```

### Known Billing Gaps (Post-MVP)

| Gap | Risk | Status |
|---|---|---|
| No reconnect on `onBillingServiceDisconnected` | Purchase flow fails if Play Store disconnects mid-session | Not implemented |
| No pending purchase recovery (`queryPurchasesAsync`) | Purchases may be lost on process death during flow | Not implemented |
| No purchase token deduplication | Potential duplicate coin grants on retry | Not implemented |
| No server-side receipt validation | Fraud risk at scale | Intentional deferral to post-MVP |

**Security constraint (currently enforced):** `updateCoins()` is called **only after** `consumeAsync()` succeeds, in both debug (immediate mock) and release builds.

---

## 2.8 Core Workflows

### App Startup

```
1. MainActivity.onCreate():
    a. enableEdgeToEdge()
    b. UserPreferencesRepository created (preferencesDataStore delegate on applicationContext)
    c. AppDatabase.getDatabase(applicationContext) → Room singleton
    d. SoundManager + HapticManager created via remember {}; released via DisposableEffect
    e. CompositionLocalProvider for LocalSoundManager + LocalHapticManager
    f. SudoBlitzTheme applied; SudoBlitzApp() composable launched
2. SudoBlitzApp():
    a. ViewModelProvider.Factory builds GameViewModel, ShopViewModel, SettingsViewModel, ProgressViewModel
    b. NavHost(startDestination = "home") mounted
3. GameViewModel.init:
    a. startNewGame(4, Difficulty.EASY) called immediately to pre-warm first puzzle
4. HomeScreen rendered; coin balance collected via DataStore Flow
```

### Gameplay Loop

```
1. BoostSelectionScreen: user selects difficulty + implicit grid size
2. gameViewModel.startNewGame(size, difficulty):
    a. SudokuEngine.generateBoard(size, difficulty) on Dispatchers.Default
    b. _gameState.update { new GameState(board=generated, ...) }
    c. startTimer() launched in viewModelScope (1-second interval coroutine)
3. Navigate to GameScreen; UI collects gameState: StateFlow<GameState>
4. Per user input (inputNumber(n)):
   a. ValidateInputUseCase called
   b. If correct: update score, increment combo
   c. If incorrect: increment mistakes; reset combo
5. End condition evaluated after each input:
   a. All cells filled correctly → Complete
    b. mistakes >= maxMistakes → Game Over
    c. timeRemaining == 0 → Game Over
6. GameViewModel.endGame(victory: Boolean):
    a. timerJob?.cancel()
    b. gameRecordDao.insertRecord(GameRecord(score, difficulty, size, isVictory))
    c. If victory:
        coinsEarned = 10 + (timeRemaining / 10) + currentSize
        preferencesRepository.updateCoins(+coinsEarned)
        preferencesRepository.updateBestStreak(streak + 1)
    d. If loss: streak reset to 0
    e. preferencesRepository.updateHighScore(score)
    f. Navigate to Result (Game popped inclusive)
```

### Boost Usage

```
User taps boost button
    ↓
GameViewModel method: addTime() / useHint() / undoMistake()
    ├─→ coins.value < cost → button disabled in UI (cannot reach this path)
    └─→ coins.value >= cost:
            1. preferencesRepository.updateCoins(-cost)
            2. Apply effect:
               addTime()       → timeRemaining += 30
               useHint()       → find first empty/error cell → set value = correctValue
               undoMistake()   → find last isError cell → clear it; mistakes - 1
            3. _gameState.update { ... } → StateFlow emits new state
```

### Coin Earning

Coins are granted **only on victory** inside `endGame(victory=true)`, before navigation to `ResultScreen`.

**Formula:** `coinsEarned = 10 + (timeRemaining / 10) + currentSize`

Failed sessions (mistake limit or timer) earn 0 coins and reset `streak` to 0.

---

## 2.9 Non-Functional Requirements

### Performance

| Requirement | Target | Measurement |
|---|---|---|
| Input response latency | < 100ms from tap to visual feedback | Manual test on mid-range device (API 24) |
| Sudoku generation time | < 500ms for 9×9 (Hard) | Unit test with `measureTimeMillis` |
| Cold start to HomeScreen | < 2 seconds | Android vitals / systrace |
| Compose recomposition rate | No unnecessary full-screen recompositions on cell tap | Layout inspector |

### Reliability

| Requirement | Target |
|---|---|
| Billing transaction loss | 0% — pending purchase recovery on reconnect |
| Duplicate coin grants | 0% — token deduplication enforced |
| Crash-free sessions | ≥ 99% |
| Process-death game recovery | Active game survives process death via `SavedStateHandle` |

### Security

| Requirement | Implementation |
|---|---|
| Coins granted only on verified purchase | `consumeAsync()` must succeed before `updateCoins()` is called |
| No negative coin balance | `updateCoins(delta)` uses `.coerceAtLeast(0)` |
| Debug/release isolation | `BuildConfig.DEBUG` flag in `BillingManager.init`; mock flow only in debug |
| Token replay prevention | **Not yet implemented** — documented as post-MVP technical debt |

### Maintainability

* `SudokuEngine` (object) has zero Android imports — fully unit-testable on JVM without instrumentation
* ViewModels contain minimal Android framework dependencies — primary dependency is `AndroidViewModel` (required by `ShopViewModel` for Application context in `BillingManager`)
* `UserPreferencesRepository` and `GameRecordDao` can be mocked in tests via constructor injection

---

## 2.10 Technical Constraints and Compliance

**Language and Framework**

* Kotlin only — no Java source files in new code
* Jetpack Compose for all UI — no XML layouts except `AndroidManifest.xml` and resource files
* MVVM pattern is mandatory across all screens

**Screen Count**

* Exactly 8 screens are defined. Adding screens requires a documented decision.

**Google Play Billing Compliance**

* All consumable products must be consumed via `BillingClient.consumeAsync()` before coins are granted
* Purchases must never remain unacknowledged for more than 3 days (Play policy)
* The app must handle `ITEM_ALREADY_OWNED` by attempting consumption (necessary for consumables)
* Billing Library 7.x requires all `BillingClient` operations on main thread

**Proguard / R8**

* `billing-ktx` classes must be kept in ProGuard rules to prevent obfuscation of Play Billing interfaces
* Room entity classes must be kept

---

## 2.11 Implementation Guidelines

### Code Structure (Package Layout)

```
com.kotonosora.sudoblitz
├── MainActivity.kt
│   └── SudoBlitzApp()       // NavHost + ViewModel provisioning
│   └── LocalSoundManager    // CompositionLocal<SoundManager>
│   └── LocalHapticManager   // CompositionLocal<HapticManager>
│
├── audio/
│   ├── SoundManager.kt      // SoundPool; 4 sounds: tap, error, win, lose
│   └── HapticManager.kt     // Vibrator; 50ms single-shot
│
├── billing/
│   └── BillingManager.kt    // BillingClient wrapper + debug mock
│
├── data/
│   ├── AppDatabase.kt       // Room singleton
│   ├── GameRecord.kt        // @Entity("game_records")
│   ├── GameRecordDao.kt     // getRecentRecords(50), insertRecord, getHighScore
│   └── UserPreferencesRepository.kt
│
├── engine/
│   └── SudokuEngine.kt      // object; generateBoard(size, difficulty)
│
├── model/
│   └── SudokuModels.kt      // Cell, Board, Difficulty (4 levels)
│
├── ui/
│   ├── components/          // NeonButton, NeonTitle, NeonText, SudokuGrid, Numpad
│   ├── navigation/          // Screen.kt (sealed class with 8 route strings)
│   ├── screens/             // 8 screen composables
│   └── theme/               // Color.kt, Theme.kt, Type.kt
│
└── viewmodel/
    ├── GameViewModel.kt     // GameState, timer, boosts, scoring
    ├── ProgressViewModel.kt
    ├── SettingsViewModel.kt
    └── ShopViewModel.kt     // AndroidViewModel; owns BillingManager
```

### Best Practices

* **Immutable UI state**: `UiState` data classes use only `val` fields; mutation produces a new instance via `copy()`
* **Unidirectional data flow**: UI emits events (function calls), ViewModel processes them and emits new state
* **No business logic in Composables**: Composables call ViewModel functions; they do not contain conditions or calculations beyond display formatting
* **Coroutine scope discipline**: Only `viewModelScope` and `lifecycleScope` are used; no `GlobalScope`

### Shared Composables

| Component | Key Props | Screens |
|---|---|---|
| `SudokuGrid` | `board: Board`, `selectedCell: Cell?`, `onCellSelected` | `GameScreen`, `DailyChallengeScreen` |
| `Numpad` | `size: Int`, `onNumberSelected` | `GameScreen`, `DailyChallengeScreen` |
| `NeonButton` | `text`, `color`, `icon?`, `onClick`, `enabled` | All screens |
| `NeonTitle` | `text`, `color`, `fontSize` | All screens (glow shadow, Press Start 2P) |
| `NeonText` | `text`, `color`, `fontSize`, `fontWeight` | All screens |

### Testing Strategy

**Unit Tests** (JVM, no Android framework)

| Test | Coverage Target |
|---|---|
| `SudokuGeneratorTest` — valid puzzle, unique solution | Core algorithm correctness |
| `SudokuValidatorTest` — correct / incorrect / complete detection | All validation branches |
| `CalculateScoreUseCaseTest` — time bonus, combo multiplier, difficulty | Score formula correctness |
| `ManageCoinsUseCaseTest` — deduct insufficient, deduct sufficient, add | Economy invariants |
| `ApplyBoostUseCaseTest` — each boost type | Boost effect application |
| `CoinBalanceTest` — negative guard | Value class invariant |

**Integration Tests** (Android JVM with Robolectric or `kotlinx-coroutines-test`)

| Test | Coverage Target |
|---|---|
| `BillingRepositoryTest` — mocked `BillingClient` | Purchase flow state transitions |
| `CoinRepositoryTest` — DataStore in-memory | Coin persistence and retrieval |
| `GameRepositoryTest` — Room in-memory DB | Save and query results |

**UI Tests** (Compose Testing, Espresso)

| Test | Coverage Target |
|---|---|
| `GameScreenTest` — tap cell, enter number | Input response and state update |
| `CoinShopScreenTest` — product list rendering | Billing state display |
| `NavigationTest` — Home → Difficulty → Game → Result | Back-stack correctness |

---

## 2.12 Risks and Technical Considerations

| Risk | Severity | Mitigation |
|---|---|---|
| Duplicate coin grants from billing retry | High | **Not implemented** — post-MVP: token dedup in DataStore |
| Lost purchase on process death during flow | High | **Not implemented** — post-MVP: `queryPurchasesAsync` on reconnect |
| BillingClient disconnects mid-flow | Medium | **Not implemented** — post-MVP: reconnect in `onBillingServiceDisconnected` |
| Game state lost on process death | Medium | **Not implemented** — post-MVP: `SavedStateHandle` for `GameState` |
| Input lag on low-end devices (API 24) | Medium | Use `derivedStateOf` and `key()` for grid recomposition |
| Sudoku generation blocking (9×9 VERY_HARD) | Low | Runs on `Dispatchers.Default` ✓; add timeout if profiling shows issues |
| No server-side receipt validation | Low (now), High (at scale) | Intentional MVP deferral; document as known debt |
| ProGuard stripping Billing classes | Medium | Explicit `-keep` rules in `proguard-rules.pro` |
| `music_enabled` key stored but no music player | Low | Implement background music or remove key |

---

## 2.13 Architectural Decision Records

### ADR-001: No Dependency Injection Framework (Hilt/Koin)

**Decision:** Manual dependency wiring via `ViewModelProvider.Factory` implementations. No `Application` subclass; dependencies created inline in `MainActivity.onCreate()` and `SudoBlitzApp` composable.

**Rationale:** Hilt requires plugin configuration and annotation processing that increases build complexity. For an MVP with a contained number of classes, manual factories are sufficient and transparent. Koin was considered but adds a runtime dependency for marginal benefit at this scale.

**Consequences:** `UserPreferencesRepository`, `AppDatabase`, and `GameRecordDao` are created in `MainActivity.onCreate()` and passed to all 4 ViewModel factories. Migrating to Hilt post-MVP is straightforward given the constructor-injection pattern.

---

### ADR-002: DataStore over SharedPreferences for Coin Balance

**Decision:** Use `PreferencesDataStore` for all persistent key-value data.

**Rationale:** `SharedPreferences` has known thread-safety issues on older APIs. `DataStore` provides a coroutine-safe, `Flow`-based API that integrates naturally with the reactive architecture.

**Consequences:** All DataStore reads are asynchronous; there is no synchronous accessor. Callers must collect from `Flow` or use `first()` in a coroutine.

---

### ADR-003: Room for Stats (Optional at MVP)

**Decision:** Room database is included in dependencies. `GameRecord` entity and `GameRecordDao` are fully implemented. `ProgressScreen` (Leaderboard) reads from this database; it will gracefully display an empty state until the first game is completed.

**Rationale:** Defining the schema now prevents a future migration headache. The table does not block any critical feature.

---

## 2.14 Art Style and Visual Design

> Source of truth: `ui/theme/Color.kt`, `ui/theme/Theme.kt`, `ui/theme/Type.kt`, `ui/components/NeonComponents.kt`

### Theme Identity: Neon Arcade

SudoBlitz enforces a strict **dark-only** theme. No light mode variant exists. `SudoBlitzTheme` wraps `MaterialTheme` with `darkColorScheme`; `isAppearanceLightStatusBars = false`. Edge-to-edge layout is active (`enableEdgeToEdge()` in `MainActivity`) with transparent status and navigation bars.

### Color Tokens

| Token | Hex Value | Primary Usage |
|---|---|---|
| `DarkBackground` | `#0A0A12` | Global screen background |
| `SurfaceDark` | `#1A1A2E` | Cards and elevated surfaces |
| `GridLineColor` | `#222244` | Sudoku grid cell borders |
| `NeonCyan` | `#00FFFF` | Primary accent; timer (normal); "SUDO" title; HomeScreen primary |
| `NeonMagenta` | `#FF00FF` | Secondary accent; "BLITZ" title; SETTINGS/HOME/HARD buttons |
| `NeonYellow` / `CoinGold` | `#FFEA00` | Coin balance; MEDIUM button; combo badge; score value on Result |
| `NeonGreen` | `#39FF14` | EASY button; PLAY GAME; VICTORY! title; NEXT LEVEL button |
| `NeonRed` / `ErrorRed` | `#FF003C` | EXPERT button; GAME OVER title; timer < 10s warning |
| `NeonBlue` | `#0066FF` | LEADERBOARD button; "Final Score" label on Result |

### Typography

Font: **Press Start 2P** (pixel/retro, from `res/font/press_start_2p.ttf`). Applied to **all** `TextStyle` levels in `Typography` (`displayLarge` → `bodySmall`). No system font fallback.

### UI Component Specifications

| Component | Shape | Height | Fill | Border | Notes |
|---|---|---|---|---|---|
| `NeonButton` | `RoundedCornerShape(50)` (pill) | 64dp | `color.copy(alpha=0.15f)` | 2dp solid neon color | Optional `ImageVector` icon |
| `NeonTitle` | Text | — | — | Shadow `blurRadius=16f` (glow) | Used for large screen titles |
| `NeonText` | Text | — | — | No shadow | General text; configurable size, weight, color |

---

## 2.15 Audio and Haptic System

> Source of truth: `audio/SoundManager.kt`, `audio/HapticManager.kt`

### SoundManager

| Property | Value |
|---|---|
| Implementation | Android `SoundPool` |
| Audio usage attribute | `AudioAttributes.USAGE_GAME` |
| Max concurrent streams | 5 |
| Sound files | `res/raw/tap.wav`, `res/raw/error.wav`, `res/raw/win.wav`, `res/raw/lose.wav` |
| Toggle | `sound_enabled` in DataStore; `soundManager.soundEnabled` property |
| Lifecycle | Created in `MainActivity` via `remember {}`; released in `DisposableEffect` |
| Provided via | `LocalSoundManager` (`CompositionLocal<SoundManager>`) |

**Trigger table:**

| Method | Sound | Trigger Condition |
|---|---|---|
| `playTap()` | `tap.wav` | Cell tapped, boost button tapped |
| `playError()` | `error.wav` | Incorrect input (new mistake registered) |
| `playWin()` | `win.wav` | All cells filled correctly (`isVictory = true`) |
| `playLose()` | `lose.wav` | Game over (`isGameOver = true`) |

### HapticManager

| Property | Value |
|---|---|
| Effect | Single-shot 50ms vibration |
| API 26+ | `VibrationEffect.createOneShot(50, DEFAULT_AMPLITUDE)` |
| API 24–25 | Legacy `vibrator.vibrate(50L)` |
| Toggle | `haptic_enabled` in DataStore |
| Provided via | `LocalHapticManager` (`CompositionLocal<HapticManager>`) |

**Haptic is triggered on:** new mistake + game over (combined with `playError()` / `playLose()`).

### Background Music

`music_enabled` key exists in DataStore and a UI toggle exists in `SettingsScreen`. **No music player is implemented in the current codebase.** The key is reserved for a future implementation (e.g., `MediaPlayer` or `ExoPlayer` with a looping ambient track).

---

### ADR-004: Activity-Scoped BillingClient

**Decision:** `BillingManager` is initialized inside `ShopViewModel` (an `AndroidViewModel`), not in `MainActivity` or `Application`.

**Rationale:** Scoping to `ShopViewModel` ensures the billing client is alive only when the Shop Screen is active and is automatically torn down via `onCleared()`. This avoids background billing activity outside of user intent.

**Consequences:** If the Play Store disconnects while the Shop Screen is open, purchases cannot be retried without the user leaving and re-entering the Shop Screen (reconnect is not yet auto-triggered). This is a known limitation to address post-MVP.

---

# 3. Glossary

| Term | Definition |
|---|---|
| **IAP** | In-App Purchase — a user-initiated payment transaction through Google Play |
| **Consumable product** | An IAP item that can be purchased multiple times; must be consumed via `consumeAsync()` before re-purchase |
| **Purchase token** | Unique string (`purchaseToken`) returned by Play for each transaction; required for `consumeAsync()` |
| **Boost** | A coin-funded in-game power-up: Extra Time (20 coins, +30s), Hint (30 coins), Undo (15 coins) |
| **Combo multiplier** | `GameState.comboMultiplier`; increments on each correct input (max 5×); resets to 1× on any mistake |
| **Streak** | `GameState.streak`; count of consecutive levels completed via "NEXT LEVEL" without returning to Home |
| **VERY_HARD** | The `Difficulty` enum value for Expert tier; 9×9 grid, 60 cells removed, 300-second timer |
| **StateFlow** | A hot Kotlin coroutine flow that always holds and replays its latest emitted value |
| **SoundPool** | Android API for low-latency playback of short audio clips; used for all 4 game sound effects |
| **CompositionLocal** | Jetpack Compose mechanism to implicitly pass values down the composition tree; used for `SoundManager` and `HapticManager` |
| **BillingManager** | App-specific wrapper around `BillingClient`; initialized in `ShopViewModel`; handles product query, purchase flow, and coin grant |
| **KSP** | Kotlin Symbol Processing — annotation processor for Room entity code generation |
| **Press Start 2P** | Pixel/retro Google Font used as the exclusive typeface for all text in SudoBlitz |
| **Neon arcade theme** | Visual identity: `#0A0A12` background, neon color accents, 16f glow shadows, edge-to-edge |
| **Process death** | Android OS terminating the app process due to memory pressure; in-memory `GameState` is lost |
