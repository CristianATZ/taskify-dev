package com.devtorres.taskalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.devtorres.taskalarm.ui.task.MainScreen
import com.devtorres.taskalarm.ui.task.SettingsViewModel
import com.devtorres.taskalarm.ui.task.SettingsViewModelFactory
import com.devtorres.taskalarm.ui.task.TaskViewModel
import com.devtorres.taskalarm.ui.task.TaskViewModelFactory
import com.devtorres.taskalarm.ui.theme.TaskAlarmTheme
import com.devtorres.taskalarm.util.NotificationHelper

class MainActivity : ComponentActivity() {

    private val app by lazy { application as MyApp }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(app.taskRepository)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(app)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannels(applicationContext)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()

            val isDarkTheme by settingsViewModel.theme.collectAsState(initial = false)

            TaskAlarmTheme(isDarkTheme) {
                MainScreen(
                    navHostController = navHostController,
                    taskViewModel = taskViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}