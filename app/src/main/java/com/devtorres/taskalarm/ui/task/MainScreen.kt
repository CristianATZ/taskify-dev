package com.devtorres.taskalarm.ui.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.ui.navigation.Destinations
import com.devtorres.taskalarm.ui.navigation.NavGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navHostController: NavHostController,
    taskViewModel: TaskViewModel,
    settingsViewModel: SettingsViewModel
) {
    Scaffold(
        bottomBar = {
            BottomBarApp(navHostController)
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            NavGraph(
                taskViewModel = taskViewModel,
                settingsViewModel = settingsViewModel,
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun BottomBarApp(navHostController: NavHostController) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomAppBar(
        actions = {
            NavigationBarItem(
                selected = currentRoute == Destinations.Home.route,
                onClick = { navHostController.navigate(Destinations.Home.route) },
                icon = {
                    Icon(
                        imageVector = if(currentRoute == Destinations.Home.route) Icons.Filled.Task else Icons.Outlined.Task,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.lblTask)
                    )
                }
            )

            NavigationBarItem(
                selected = currentRoute == Destinations.Theme.route,
                onClick = { navHostController.navigate(Destinations.Theme.route) },
                icon = {
                    Icon(
                        imageVector = if(currentRoute == Destinations.Theme.route) Icons.Filled.DarkMode else Icons.Outlined.DarkMode,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.lblTheme)
                    )
                }
            )

        }
    )
}