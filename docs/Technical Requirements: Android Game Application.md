# Technical Requirements: Android Game Application

## 1. Overview

The application shall be a **native Android game** built using **Kotlin** and **Jetpack Compose**, designed with **simple gameplay mechanics** and a monetization model centered on **virtual currency (coins)**.

The system must balance:

* Ease of use and low onboarding friction
* Clear in-game value for currency usage
* Scalable architecture for future enhancements

---

## 2. Platform and Architecture

### 2.1 Technology Stack

* Platform: **Android (native)**
* Programming Language: **Kotlin**
* UI Framework: **Jetpack Compose**
* Architecture Pattern: **MVVM (Model-View-ViewModel)**

### 2.2 Architectural Principles

* Clear separation of concerns between UI, domain, and data layers
* Reactive UI updates aligned with Compose best practices
* Modular and maintainable code structure to support feature expansion

---

## 3. Application Structure

* The application shall consist of **8 functional screens (UI views)**
* Each screen must have a **clearly defined role** within the user journey
* Navigation must follow a **consistent and maintainable structure**

### UI Scope Constraint

* The **Splash Screen** is classified as a **non-interactive initialization state**
* It shall **not be counted** as part of the 8 functional screens

---

## 4. Core Feature: Coin Shop (High Priority)

### 4.1 Priority Definition

* The **Coin Shop module** is the **highest-priority feature**
* It must be implemented and validated before lower-priority features
* All billing-related dependencies must be resolved early in development

### 4.2 Functional Requirements

The Coin Shop shall:

* Display available coin packages
* Initiate and manage purchase flows
* Handle transaction states (success, pending, failed, canceled)
* Update the user’s coin balance upon successful purchase

---

## 5. In-App Purchase (IAP) Configuration

### 5.1 Overview

The application shall support **consumable in-app purchases** for virtual currency using **Google Play Billing**.

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

---

### 5.3 Implementation Requirements

* All products shall be configured as **consumable items** in Google Play Console
* Product IDs must match exactly between the application and Play Console

The application shall:

* Query product details dynamically via **Google Play Billing API**
* Display **localized pricing** (no hardcoded price values)
* Handle purchase lifecycle:

  * Purchase initiation
  * Verification
  * Consumption

### 5.4 Purchase Handling

* Coins shall be granted **only after successful purchase verification**
* All consumable purchases must be **explicitly consumed** to allow repeat transactions

---

### 5.5 Validation and Compliance

* The implementation must comply with **Google Play Billing policies**
* Purchase verification is **required** (server-side recommended for production)

The system must handle:

* Canceled transactions
* Pending purchases
* Network or service interruptions

---

## 6. Game Design Constraints

* Gameplay must be **simple and intuitive**
* The core loop should **encourage voluntary coin usage**
* Coin-based actions (e.g., upgrades, unlocks) must provide **clear user value**
* Monetization must **not block core gameplay progression**

---

## 7. Assets and UI Standards

### 7.1 Visual Assets

* Image assets shall use **PNG format** to support transparency
* Assets should be optimized for **mobile performance and memory usage**

### 7.2 Audio Assets

* The application shall include **sound effects**
* Audio files must be optimized for **low latency and efficient playback**

### 7.3 Typography

* The primary font shall be **"Press Start 2P"**
* Font usage must remain consistent across all screens

---

## 8. Non-Functional Requirements

* The application must maintain **responsive performance** across supported Android devices
* UI interactions must be **smooth and stable**, including billing flows
* The system must be resilient to **runtime errors and connectivity issues**
* The architecture should support:

  * Future feature expansion
  * Additional monetization options
  * Maintainability and testability

---

## 9. Summary of Key Constraints

* Android native development using Kotlin and Jetpack Compose
* MVVM architecture is mandatory
* Coin Shop and IAP integration are **critical path features**
* Total functional screens: **8 (excluding splash screen)**
* All monetization must comply with **Google Play policies**

---
