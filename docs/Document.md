---
**Document:** SudoBlitz — Product and Design Document
**Version:** 1.2
**Status:** Active
**Last Updated:** 2026-03-20
**Roles:** Product Owner (PO) · Software Architect (SA)

---

# Table of Contents

1. [Game Overview](#1-game-overview)
2. [Gameplay Design](#2-gameplay-design)
3. [Monetization Design](#3-monetization-design)
4. [Feature Breakdown](#4-feature-breakdown)
5. [Screen Definition](#5-screen-definition-8-screens)
6. [User Flows](#6-user-flows)
7. [Requirements](#7-requirements)
8. [Architecture Overview](#8-architecture-overview)
9. [Prioritization](#9-prioritization)
10. [Risks and Considerations](#10-risks-and-considerations)

---

## 1. Game Overview

### Concept

SudoBlitz is a mobile-first Sudoku game redesigned for short, repeatable sessions. Gameplay layers light arcade mechanics (countdown timer, mistake limit, streak multiplier) on top of classic Sudoku logic to create a faster, more engaging experience. Puzzle complexity scales from 4×4 to 9×9 grids, accommodating both casual and experienced players.

**Application ID:** `com.kotonosora.sudoblitz`
**Platform:** Native Android (API 24+)

---

### Target Audience

| Segment | Session Pattern | Primary Need |
|---|---|---|
| Casual mobile users | 1–5 minutes | Low friction, quick reward |
| Puzzle enthusiasts | 5–15 minutes | Challenge depth, progression |
| IAP-open users | Recurring | Meaningful boosts, no pay-to-win |

---

### Value Proposition

| Pillar | Description |
|---|---|
| Speed | Short session cycles; results and rewards within minutes |
| Accessibility | No onboarding required; rules are universally understood |
| Fairness | All puzzles reachable without payment; coins improve efficiency, not access |
| Progression | Difficulty scaling (4×4 → 9×9) and unlock depth keep players returning |

---

### Success Criteria

| Metric | Target |
|---|---|
| Input response latency | < 100ms |
| Session length average | 2–4 minutes |
| Crash-free sessions | ≥ 99% |
| Coin spend rate (engaged users) | ≥ 1 boost used per session |
| Billing transaction loss rate | 0% |

---

## 2. Gameplay Design

### Core Gameplay Loop

```
1. Home Screen: user selects "Play"
2. Boost Selection Screen: user selects difficulty tier (includes grid size)
3. System generates a valid, uniquely-solvable Sudoku grid
4. Timer starts; player fills cells via number pad
5. System validates each input in real-time:
   - Correct → score increases, combo streak continues
   - Incorrect → mistake counter increments, combo resets
6. End condition evaluated after each input:
   - All cells correctly filled → Puzzle Complete
    - mistakes ≥ 3 → Game Over (no revive)
    - timeRemaining = 0 → Game Over (no revive)
7. Coins awarded based on difficulty, score, and accuracy
8. Result Screen shown → player retries or returns to Home
```

---

### Difficulty Tiers

| Tier | Internal Name | Grid Size | Timer | Max Mistakes | Cells Removed |
|---|---|---|---|---|---|
| Easy | `EASY` | 4×4 | 60 seconds | 3 | 6 |
| Medium | `MEDIUM` | 6×6 | 180 seconds | 3 | 18 |
| Hard | `HARD` | 6×6 | 180 seconds | 3 | 22 |
| Expert | `VERY_HARD` | 9×9 | 300 seconds | 3 | 60 |

> **Note:** Hard and Medium share the same 6×6 grid size. Expert increases to 9×9 with 60 removed cells and a 5-minute timer.

---

### Scoring System

| Event | Score Delta |
|---|---|
| Correct cell input | +10 × combo multiplier |
| Incorrect input | 0 (no penalty to score; mistakes counter incremented) |
| Puzzle completion | No additional score bonus; coin reward is granted separately |

**Combo multiplier** increments by 1 for each consecutive correct input (cap: 5×). Resets to 1× on any mistake.

---

### Coin Economy

**Earning coins:**

Coins are awarded **only on puzzle completion (victory)**. Failed sessions (mistake limit or timer expiry) grant 0 coins.

**Formula:** `coinsEarned = 10 + (timeRemaining / 10) + gridSize`

| Example | Time Remaining | Grid Size | Coins Earned |
|---|---|---|---|
| Easy (fast) | 45s | 4 | `10 + 4 + 4 = 18` |
| Medium (average) | 90s | 6 | `10 + 9 + 6 = 25` |
| Expert (fast) | 200s | 9 | `10 + 20 + 9 = 39` |

**Starting coins:** 100 (awarded on first install).

**Spending coins:**

| Boost | Coin Cost | Effect |
|---|---|---|
| Extra Time | **20** | +30 seconds added to current timer |
| Hint | **30** | Reveals the correct value for the **first empty or error cell** |
| Undo | **15** | Clears the last error cell; reduces mistake count by 1 |

> All boost effects are applied immediately. Boost buttons are **disabled in the UI** (not hidden) when the coin balance is insufficient for that boost. The Hint boost reveals the first empty/error cell — not necessarily the player-selected cell.

---

### Engagement Mechanics

* **Timer pressure**: countdown creates urgency and differentiates difficulty tiers
* **Mistake limit**: 3 mistakes enforce mindful play without being punishing
* **Streak multiplier**: rewards consistent accuracy up to 5× per-cell score
* **Daily challenge**: fixed seed puzzle refreshes daily; bonus coin reward encourages return visits

---

## 3. Monetization Design

### Model

Fully coin-based consumable IAP via Google Play Billing. All core gameplay is free. Coins accelerate progression and reduce friction at failure points — they do not gate any content.

---

### IAP Product Catalog

All products are configured as **consumable items** in Google Play Console. Pricing is displayed using **localized values** fetched dynamically from the Play Billing API — no hardcoded price strings in the app.

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

> Product IDs must match **exactly** between the application code and the Google Play Console configuration.

---

### Monetization Principles

* **No gameplay blocking**: all puzzles are playable without spending coins
* **Contextual placement**: boost buttons surface only when in-game context makes them relevant
* **Immediate feedback**: every coin spend has a visible, immediate effect
* **Fairness**: no pay-to-win; a skilled player has no mechanical disadvantage over a paying player

---

### Compliance

* All purchases are repeatable consumables in compliance with Google Play policy
* Purchase verification occurs before coins are granted
* Localized pricing served via Billing API — no hardcoded currency values

---

## 4. Feature Breakdown

### MVP Features (Must Have)

| Feature | Priority | Dependency |
|---|---|---|
| Sudoku engine (4×4, 6×6) | Critical | None |
| Real-time input validation | Critical | Sudoku engine |
| Timer and mistake system | Critical | Sudoku engine |
| Coin balance (earn/spend) | Critical | DataStore persistence |
| **Coin Shop + Google Play Billing** | **Critical** | Coin balance, BillingClient |
| Boost mechanics (time, hint, undo) | Critical | Coin balance |
| Result and scoring screen | High | Sudoku engine, coin balance |
| Home screen + navigation | High | All screens |

---

### Secondary Features (Should Have)

| Feature | Priority | Dependency |
|---|---|---|
| Expert difficulty (9×9 / VERY_HARD) | High | Sudoku engine |
| Daily challenge mode | Medium | Sudoku engine, DataStore (date tracking) |
| Stats tracking | Medium | Room database |
| Sound effects | Medium | Audio assets |

---

### Deferred / Post-MVP (Could Have)

| Feature | Notes |
|---|---|
| Cloud sync | Requires backend infrastructure |
| Multiplayer | Out of scope for MVP |
| Server-side purchase verification | Documented as technical debt; essential at scale |
| Advanced analytics | Local-only in MVP |

---

### Feature Dependencies

```
Billing Integration
    └─→ Coin Shop UI
    └─→ Coin Balance (CoinRepository)
            └─→ Boost mechanics
            └─→ Coin earn/reward system

Sudoku Engine (SudokuGenerator + SudokuValidator)
    └─→ Game Screen
    └─→ Daily Challenge Screen
    └─→ Difficulty Selection

DataStore Persistence
    └─→ Coin balance
    └─→ Settings
    └─→ Daily challenge state

Room Database
    └─→ Stats Screen
    └─→ Result history
```

---

## 5. Screen Definition (8 Screens)

> The Splash Screen is a non-interactive initialization state and is **not counted** among the 8 functional screens.

---

### Screen 1: Home Screen

**Purpose:** Application entry point and primary navigation hub.

**Content:**
* Current coin balance (persistent, always visible)
* "Play" button → navigates to Difficulty Selection
* Quick links: Coin Shop, Daily Challenge, Stats, Settings

**Acceptance Criteria:**
- Coin balance rendered from `CoinRepository` within 300ms of screen load
- Navigation to all 4 linked screens is reachable from this screen
- "Play" button is always enabled (no prerequisites)

---

### Screen 2: Difficulty Selection Screen

**Purpose:** Allow the player to choose puzzle difficulty and grid size before starting a game.

**Screen name (route):** `BoostSelectionScreen` (`boost_selection`)

**Content:**
* Title: "CHOOSE LEVEL"
* Four difficulty buttons, each labelled with grid size:
    - "EASY (4×4)" — NeonGreen
    - "MEDIUM (6×6)" — NeonYellow
    - "HARD (6×6)" — NeonMagenta
    - "EXPERT (9×9)" — NeonRed

**Acceptance Criteria:**
- Tapping a level button calls `startNewGame(size, difficulty)` on `GameViewModel` and navigates to Game Screen
- EASY and EXPERT buttons visually contrast in color to communicate risk level
- Back navigation returns to Home without side effects

---

### Screen 3: Game Screen

**Purpose:** Core gameplay interface.

**Content:**
* Top bar: level label ("Lvl {streak+1}") and current score ("SCORE: {score}")
* Stats row: countdown timer (MM:SS format; turns NeonRed when < 10 seconds remaining) and combo indicator ("x{n} COMBO" in NeonYellow — shown only when combo > 1×)
* Sudoku grid (dynamically sized: 4×4, 6×6, or 9×9)
* Number pad (1–4, 1–6, or 1–9 depending on grid size)
* Boost row — 3 boost buttons with coin costs:
    - "+ 30s" (Extra Time) — 20 coins
    - "Hint" — 30 coins
    - "Undo" — 15 coins

**Acceptance Criteria:**
- Cell tap response < 100ms to visual highlight; tap sound (`tap.wav`) plays
- Input validation triggers within 100ms of number selection
- Correct input: score incremented, combo multiplied; incorrect: `error.wav` + haptic vibration + mistake counter incremented
- Timer updates every second without recomposing the grid
- Boost buttons are **disabled** (not hidden) when coin balance is insufficient for that boost
- On mistake count reaching 3: `isGameOver = true`, `playLose()` + haptic, navigate to Result Screen
- On timer reaching 0: `isGameOver = true`, `playLose()` + haptic, navigate to Result Screen
- On all cells correctly filled: `isVictory = true`, `playWin()`, navigate to Result Screen

---

### Screen 4: Result Screen

**Purpose:** Display session outcome, final score, and allow the player to continue or return home.

**Content:**
* Outcome title: **"VICTORY!"** (NeonGreen) on win, **"GAME OVER"** (NeonRed) on loss
* Stats card containing:
    - "Final Score" label (NeonBlue) with score value (NeonYellow)
    - "Streak" label (NeonMagenta) with streak count (NeonCyan)
* Actions:
    - **"NEXT LEVEL"** button (NeonGreen) — shown on victory; continues same difficulty/size with bonus time
    - **"RETRY"** button (NeonCyan) — shown on loss; restarts same difficulty/size
    - **"HOME"** button (NeonMagenta) — returns to Home Screen

**Acceptance Criteria:**
- Coins earned are added to balance **before** the Result Screen is displayed
- "NEXT LEVEL" calls `nextLevel()` on `GameViewModel` (streak +1, bonus time added) then navigates to Game Screen
- "RETRY" calls `startNewGame(same params)` then navigates to Game Screen
- Back button on this screen returns to Home (Game Screen is popped from back stack including Game entry)
- Streak resets to 0 on loss; streak increments only on "NEXT LEVEL" (not on "HOME")

---

### Screen 5: Coin Shop Screen

**Screen name (route):** `ShopScreen` (`shop`)

**Purpose:** Allow players to purchase coin packages via Google Play Billing.

**Content:**
* Current coin balance
* List of all 9 coin packages with localized price and coin amount
* Selected package triggers native Play purchase flow
* Purchase state feedback: loading, success, failed, pending, canceled

**Acceptance Criteria:**
- Product list loaded from Play Billing API (no hardcoded prices)
- Each product shows localized price and coin amount
- Purchase success: coin balance updated immediately in UI
- Purchase failure: error message shown; no coin change
- Pending purchase: status message shown; no premature coin grant
- Screen remains navigable during and after any purchase state
- In **debug builds**: mock products are shown; tapping any product immediately grants coins without Play flow

---

### Screen 6: Daily Challenge Screen

**Purpose:** Offer a daily fixed puzzle with bonus coin rewards to drive return visits.

**Content:**
* Fixed seed puzzle (refreshes daily at midnight local time)
* Bonus coin reward indicator
* Completion status for today ("Completed" badge if already done)

**Acceptance Criteria:**
- Seed is deterministic per calendar date (same puzzle for all users on same date)
- Completion state persisted via DataStore; replaying shows "Already completed today"
- Bonus coins awarded only on first completion per calendar day

---

### Screen 7: Stats Screen

**Screen name (route):** `ProgressScreen` (`progress`) — labeled **"LEADERBOARD"** in the Home Screen navigation button.

**Purpose:** Show player performance history to reinforce progression and retention.

**Content:**
* All-time **high score** (from Room database `getHighScore()`)
* All-time **best streak** (from DataStore `best_streak`)
* **Recent game history** — up to 50 most recent sessions from Room database, each showing: difficulty, grid size, score, victory/loss, timestamp

**Acceptance Criteria:**
- High score and best streak sourced from `UserPreferencesRepository` (DataStore) and `GameRecordDao`
- Recent records list sourced from Room database `game_records` table (last 50 by timestamp)
- Empty state displayed gracefully when no games have been recorded
- Stats are read-only; screen has no editing or reset functionality

---

### Screen 8: Settings Screen

**Purpose:** Allow players to configure app-level audio and feedback preferences.

**Content:**
* **Sound effects** toggle (on/off) — controls `tap.wav`, `error.wav`, `win.wav`, `lose.wav`
* **Background music** toggle (on/off) — key stored in DataStore; player implementation deferred
* **Haptic feedback** toggle (on/off) — controls 50ms vibration on errors and game over

**Acceptance Criteria:**
- Each toggle state persisted immediately via DataStore; survives app restart
- Sound toggle: when off, all game sounds are suppressed; when on, sounds play normally
- Haptic toggle: when off, no vibration; when on, 50ms vibrate on mistake and game over
- Toggles take effect immediately without requiring app restart

---

## 6. User Flows

### First-Time User

```
App Launch → Splash (init) → Home Screen
    → Tap "PLAY GAME" → Boost Selection → Easy (4×4)
    → Game Screen → Complete puzzle
    → Result Screen (coins earned)
    → Home Screen
```

No onboarding overlay is required. Rules are discoverable through gameplay.

---

### Returning User — Gameplay Loop

```
Home → Boost Selection → Game → Result → [NEXT LEVEL | RETRY | HOME]
```

---

### Coin Purchase Flow

```
Home → Shop (tap cart icon in top bar)
    → Products loaded from Play API
    → User selects package
    → Play native purchase dialog
    → [Success] → consume token → grant coins → update balance UI
    → [Canceled] → return to Coin Shop; no state change
    → [Pending] → show pending status; coins not granted yet
    → [Failed] → show error message; no coin change
```

---

### Boost Usage Flow

```
Game Screen — player taps boost button
    → Sufficient coins: deduct → apply effect immediately → visual confirmation
    → Insufficient coins: show "Not enough coins" prompt → offer Coin Shop shortcut
```

---

### Daily Challenge Flow

```
Home → Daily Challenge
    → [Not completed today] → load seed puzzle → play → complete → bonus coins awarded
    → [Already completed today] → show completion badge; puzzle accessible for practice (no reward)
```

---

## 7. Requirements

### Functional Requirements

| ID | Requirement | Priority |
|---|---|---|
| FR-01 | System shall generate a valid, uniquely-solvable Sudoku grid for each selected difficulty | Critical |
| FR-02 | System shall validate each cell input in real-time (< 100ms) | Critical |
| FR-03 | System shall track score, combo multiplier, mistakes, and timer per session | Critical |
| FR-04 | System shall maintain a non-negative coin balance persisted via DataStore | Critical |
| FR-05 | System shall support all 9 consumable IAP products via Google Play Billing | Critical |
| FR-06 | Coins shall be granted only after successful purchase verification and consumption | Critical |
| FR-07 | System shall apply boost effects immediately upon coin deduction | High |
| FR-08 | System shall recover pending purchases on every BillingClient reconnect | High |
| FR-09 | System shall prevent duplicate coin grants via purchase token deduplication | High |
| FR-10 | Daily challenge puzzle shall be deterministic per calendar date | Medium |
| FR-11 | Daily challenge bonus coins shall be awarded only once per calendar day | Medium |
| FR-12 | Game state shall be restored after process death (SavedStateHandle) | High |
| FR-12 | Game state shall be restored after process death (SavedStateHandle) | High |

> **Current implementation note:** FR-08, FR-09, and FR-12 are **not yet implemented** in the current codebase. They are documented as post-MVP technical debt. FR-08 (pending purchase recovery) and FR-09 (token deduplication) are required before production scale.
| FR-13 | Stats history shall be persisted in Room database | Medium |

---

### Non-Functional Requirements

| ID | Requirement | Target |
|---|---|---|
| NFR-01 | Input response latency (cell tap to visual feedback) | < 100ms |
| NFR-02 | Sudoku generation time (9×9 Hard) | < 500ms |
| NFR-03 | Cold start to Home Screen | < 2 seconds |
| NFR-04 | Crash-free sessions | ≥ 99% |
| NFR-05 | Offline gameplay support (except billing flows) | Full offline |
| NFR-06 | Billing transaction loss rate | 0% |
| NFR-07 | Duplicate coin grant rate | 0% |

---

### Assumptions

* No backend server is required for MVP — architecture is local-first
* Sudoku puzzle validity and difficulty scaling handled entirely on-device
* Players are familiar with Sudoku rules; no in-app tutorial is required for MVP
* Purchase verification is client-side at MVP; server-side verification is post-MVP technical debt

---

### Constraints and Limitations

| Constraint | Detail |
|---|---|
| No cloud sync | All data is device-local; unrecoverable on device change |
| No multiplayer | Single-player only in MVP scope |
| No backend analytics | Session data remains local; no aggregation or reporting |
| Server-side receipt validation | Deferred — client-side only in MVP |
| Screen count | Exactly 8 functional screens; additions require documented decision |

---

## 8. Architecture Overview

> This section summarizes the system architecture for product and design alignment. Full architectural detail is in the **Project Brief and Technical Plan (v1.1)**.

### Pattern: MVVM + Clean Use Cases

### Pattern: MVVM with Engine Singleton

```
UI Layer (Jetpack Compose)
    ↕  events / StateFlow
ViewModel Layer (GameViewModel, ShopViewModel, ProgressViewModel, SettingsViewModel)
    ↕  direct calls / coroutines
Data Layer (UserPreferencesRepository / AppDatabase / BillingManager)
```

`SudokuEngine` is a pure Kotlin `object` with no Android dependencies. All business logic (scoring, boost effects, timer) lives directly in `GameViewModel`.

### Key Architectural Decisions Relevant to Product

| Decision | Implication |
|---|---|
| No DI framework (manual wiring) | Faster MVP build; ViewModels need explicit factories |
| DataStore for coins and settings | Asynchronous reads; all balance access is via `Flow` |
| Room for game records | Progress screen may show empty state on fresh install |
| `BillingManager` in `ShopViewModel` | Client disconnects when ShopViewModel is cleared; reconnect handling is post-MVP |
| No `SavedStateHandle` for game state | Active game is **lost** on OS-initiated process death — noted as post-MVP item |
| No token deduplication yet | Duplicate coin grant risk on purchase retry — noted as post-MVP item |

### Component Ownership Summary

| Component | Layer | Owned Feature |
|---|---|---|
| `SudokuEngine` (object) | Domain | Puzzle generation and validation — all gameplay screens |
| `GameViewModel` | ViewModel | Timer, mistakes, combo, score, boosts |
| `UserPreferencesRepository` (DataStore) | Data | Coin balance, high score, best streak, all settings |
| `BillingManager` in `ShopViewModel` | Data/ViewModel | Shop screen — product query, purchase, coin grant |
| `AppDatabase` / `GameRecordDao` (Room) | Data | Progress/Leaderboard screen |

---

## 9. Prioritization

### MVP Scope (v1.0)

| Priority | Feature | Rationale |
|---|---|---|
| 1 — Critical | Coin Shop + Google Play Billing | Direct revenue; requires early validation with Play Console |
| 2 — Critical | Sudoku engine (4×4, 6×6) + validation | Core user retention driver |
| 3 — Critical | Coin system (earn/spend) + boosts | Ties monetization to gameplay loop |
| 4 — High | Timer, mistakes, scoring | Differentiates from static Sudoku apps |
| 5 — High | Result screen + coin reward | Closes the session loop; drives repeat play |
| 6 — High | Home + navigation structure | Required for functional app delivery |

### Post-MVP Scope (v1.1+)

| Priority | Feature | Rationale |
|---|---|---|
| 1 — High | Hard difficulty (9×9) | Expands audience; engine already supports it |
| 2 — Medium | Daily challenge | Retention mechanic; drives daily active users |
| 3 — Medium | Stats screen | Progression reinforcement |
| 4 — Medium | Sound effects | Polish; uses pre-generated assets |
| 5 — Low | Server-side receipt validation | Security hardening at scale |
| 6 — Low | Cloud sync / analytics | Infrastructure dependency; deferred |

---

## 10. Risks and Considerations

| Risk | Severity | Impact | Mitigation |
|---|---|---|---|
| Billing integration edge cases (pending, duplicate, disconnect) | High | Financial / user trust | Implement full purchase lifecycle: token dedup, pending recovery, reconnect back-off |
| Low coin conversion if boost value is unclear | High | Revenue | Ensure every boost has visible, immediate effect with clear cost/benefit display |
| Process death during active game | Medium | UX / retention | SavedStateHandle serializes full `GameUiState` on every input |
| BillingClient disconnect mid-purchase | Medium | Transaction loss | Exponential back-off reconnect; recover via `queryPurchasesAsync` on reconnect |
| Input lag on low-end devices (API 24) | Medium | Retention | Use `derivedStateOf` and `key()` to minimize recomposition scope; test on API 24 emulator |
| Sudoku generation timeout (9×9 Hard) | Low | UX | Run generation on `Dispatchers.Default`; enforce 5-second timeout with cancellation |
| Overuse of boosts reducing challenge integrity | Low | Engagement | Boost costs calibrated so casual players are not incentivized to abuse; no auto-boost |

---

## 11. Art Style & Visual Design

> Source of truth: `ui/theme/Color.kt`, `ui/theme/Theme.kt`, `ui/theme/Type.kt`, `ui/components/NeonComponents.kt`

### Visual Identity: Neon Arcade

SudoBlitz uses a strict **dark-only** neon arcade aesthetic. There is no light mode. The theme enforces edge-to-edge layout with transparent status and navigation bars (dark icons disabled).

### Color Palette

| Token Name | Hex | Usage in App |
|---|---|---|
| `DarkBackground` | `#0A0A12` | Global background — near-black deep space |
| `SurfaceDark` | `#1A1A2E` | Cards and elevated panels (e.g., Result stats card) |
| `GridLineColor` | `#222244` | Sudoku grid cell borders |
| `NeonCyan` | `#00FFFF` | Primary accent; timer (normal state); streak value in Result |
| `NeonMagenta` | `#FF00FF` | Secondary accent; "BLITZ" title; HOME/SETTINGS buttons; streak label |
| `NeonYellow` / `CoinGold` | `#FFEA00` | Coin balance; MEDIUM button; combo indicator; score value in Result |
| `NeonGreen` | `#39FF14` | EASY button; victory title ("VICTORY!"); PLAY GAME button; NEXT LEVEL button |
| `NeonRed` / `ErrorRed` | `#FF003C` | EXPERT button; game-over title ("GAME OVER"); timer < 10s warning |
| `NeonBlue` | `#0066FF` | LEADERBOARD button; "Final Score" label in Result Screen |

### Typography

**Font:** [Press Start 2P](https://fonts.google.com/specimen/Press+Start+2P) — pixel/retro style. Applied **universally** to all Material3 typography levels (`displayLarge` through `bodySmall`) in `Type.kt`. No fallback to system fonts.

The font is embedded as `res/font/press_start_2p.ttf`.

### UI Component Tokens

| Component | Shape | Height | Fill | Border |
|---|---|---|---|---|
| `NeonButton` | Pill — `RoundedCornerShape(50)` | 64dp | `color.copy(alpha = 0.15f)` semi-transparent | 2dp solid neon color |
| `NeonTitle` | Text only | — | — | Shadow `blurRadius = 16f` (glow effect) |
| `NeonText` | Text only | — | — | No shadow; configurable size/weight/color |

### Screen-Level Visual Notes

| Screen | Notable Visual Behavior |
|---|---|
| Home | Title rendered as "SUDO" (NeonCyan) + "BLITZ" (NeonMagenta) on two stacked NeonTitle composables |
| Boost Selection | Four NeonButtons, each in a different neon color corresponding to difficulty |
| Game | Timer text turns `NeonRed` when < 10 seconds; combo badge (`x{n} COMBO`) only visible when combo > 1× |
| Result | Stats displayed on a `SurfaceDark` rounded card; victory/loss title fills full width |
| All screens | Edge-to-edge: `enableEdgeToEdge()` called in `MainActivity`; `isAppearanceLightStatusBars = false`; transparent system bars |

---

## 12. Audio & Haptic Feedback

> Source of truth: `audio/SoundManager.kt`, `audio/HapticManager.kt`

### Sound System

Sound effects are implemented using Android `SoundPool` with the `USAGE_GAME` audio attribute (max 5 concurrent streams). Sounds are loaded from `res/raw/` at startup.

| Sound File | Trigger |
|---|---|
| `tap.wav` | Cell tap, boost button tap |
| `error.wav` | Incorrect number input (new mistake registered) |
| `win.wav` | All cells correctly filled (victory) |
| `lose.wav` | Game over (mistake limit reached or timer expired) |

`SoundManager` is provided globally via `LocalSoundManager` (`CompositionLocal`) from `MainActivity`. It is released via `DisposableEffect` when the Activity is destroyed.

**Sound toggle:** Controlled by `sound_enabled` key in DataStore. When off, all `play*()` calls are no-ops.

### Haptic Feedback

`HapticManager` provides a single-shot 50ms vibration. It is provided globally via `LocalHapticManager` (`CompositionLocal`).

| API Level | Implementation |
|---|---|
| API 26+ | `VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)` |
| API 25 and below | Legacy `vibrator.vibrate(50L)` (deprecated but supported for min SDK 24) |

**Haptic triggers in gameplay:**
- New mistake: `hapticManager.vibrate()` + `soundManager.playError()`
- Game over: `hapticManager.vibrate()` + `soundManager.playLose()`

**Haptic toggle:** Controlled by `haptic_enabled` key in DataStore. When off, `vibrate()` is a no-op.

### Background Music

A `music_enabled` key exists in DataStore and a toggle is present in the Settings Screen. A background music **player is not yet implemented** — the key is reserved for future use.
| No server-side receipt validation (MVP) | Low now / High at scale | Fraud risk | Documented as post-MVP debt; monitor Play fraud reports post-launch |
