package com.app.scheduler.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.scheduler.domainlayer.ui.AppSchedulerUI
import com.app.scheduler.domainlayer.ui.ScheduleHistoryList

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME_SCREEN.name
    ) {
        composable(Routes.HOME_SCREEN.name) {
            AppSchedulerUI(navController = navController)
        }
        composable(Routes.SCHEDULE_HISTORY.name) {
            ScheduleHistoryList(navController = navController)
        }
    }
}