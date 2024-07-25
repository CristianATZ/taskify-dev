package com.devtorres.taskalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devtorres.taskalarm.data.database.AppDataBase
import com.devtorres.taskalarm.data.database.TaskDao
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.repository.TaskRepository
import com.devtorres.taskalarm.data.repository.TaskRepositoryImpl
import com.devtorres.taskalarm.ui.task.TaskScreen
import com.devtorres.taskalarm.ui.task.TaskState
import com.devtorres.taskalarm.ui.task.TaskViewModel
import com.devtorres.taskalarm.ui.task.TaskViewModelFactory
import com.devtorres.taskalarm.ui.theme.TaskAlarmTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val taskRepository by lazy {
        TaskRepositoryImpl.getInstance(AppDataBase.getInstance(this).taskDao())
    }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(taskRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()
        setContent {
            TaskAlarmTheme {
                TaskScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskAlarmTheme {
        Greeting("Android")
    }
}