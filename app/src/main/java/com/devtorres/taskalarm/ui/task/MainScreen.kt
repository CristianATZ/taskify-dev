package com.devtorres.taskalarm.ui.task

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.ui.dialog.AddTaskDialog
import com.devtorres.taskalarm.ui.navigation.Destinations
import com.devtorres.taskalarm.ui.navigation.NavGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navHostController: NavHostController,
    taskViewModel: TaskViewModel,
    settingsViewModel: SettingsViewModel
) {
    Scaffold(
        bottomBar = {
            /*BottomBarApp(
                navHostController
            )*/
            BottomBarCustom(navHostController, taskViewModel)
        }
    ) {
        Column(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        ) {
            NavGraph(
                taskViewModel = taskViewModel,
                settingsViewModel = settingsViewModel,
                navHostController = navHostController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomBarCustom(navHostController: NavHostController, taskViewModel: TaskViewModel) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainerHighest
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .height(75.dp)
                    .width(150.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .background(
                                if (currentRoute == Destinations.Home.route) colorScheme.primary else Color.Transparent
                            )
                            .height(5.dp)
                    )

                    IconButton(
                        onClick = { navHostController.navigate(Destinations.Home.route) },
                        modifier = Modifier
                            .size(75.dp)
                    ) {
                        Icon(
                            imageVector = if(currentRoute == Destinations.Home.route) Icons.Filled.Task else Icons.Outlined.Task,
                            contentDescription = null
                        )
                    }
                }

                VerticalDivider(modifier = Modifier.fillMaxHeight(0.5f))

                Column {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (currentRoute == Destinations.Settings.route) colorScheme.primary else Color.Transparent
                            )
                            .height(5.dp)
                    )

                    IconButton(
                        onClick = { navHostController.navigate(Destinations.Settings.route) },
                        modifier = Modifier
                            .size(75.dp)
                    ) {
                        Icon(
                            imageVector = if(currentRoute == Destinations.Settings.route) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(32.dp))

        Column(
            modifier = Modifier.size(75.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FloatingActionApp(taskViewModel = taskViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FloatingActionApp(taskViewModel: TaskViewModel) {
    var openDialog by remember {
        mutableStateOf(false)
    }

    if(openDialog){
        AddTaskDialog(
            taskViewModel = taskViewModel,
            closeDialog = { openDialog = false }
        )
    }

    FloatingActionButton(
        onClick = { openDialog = !openDialog },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomBarApp(
    navHostController: NavHostController
) {
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
                        text = stringResource(id = R.string.lblTasks)
                    )
                }
            )

            NavigationBarItem(
                selected = currentRoute == Destinations.Settings.route,
                onClick = { navHostController.navigate(Destinations.Settings.route) },
                icon = {
                    Icon(
                        imageVector = if(currentRoute == Destinations.Settings.route) Icons.Filled.DarkMode else Icons.Outlined.DarkMode,
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
