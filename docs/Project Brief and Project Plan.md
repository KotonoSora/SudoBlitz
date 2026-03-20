# 1. Project Brief

**Project Overview**
Arcade-style Sudoku mobile game built natively on Android using Kotlin and Jetpack Compose. The product delivers short-session puzzle gameplay enhanced with time pressure, streak mechanics, and optional coin-based boosts.

**Target Users**

* Casual mobile players (1–5 minute sessions)
* Puzzle enthusiasts seeking lightweight interaction
* Users open to optional in-app purchases

**Core Gameplay**

* Dynamic Sudoku generation (4x4 → 9x9)
* Real-time validation
* Timer, mistake limit, and streak system
* Score-based progression

**Monetization Summary**

* Fully coin-based consumable IAP model
* Coins used for:

  * Extra time
  * Hints
  * Undo
  * Continue after failure
* No gameplay blocking; all features accessible without payment

**Key Features**

* Core Sudoku gameplay engine
* Real-time scoring and validation
* Coin system (earn/spend)
* **Coin Shop (Google Play Billing integration – critical)**
* Boost mechanics
* Stats and daily challenge

**Success Criteria**

* Stable billing integration (no transaction loss)
* Input latency <100ms
* High session repeatability (2–4 min sessions)
* Positive coin usage engagement (spend rate per session)

---

# 2. Project Plan (Technical Implementation)

## 2.1 System Overview

**Technical Summary**
Native Android application using MVVM architecture. The system is composed of modular features with reactive state handling via Kotlin Flow.

**Key Components**

* UI Layer (Compose screens)
* ViewModels (state management)
* Domain Layer (game logic, use cases)
* Data Layer (local storage + billing integration)

**Responsibilities**

* Game Engine: puzzle generation and validation
* Economy System: coin balance and boost logic
* Billing System: IAP lifecycle handling
* Persistence: local storage of user data

---

## 2.2 Architecture Design

**Architecture Pattern:** MVVM (mandatory)

### UI Layer

* Jetpack Compose screens
* Stateless composables driven by ViewModel state

### ViewModel Layer

* Exposes StateFlow
* Handles UI logic and user actions

### Domain Layer

* Use Cases:

  * GenerateSudoku
  * ValidateInput
  * CalculateScore
  * ApplyBoost
  * ManageCoins

### Data Layer

* Repositories:

  * GameRepository
  * CoinRepository
  * BillingRepository
* Data sources:

  * DataStore (coins, settings)
  * Optional Room (stats)

---

### Data Flow

UI → ViewModel → UseCase → Repository → DataSource
Result → StateFlow → UI recomposition

---

## 2.3 Module and Feature Mapping

**Game Module**

* SudokuGenerator
* SudokuValidator
* GameSessionManager

**Economy Module**

* CoinManager
* BoostProcessor

**Billing Module (Critical Path)**

* BillingClientWrapper
* PurchaseManager
* ProductRepository

**Persistence Module**

* DataStoreManager
* StatsStorage

**UI Module**

* 8 Compose screens + ViewModels

---

### Dependencies

* Billing → CoinManager
* CoinManager → DataStore
* Game → independent core logic

---

## 2.4 Screen and Navigation Design

**Navigation:** Jetpack Compose Navigation (NavHost)

### Screens (Strict 8)

1. HomeScreen
2. GameScreen
3. ResultScreen
4. CoinShopScreen (**critical**)
5. DifficultyScreen
6. DailyChallengeScreen
7. StatsScreen
8. SettingsScreen

---

### Navigation Flow

* Home → Difficulty → Game → Result
* Home → Coin Shop
* Home → Stats / Daily / Settings

---

### State Handling

* Each screen has dedicated ViewModel
* Shared states (coins) via shared repository

---

## 2.5 Data and State Management

**Core Models**

* CoinBalance:

  * amount: Int

* PurchaseState:

  * Idle / Loading / Success / Failed / Pending

* GameState:

  * grid
  * timeRemaining
  * score
  * mistakes

---

**Persistence Strategy**

* DataStore:

  * coin balance
  * settings

* Room (optional):

  * stats history

---

**State Management**

* StateFlow for UI state
* Single source of truth in ViewModel

---

## 2.6 Google Play Billing Integration

**Product Query**

* Fetch product list using BillingClient
* Map product IDs:

  * coins_100 → coins_4000

---

**Purchase Flow**

1. User selects product
2. Launch billing flow
3. Receive purchase callback

---

**Verification**

* Validate purchase state (client-side MVP)
* Ensure purchase is acknowledged

---

**Consumption**

* Consume purchase token
* Allow repeat purchases

---

**Coin Granting**

* Only after:

  * Purchase successful
  * Verified
  * Consumed

---

**Error Handling**

* Pending:

  * Show status, retry later
* Failed:

  * Display error message
* Canceled:

  * No state change

---

## 2.7 Core Workflows

### App Startup

1. Splash (non-counted)
2. Initialize:

   * DataStore
   * BillingClient
3. Load coin balance
4. Navigate to Home

---

### Gameplay Loop

1. Generate grid
2. Initialize GameState
3. Start timer
4. Handle input:

   * Validate
   * Update score/combo
5. End condition check
6. Save result
7. Reward coins

---

### Coin Earning & Spending

* Earn:

  * After game completion
* Spend:

  * Trigger boost
  * Deduct immediately
  * Apply effect instantly

---

### Purchase Flow

1. Open CoinShop
2. Load products
3. User purchases
4. Verify → consume
5. Update coin balance
6. Emit updated state

---

## 2.8 Non-Functional Requirements

**Performance**

* Input latency <100ms
* Smooth Compose recomposition

**Reliability**

* Billing retry handling
* No duplicate coin grants

**Security**

* Purchase must be verified before granting coins
* Avoid local manipulation (basic validation)

**Maintainability**

* Modular architecture
* Clear separation of concerns

---

## 2.9 Technical Constraints and Compliance

* Kotlin + Jetpack Compose enforced
* MVVM mandatory
* Exactly 8 screens
* Google Play Billing compliance required:

  * Consumable products
  * Proper acknowledgment and consumption

---

## 2.10 Implementation Guidelines

**Code Structure**

* Feature-based modules
* Clear separation: UI / Domain / Data

---

**Best Practices**

* Immutable UI state
* Unidirectional data flow
* Avoid business logic in UI layer

---

**Reusability**

* Shared Sudoku grid component
* Reusable timer logic

---

**Testing Strategy**

Unit Tests:

* Sudoku generator validity
* Score calculation
* Coin deduction logic

Integration Tests:

* Billing flow (mocked BillingClient)

UI Tests:

* Game interaction flows

---

## 2.11 Risks and Technical Considerations

**Billing Complexity**

* Risk: duplicate or lost transactions
* Mitigation: idempotent purchase handling

---

**State Consistency**

* Risk: mismatch between UI and data
* Mitigation: single source of truth (StateFlow)

---

**Performance Under Input Load**

* Risk: lag during rapid input
* Mitigation: optimized Compose recomposition

---

**UX Friction in Purchase Flow**

* Risk: drop-off during purchase
* Mitigation: minimal steps, clear feedback

---

**Scalability Limitation**

* No backend validation (MVP constraint)
* Future need for server-side verification
