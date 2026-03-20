# Project Plan

Update the IAP Product Catalog. Based on the provided IDE context, the `ShopScreen.kt` and underlying billing logic are still using outdated placeholder product IDs (`coin_pack_small`, `coin_pack_medium`, `coin_pack_large`). We need a plan to update the Google Play Billing implementation to query, display, and handle the exactly 9 required consumable product IDs: `coins_100`, `coins_500`, `coins_1000`, `coins_1500`, `coins_2000`, `coins_2500`, `coins_3000`, `coins_3500`, `coins_4000`.

## Project Brief

# Technical Requirements: Android Game Application

## 1. Overview
The application shall be a **native Android game** built using **Kotlin** and **Jetpack Compose**, designed with **simple gameplay mechanics** and a monetization model centered on **virtual currency (coins)**.

## 2. Platform and Architecture
* Platform: **Android (native)**
* Programming Language: **Kotlin**
* UI Framework: **Jetpack Compose**
* Architecture Pattern: **MVVM (Model-View-ViewModel)**

## 3. Application Structure
* The application shall consist of **8 functional screens (UI views)**
* Each screen must have a **clearly defined role** within the user journey
* Navigation must follow a **consistent and maintainable structure**

### UI Scope Constraint
* The **Splash Screen** is classified as a **non-interactive initialization state**
* It shall **not be counted** as part of the 8 functional screens

## 4. Core Feature: Coin Shop (High Priority)
* The **Coin Shop module** is the **highest-priority feature**
* It must be implemented and validated before lower-priority features
* All billing-related dependencies must be resolved early in development

## 5. In-App Purchase (IAP) Configuration
### 5.2 Product Catalog
| Product ID | Price (USD) | Coin Amount | Type       |
| ---------- | ----------- | ----------- | ---------- |
| coins_100  | 0.29        | 100         | Consumable |
| coins_500  | 0.49        | 500         | Consumable |
| coins_1000 | 0.69        | 1,000       | Consumable |
| coins_1500 | 0.99        | 1,500       | Consumable |
| coins_2000 | 1.99        | 2,000       | Consumable |
| coins_2500 | 3.99        | 2,500       | Consumable |
| coins_3000 | 4.99        | 3,000       | Consumable |
| coins_3500 | 7.99        | 3,500       | Consumable |
| coins_4000 | 9.99        | 4,000       | Consumable |

## 5.3 Implementation Requirements
* All products shall be configured as **consumable items** in Google Play Console
* Product IDs must match exactly between the application and Play Console

## 6. Game Design Constraints
* Gameplay must be **simple and intuitive**
* The core loop should **encourage voluntary coin usage**
* Coin-based actions (e.g., upgrades, unlocks) must provide **clear user value**
* Monetization must **not block core gameplay progression**

## 7. Assets and UI Standards
* Image assets shall use **PNG format** to support transparency
* The application shall include **sound effects**
* The primary font shall be **"Press Start 2P"**

## 8. Non-Functional Requirements
* The application must maintain **responsive performance** across supported Android devices
* UI interactions must be **smooth and stable**, including billing flows
* The system must be resilient to **runtime errors and connectivity issues**

## Implementation Steps
**Total Duration:** 26m 57s

### Task_1_Data_and_Engine: Implement DataStore for persistent stats (coins, high scores) and build the Sudoku Game Engine (grid generator for 4x4/6x6, validation, GameState ViewModel).
- **Status:** COMPLETED
- **Updates:** DataStore successfully implemented for coins, high scores, and best streaks. SudokuEngine built to generate robust 4x4 and 6x6 grids with validation. GameViewModel exposes GameState using StateFlow, handling real-time operations like score, combo, and mistakes. Code compiles successfully.
- **Acceptance Criteria:**
  - DataStore saves/loads coins and scores
  - Sudoku generator correctly creates solvable 4x4 and 6x6 grids
  - ViewModel exposes GameState
- **Duration:** 2m 48s

### Task_2_Core_UI: Implement Compose Navigation and build the core UI screens: Home, Game (grid rendering, input, stats), and Result screens.
- **Status:** COMPLETED
- **Updates:** Navigation between Home, Game, and Result works perfectly. Game screen dynamically renders 4x4 and 6x6 Sudoku grids with responsive timer and stats. The Numpad input accurately inserts numbers into cells. The project builds and runs without errors.
- **Acceptance Criteria:**
  - Navigation between Home, Game, and Result works
  - Game screen renders the Sudoku grid dynamically
  - User can input numbers into the grid
- **Duration:** 3m 41s

### Task_3_Arcade_and_Monetization: Implement arcade mechanics (timer countdown, combo multiplier, lives limit), in-game coin boosts (extra time, hints), and the Coin Shop UI with Google Play Billing integration.
- **Status:** COMPLETED
- **Updates:** Google Play Billing Library successfully integrated and Coin Shop UI implemented. Arcade mechanics including timer countdown, combo multipliers, lives limit, and in-game coin boosts are fully functional. The project builds successfully.
- **Acceptance Criteria:**
  - Timer counts down and ends game when reaching zero
  - Mistakes reduce lives
  - Coins can be spent for hints or time
  - Coin Shop screen displays purchase options
- **Duration:** 4m 48s

### Task_4_Polish_and_Verify: Apply Material 3 vibrant theming, edge-to-edge display, and create an adaptive app icon. Run and Verify: instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Polishing is completed. Material 3 vibrant theming, edge-to-edge display, and adaptive app icon are implemented. Critic agent verified the application. The app is stable with no crashes, core features are functional, and there are no critical UI issues. Test and build passed successfully.
- **Acceptance Criteria:**
  - App uses vibrant M3 color scheme and edge-to-edge
  - Adaptive app icon is present
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 4m 26s

### Task_5_Additional_Screens_and_Architecture: Implement the remaining screens (Boost Selection, Progress, Daily Challenge, Settings) to complete the strict 8-screen architecture, and update navigation flow.
- **Status:** COMPLETED
- **Updates:** The remaining screens (Boost Selection, Progress, Daily Challenge, Settings) have been successfully implemented, completing the strict 8-screen architecture. The Compose NavHost in MainActivity has been updated to include all 8 interactive screens. A custom NeonComponents design system was built to strictly adhere to the retro arcade aesthetic and visual cues from the provided design image. The project successfully compiled and all unit tests passed.
- **Acceptance Criteria:**
  - Boost Selection, Progress, Daily Challenge, and Settings screens are accessible
  - App navigation contains exactly 8 functional screens
  - The implemented UI must match the design provided in /Users/thangnguyen/kotonosora/unityhub/SudoBlitz/input_images/sudoblitz_mvp_ui.jpg
- **Duration:** 4m 54s

### Task_6_Retro_Aesthetic_and_Audio: Apply the Retro Arcade Aesthetic using 'Press Start 2P' font, matching UI to the design image, and integrate SoundPool for low-latency audio effects. Run and Verify: instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** The Retro Arcade Aesthetic using 'Press Start 2P' font has been successfully applied, matching the UI to the design image. Android SoundPool has been integrated for low-latency audio effects (tap, error, win, lose). The critic_agent verified application stability, confirming no crashes, all core features present, and the UI correctly utilizing the retro arcade aesthetic and font. All existing tests pass and the build succeeds.
- **Acceptance Criteria:**
  - 'Press Start 2P' font is applied
  - SoundPool plays sound effects during gameplay
  - The implemented UI must match the design provided in /Users/thangnguyen/kotonosora/unityhub/SudoBlitz/input_images/sudoblitz_mvp_ui.jpg
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 6m 20s

