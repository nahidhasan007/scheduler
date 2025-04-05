Scheduler is an app with functionalities
- The user can schedule any Android app which is installed on the device to start at a
specific time.
- The user can cancel the schedule if the scheduled app has not started.
- The user can change the time schedule of an existing scheduled app.
- It should support multiple schedules without time conflicts.
- The schedule record must be kept to query if the schedule was successfully

App Architecture
MVVM is used for the seperation of concern. UI And Business Logic in not seperated making more readibility and reusability
Jetpack compose used as UI Toolkit with 100% kotlin code
com.app.scheduler
---manifest -> declare Exact Alarm and Package permission
---backgroundservice -> handles schedule intent using BroadcastReceiver
---datalayer -> Model Classes for store Schedule Data
---domainlayer -> UI layer, and Activity class
---navigation -> Navcontroller for navigate between screen
---network -> Handles local and remote data access. Schedule Database mainly
---utils -> Extensions function for avoiding code duplication
---viewmodels -> Controll full business Logic for app scheduler(schedule, reschedule, crud db operations, cancel schedule, getInstalled apps)
