package com.devtorres.taskalarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devtorres.taskalarm.ui.task.TaskScreen
import com.devtorres.taskalarm.ui.viewmodel.TaskViewModel
import com.devtorres.taskalarm.ui.task.SettingsScreen
import com.devtorres.taskalarm.ui.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    taskViewModel: TaskViewModel,
    settingsViewModel: SettingsViewModel,
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Destinations.Home.route) {
        composable(route = Destinations.Home.route){
            TaskScreen(taskViewModel = taskViewModel)
        }

        composable(route = Destinations.Settings.route){
            SettingsScreen(settingsViewModel = settingsViewModel)
        }
    }
}