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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.ui.theme.TaskAlarmTheme
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen() {
    val taskList = remember {
        mutableStateListOf(
            Task(id = 1, title = "Comprar víveres", isCompleted = false, date = LocalDateTime.now().minusDays(1)),
            Task(id = 2, title = "Reunión con equipo", isCompleted = true, date = LocalDateTime.now().minusDays(2)),
            Task(id = 3, title = "Llamar al doctor", isCompleted = false, date = LocalDateTime.now().minusDays(3)),
            Task(id = 4, title = "Ejercicio en el gimnasio", isCompleted = true, date = LocalDateTime.now().minusDays(4)),
            Task(id = 5, title = "Leer libro", isCompleted = false, date = LocalDateTime.now().minusDays(5)),
            Task(id = 6, title = "Preparar presentación", isCompleted = true, date = LocalDateTime.now().minusDays(6)),
            Task(id = 7, title = "Hacer la colada", isCompleted = false, date = LocalDateTime.now().minusDays(7)),
            Task(id = 8, title = "Limpiar la casa", isCompleted = true, date = LocalDateTime.now().minusDays(8)),
            Task(id = 9, title = "Escribir informe", isCompleted = false, date = LocalDateTime.now().minusDays(9)),
            Task(id = 10, title = "Revisar correos electrónicos", isCompleted = true, date = LocalDateTime.now().minusDays(10)),
            Task(id = 11, title = "Organizar escritorio", isCompleted = false, date = LocalDateTime.now().minusDays(11)),
            Task(id = 12, title = "Visitar a los abuelos", isCompleted = true, date = LocalDateTime.now().minusDays(12)),
            Task(id = 13, title = "Pagar facturas", isCompleted = false, date = LocalDateTime.now().minusDays(13)),
            Task(id = 14, title = "Ir al banco", isCompleted = true, date = LocalDateTime.now().minusDays(14)),
            Task(id = 15, title = "Configurar nueva computadora", isCompleted = false, date = LocalDateTime.now().minusDays(15)),
            Task(id = 16, title = "Cocinar cena", isCompleted = true, date = LocalDateTime.now().minusDays(16)),
            Task(id = 17, title = "Estudiar para el examen", isCompleted = false, date = LocalDateTime.now().minusDays(17)),
            Task(id = 18, title = "Llevar el coche al taller", isCompleted = true, date = LocalDateTime.now().minusDays(18)),
            Task(id = 19, title = "Actualizar currículum", isCompleted = false, date = LocalDateTime.now().minusDays(19)),
            Task(id = 20, title = "Jugar videojuegos", isCompleted = true, date = LocalDateTime.now().minusDays(20))
        )
    }

    val taskUncompleted = remember {
        mutableStateListOf<Task>().apply {
            addAll(taskList.filter { !it.isCompleted }.sortedByDescending { it.date })
        }
    }

    val taskCompleted = remember {
        mutableStateListOf<Task>().apply {
            addAll(taskList.filter { it.isCompleted }.sortedByDescending { it.date })
        }
    }

    var idAction by remember {
        mutableIntStateOf(-1)
    }


    Scaffold(
        topBar = {
            TopBarApp(
                taskActions = idAction,
                taskCompleted = { isCompleted ->
                    val updatedTask = taskUncompleted[idAction].copy(isCompleted = isCompleted)
                    Log.d("OBJETO", updatedTask.toString())
                    taskUncompleted[idAction] = updatedTask
                    taskCompleted.add(0, updatedTask)
                    taskUncompleted.removeAt(idAction)
                    idAction = -1
                },
                taskDeleted = {
                    taskUncompleted.removeAt(idAction)
                    idAction = -1
                }
            )
        },
        bottomBar = {
            BottomBarApp()
        },
        floatingActionButton = {
            FloatingActionApp()
        }
    ) { padding ->
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
                items( taskUncompleted.size ){ index ->
                    TaskObject(
                        task = taskUncompleted[index],
                        idAction = idAction,
                        index = index,
                        updateAction = {
                            idAction = it
                        },
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.lblTaskCompleted),
                        style = typography.labelLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // tareas completadas
                items(taskCompleted.size) { index ->
                    TaskObject(
                        task = taskCompleted[index]
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskObject(
    task: Task,
    idAction: Int = -1,
    index: Int = -1,
    updateAction: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(0.9f)
            .heightIn(50.dp)
            .combinedClickable(
                onClick = {
                    updateAction(-1)
                },
                onLongClick = {
                    updateAction(index)
                }
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if(idAction != index || task.isCompleted) colorScheme.surfaceContainerLow else colorScheme.inverseSurface
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
    taskActions: Int,
    taskCompleted: (Boolean) -> Unit = {},
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
            if(taskActions != -1){
                // borrar tarea
                IconButton(onClick = { taskDeleted() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        tint = colorScheme.error,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }

                // terminar tarea
                IconButton(onClick = { taskCompleted(true) }) {
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

@Composable
fun FloatingActionApp() {
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
                        onClick = { openDialog = !openDialog },
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

@Preview
@Composable
fun BottomBarApp() {
    BottomAppBar(
        actions = {
            NavigationBarItem(
                selected = false,
                onClick = { /*TODO*/ },
                icon = {
                    Icon(imageVector = Icons.Outlined.Task, contentDescription = null)
                },
                label = {
                    Text(text = stringResource(id = R.string.lblTask))
                }
            )

            NavigationBarItem(
                selected = false,
                onClick = { /*TODO*/ },
                icon = {
                    Icon(imageVector = Icons.Outlined.DarkMode, contentDescription = null)
                },
                label = {
                    Text(text = stringResource(id = R.string.lblTheme))
                }
            )
            
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun TaskDarkPreview() {
    TaskAlarmTheme(true) {
        TaskScreen()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun TaskLightPreview() {
    TaskAlarmTheme(false) {
        TaskScreen()
    }
}
