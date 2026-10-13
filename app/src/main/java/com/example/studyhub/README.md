# StudyHub 📚

**StudyHub** is a native Android task and assignment management application engineered with modern Android architecture standards (**MVVM**), **Jetpack Compose** (Material 3), and local persistence using **Room Database**.

Built with a strong focus on clean architecture, state preservation, and reactive UI streams, StudyHub allows students to keep track of course deliverables, filter tasks by subject, and manage deadlines offline.

---

## 🛠 Tech Stack & Architecture

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture Pattern:** Model-View-ViewModel (MVVM)
- **Local Persistence:** Room Database (SQLite)
- **Asynchronous & Reactive Streams:** Kotlin Coroutines & StateFlow
- **Design System:** Material 3 (`Scaffold`, `ModalBottomSheet`, `FilterChip`, `TopAppBar`)

---

## ✨ Key Technical Features

- **Reactive State Management:** Uses `StateFlow` to stream real-time database updates from Room directly to Compose composables (`collectAsState()`).
- **Dynamic Course Filtering:** Computes distinct course lists using `remember` blocks to derive UI state without unnecessary recompositions or secondary database calls.
- **Local SQLite Persistence:** Implements a single-source-of-truth local storage architecture via Room DAO operations (`@Insert`, `@Update`, `@Delete`, `@Query`).
- **Interactive Completion & Sorting:** Toggles assignment status with real-time `TextDecoration.LineThrough` rendering and automatic completion re-sorting (`ORDER BY isCompleted ASC, id DESC`).
- **Adaptive Material 3 Theme:** Full system light and dark mode adaptation built on top of `MaterialTheme` color schemes.

---

## 📱 Application Preview

| Main Task List (Light Mode) | Course Filtered View (Dark Mode) | Add Assignment Sheet |
| :---: | :---: | :---: |
| <img src="screenshots/screenshot_main_light.png" width="250"/> | <img src="screenshots/screenshot_filter_dark.png" width="250"/> | <img src="screenshots/screenshot_bottom_sheet.png" width="250"/> |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- Android SDK 26+ (Android 8.0 Oreo or higher)
- JDK 17

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/krishp0731/StudyHub.git](https://github.com/krishp0731/StudyHub.git)
   cd StudyHub