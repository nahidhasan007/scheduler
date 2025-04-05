Scheduler is an app with functionalities
- The user can schedule any Android app which is installed on the device to start at a
specific time.
- The user can cancel the schedule if the scheduled app has not started.
- The user can change the time schedule of an existing scheduled app.
- It should support multiple schedules without time conflicts.
- The schedule record must be kept to query if the schedule was successfully

App Architecture
MVVM is used for the seperation of concern. UI And Business Logic in not seperated making more readibility and reusability\n
Jetpack compose used as UI Toolkit with 100% kotlin code\n
com.app.scheduler\n
---manifest -> declare Exact Alarm and Package permission\n
---backgroundservice -> handles schedule intent using BroadcastReceiver\n
---datalayer -> Model Classes for store Schedule Data\n
---domainlayer -> UI layer, and Activity class\n
---navigation -> Navcontroller for navigate between screen\n
---network -> Handles local and remote data access. Schedule Database mainly\n
---utils -> Extensions function for avoiding code duplication\n
---viewmodels -> Controll full business Logic for app scheduler(schedule, reschedule, crud db operations, cancel schedule, getInstalled apps)\n
