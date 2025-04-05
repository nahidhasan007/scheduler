# Android App Scheduler

This Android application allows users to schedule the launch of any installed app at a specific time. It provides functionalities to manage schedules, including cancellation and rescheduling.

## Features

* Schedule App Launch: Schedule any installed Android app to launch at a specified time.
* Cancel Schedule: Cancel any scheduled app launch before it triggers.
* Reschedule App Launch: Modify the scheduled time for an existing app launch.
* Multiple Schedules: Support for multiple schedules without time conflicts.
* Schedule History: Maintains a record of scheduled app launches for query purposes.

## Architecture

The application follows the MVVM (Model-View-ViewModel) architectural pattern for a clean separation of concerns.

* **MVVM (Model-View-ViewModel):**
    * Enhances code readability and reusability.
    * Separates UI logic from business logic.
* **Jetpack Compose:**
    * Used for building a modern and declarative UI.
    * Implemented entirely in Kotlin.

## Project Structure

com.app.scheduler/
├── manifest/
│   └── AndroidManifest.xml (Declares EXACT_ALARM and PACKAGE permissions)
├── backgroundservice/
│   └── AppScheduleLauncher.kt (Handles scheduled app launch intents via BroadcastReceiver)
├── datalayer/
│   └── AppSchedule.kt (Model classes for storing schedule data)
├── domainlayer/
│   └── UI components and Activity classes.
├── navigation/
│   └── Navigation components (NavController for screen navigation)
├── network/
│   └── Data access layer (Handles local and remote data, primarily schedule database)
├── utils/
│   └── Extension functions (For code reusability and avoiding duplication)
└── viewmodels/
└── SchedulerMainViewModel.kt (Manages app scheduling logic, including CRUD operations, installed app retrieval, and schedule management)


## Technologies Used

* Kotlin
* Jetpack Compose
* Android Architecture Components (ViewModel, Room)
* AlarmManager
* BroadcastReceiver

## Permissions

The application requires the following permissions:

* `android.permission.SCHEDULE_EXACT_ALARM`: To schedule precise alarms for app launches.
* `android.permission.QUERY_ALL_PACKAGES` or `android.permission.GET_INSTALLED_APPS`: To retrieve a list of installed applications.

## Getting Started

1.  **Clone the repository:**

    ```bash
    git clone [repository_url]
    ```

2.  **Open the project in Android Studio.**

3.  **Build and run the application on an Android emulator or device.**

