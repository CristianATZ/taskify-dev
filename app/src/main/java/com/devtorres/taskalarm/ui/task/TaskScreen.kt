package com.devtorres.taskalarm.ui.task

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.util.TaskUtils.emptyTask
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(taskViewModel: TaskViewModel) {
    val taskUiState by taskViewModel.uiState.collectAsState()

    val taskList = taskUiState.taskList

    val taskUncompleted = taskList.filter { !it.isCompleted }.sortedByDescending { it.date }

    val taskCompleted = taskList.filter { it.isCompleted }.sortedByDescending { it.date }

    var selectedTask by remember {
        mutableStateOf(emptyTask)
    }

    Scaffold(
        topBar = {
            TopBarApp(
                selectedTask = selectedTask,
                taskCompleted = {
                    Log.d("TaskViewModel", "complete: $selectedTask")
                    taskViewModel.updateTask(
                        task = selectedTask.copy(isCompleted = true)
                    )

                    selectedTask = emptyTask
                },
                taskDeleted = {
                    Log.d("TaskViewModel", "delete: $selectedTask")
                    taskViewModel.deleteTask(task = selectedTask)

                    selectedTask = emptyTask
                }
            )
        },
        floatingActionButton = {
            FloatingActionApp(taskViewModel)
        }
    ) { padding ->
        if(taskList.isEmpty()){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.lblNoTask),
                    textAlign = TextAlign.Center,
                    style = typography.displaySmall,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn {

                    item {
                        Text(
                            text = stringResource(id = R.string.lblTaskUncompleted),
                            style = typography.labelLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // tareas no completadas
                    items( taskUncompleted ){ task ->
                        TaskObject(
                            task = task,
                            selectedTask = selectedTask,
                            updateSelectedTask = {
                                selectedTask = it
                            }
                        )
                    }

                    item {
                        Text(
                            text = stringResource(id = R.string.lblTaskUncompleted),
                            style = typography.labelLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // tareas completadas
                    items( taskCompleted ){ task ->
                        TaskObject(
                            task = task,
                            selectedTask = selectedTask
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskObject(
    task: Task,
    selectedTask: Task,
    updateSelectedTask: (Task) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(0.9f)
            .heightIn(50.dp)
            .combinedClickable(
                onClick = {
                    updateSelectedTask(emptyTask)
                },
                onLongClick = {
                    Log.d("TaskViewModel", "antes: $task")
                    updateSelectedTask(task)
                }
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if(selectedTask != task || task.isCompleted) colorScheme.surfaceContainerLow else colorScheme.inverseSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.title,
                style = typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarApp(
    selectedTask: Task,
    taskCompleted: () -> Unit = {},
    taskDeleted: () -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var openAbout by remember {
        mutableStateOf(false)
    }

    if(openAbout) {
        Dialog(onDismissRequest = { openAbout = false }) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.infoAboutTitle),
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.size(32.dp))
                    
                    Text(
                        text = "${stringResource(id = R.string.lblVersion)}: ${stringResource(id = R.string.app_version)}"
                    )
                }
            }
        }
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.lblPendientes)
            )
        },
        actions = {
            IconButton(onClick = { expanded = true }) {
                IconButton(onClick = { openAbout = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }
            }
            if(selectedTask.id != -1){
                // borrar tarea
                IconButton(onClick = { taskDeleted() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        tint = colorScheme.error,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }

                // terminar tarea
                IconButton(onClick = { taskCompleted() }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        tint = Color.Green,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FloatingActionApp(taskViewModel: TaskViewModel) {
    var openDialog by remember {
        mutableStateOf(false)
    }

    var titleTask by remember {
        mutableStateOf("")
    }

    if(openDialog){
        Dialog(onDismissRequest = { openDialog = !openDialog }) {
            Card {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(0.95f)
                ) {
                    Text(text = stringResource(id = R.string.lblAddTask))

                    Spacer(modifier = Modifier.size(16.dp))

                    OutlinedTextField(
                        value = titleTask,
                        onValueChange = { titleTask = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )

                    Spacer(modifier = Modifier.size(32.dp))

                    Button(
                        onClick = {
                            taskViewModel.addtask(
                                Task(
                                    title = titleTask,
                                    isCompleted = false,
                                    date = LocalDateTime.now()
                                )
                            )
                            openDialog = !openDialog
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(50.dp)
                    ) {
                        Text(text = stringResource(id = R.string.btnAccept))
                    }
                }
            }
        }
    }

    FloatingActionButton(onClick = { openDialog = !openDialog }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
    }
}