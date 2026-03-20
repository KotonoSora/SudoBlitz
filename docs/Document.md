# Document

## 1. Game Overview

**Concept**
A mobile-first Sudoku game redesigned for short, repeatable sessions. The gameplay emphasizes quick puzzle solving with light arcade elements (timer, streaks), while preserving the core logic of Sudoku. Complexity scales gradually from 4x4 to 9x9 grids.

**Target Audience**

* Casual mobile users (1–5 minute sessions)
* Puzzle players seeking lightweight cognitive challenges
* Users tolerant of optional monetization via boosts

**Value Proposition**

* Familiar Sudoku with faster session cycles
* Low onboarding friction (simple rules, progressive difficulty)
* Coins provide **utility-based enhancements** (time, hints, recovery), improving performance without restricting access

---

## 2. Gameplay Design

**Core Gameplay Loop**

1. User starts a game
2. System generates Sudoku grid (based on difficulty)
3. Player fills cells
4. System validates input in real-time
5. Score and streak updated
6. Game ends when:

   * Puzzle completed OR
   * Time expires OR
   * Mistakes exceeded
7. Coins rewarded based on performance

---

**Coin Economy**

**Earn:**

* Completing puzzles
* High accuracy (low mistakes)
* Streak bonuses
* Daily challenge rewards

**Spend:**

* Add time (+5 seconds)
* Hint (reveal correct value)
* Undo mistake
* Continue after failure

---

**Progression System**

* Difficulty tiers:

  * Easy (4x4)
  * Medium (6x6)
  * Hard (9x9)
* Unlock progression based on completed puzzles
* Score-based performance tracking

---

**Engagement Mechanics**

* Timer pressure
* Mistake limit (e.g., 3)
* Streak multiplier
* Daily challenge with bonus coins

---

## 3. Monetization Design (Constrained)

**Model**

* Consumable coin purchases via Google Play Billing
* Predefined coin packages (coins_100 → coins_4000)

---

**Integration into Gameplay**

* Coins enhance gameplay efficiency (not access)
* All puzzles playable without coins
* Monetization tied to:

  * Failure recovery
  * Time pressure mitigation
  * Assistance (hints)

---

**Coin Usage Design**

* Optional and contextual (shown only when relevant)
* Immediate effect after use
* Clear value feedback (e.g., +5 seconds visibly applied)

---

**Compliance**

* No gameplay blocking
* Purchases are repeatable consumables
* Localized pricing via Billing API

---

## 4. Feature Breakdown

### MVP Features (High Priority)

* Sudoku gameplay engine (4x4, 6x6)
* Real-time validation
* Timer and mistake system
* Coin system (earn/spend)
* **Coin Shop (critical path)**
* Google Play Billing integration

---

### Secondary Features

* 9x9 difficulty
* Daily challenge
* Stats tracking
* Streak/score multipliers
* Sound effects

---

### Dependencies

* Coin system depends on:

  * Billing integration
  * Local persistence
* Boost features depend on:

  * Coin balance system
* Gameplay depends on:

  * Sudoku generator

---

## 5. Screen Definition (8 Screens)

**1. Home Screen**

* Entry point
* Start game, view coins, navigation

---

**2. Game Screen**

* Sudoku grid
* Timer, score, mistakes
* Input controls and boost buttons

---

**3. Result Screen**

* Summary of performance
* Coins earned
* Retry / continue

---

**4. Coin Shop Screen**

* Display coin packages
* Trigger purchase flow

---

**5. Difficulty Selection Screen**

* Select 4x4 / 6x6 / 9x9
* Entry before gameplay

---

**6. Daily Challenge Screen**

* Fixed puzzle
* Bonus rewards

---

**7. Stats Screen**

* High scores
* Completed games
* Best streak

---

**8. Settings Screen**

* Sound toggle
* Reset progress

---

## 6. User Flow

**First-Time User**

1. Launch app
2. Land on Home Screen
3. Select difficulty
4. Start first game (no onboarding required)

---

**Gameplay Loop**
Home → Difficulty Select → Game → Result → (Replay or Home)

---

**Coin Purchase Flow**

1. User opens Coin Shop
2. Products loaded via Billing API
3. User selects package
4. Purchase flow triggered
5. On success:

   * Coins added
   * UI updated immediately

---

## 7. Requirements (BA Perspective)

### Functional Requirements

* Generate valid Sudoku puzzles dynamically
* Validate user input in real-time
* Track score, time, mistakes
* Maintain coin balance
* Support consumable IAP products
* Apply boosts instantly upon usage

---

### Non-Functional Requirements

* Input response time < 100ms
* Smooth UI rendering (Compose)
* Offline gameplay (except billing)
* Stable purchase handling (retry, failure states)

---

### Assumptions

* No backend required (local-first architecture)
* Sudoku difficulty scaling handled locally
* Users understand basic Sudoku rules

---

### Limitations

* No cloud sync
* No multiplayer
* Limited analytics (local only in MVP)

---

## 8. Prioritization (PO Perspective)

### MVP Scope

1. Core gameplay (4x4, 6x6)
2. Coin system
3. **Coin Shop + Billing integration**
4. Result and scoring system

---

### Priority Rationale

**Highest Priority**

* Coin Shop + Billing

  * Direct revenue impact
  * Required early validation

**High Priority**

* Core gameplay loop

  * User retention driver

**Medium Priority**

* Boost mechanics

  * Monetization engagement

**Low Priority**

* Stats, daily challenge

  * Retention optimization

---

## 9. Risks and Considerations

**Monetization Risk**

* Low conversion if coin value unclear
* Mitigation: ensure boosts provide visible, immediate benefit

---

**UX Risk**

* Overuse of boosts may reduce challenge integrity
* Mitigation: limit boost frequency and maintain balance

---

**Technical Risk**

* Billing integration complexity (edge cases, pending transactions)
* Mitigation: implement full purchase lifecycle handling

---

**Gameplay Risk**

* Sudoku perceived as slow or repetitive
* Mitigation: introduce timer and progression scaling
