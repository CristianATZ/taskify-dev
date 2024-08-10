package com.devtorres.taskalarm.ui.task

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.DateFilter
import com.devtorres.taskalarm.data.model.Filters
import com.devtorres.taskalarm.data.model.StatusFilter
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.model.TypeFilter
import com.devtorres.taskalarm.ui.dialog.AboutDialog
import com.devtorres.taskalarm.ui.dialog.AddTaskDialog
import com.devtorres.taskalarm.util.TaskUtils.emptyTask
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(taskViewModel: TaskViewModel) {
    val context = LocalContext.current

    var selectedTask by remember { mutableStateOf(emptyTask) }
    val taskUiState by taskViewModel.uiState.collectAsState()
    var filters by remember { mutableStateOf(Filters()) }

    val shareLauncher = getShareLauncher(
        context = context,
        taskSuccess = stringResource(id = R.string.lblShareSuccess),
        taskFailure = stringResource(id = R.string.lblShareCancel),
        unSelectTask = { selectedTask = emptyTask }
    )

    val taskList = taskUiState.taskList

    // FOR filtros
    fun countActiveFilters(filters: Filters): Int {
        var count = 1
        if (filters.status != StatusFilter.NONE) count++
        if (filters.date != DateFilter.NONE) count++
        return count
    }

    var isVisibleFilter by remember {
        mutableStateOf(false)
    }

    // Calcular fechas
    val now = LocalDateTime.now()
    val day = now.dayOfMonth
    val previousSunday = now
        .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
        .withHour(23)
        .withMinute(59)
        .withSecond(59)
        .withNano(999999999)
    val nextMonday = now
        .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(1)
    val currentMonth = now.month
    Log.d("FECHA", "$now ${now.isAfter(previousSunday) && now.isBefore(nextMonday)} y $currentMonth")

    // Funcion de filtrado por tipo
    fun isTypeMatching(task: Task): Boolean {
        return when (filters.type) {
            TypeFilter.NOREMINDER -> !task.reminder
            TypeFilter.REMINDER -> task.reminder
            else -> true
        }
    }

    // Función de filtrado por estado
    fun isStatusMatching(task: Task): Boolean {
        return when (filters.status) {
            StatusFilter.COMPLETED -> task.isCompleted && task.reminder
            StatusFilter.UNCOMPLETED -> !task.isCompleted && task.reminder
            else -> true // no filtro por nada
        }
    }

    // Función de filtrado por fecha
    fun isDateWithinFilter(taskDate: LocalDateTime): Boolean {
        return when (filters.date) {
            DateFilter.TODAY -> taskDate.dayOfMonth == day
            DateFilter.WEEK -> taskDate.isAfter(previousSunday) && taskDate.isBefore(nextMonday)
            DateFilter.MONTH -> taskDate.month == now.month
            else -> true // No filtro por fecha
        }
    }

    // Función de actualización de filtros
    fun updateFilters(type: TypeFilter, status: StatusFilter, date: DateFilter) {
        filters = Filters(type, status, date)
    }

    // Filtrar la lista de tareas
    val filteredTasks = taskList.filter { task ->
        isTypeMatching(task) && isStatusMatching(task) && isDateWithinFilter(task.finishDate)
    }

    // Define el action que deseas recibir
    val systemAction = "com.devtorres.taskalarm.TASK_RECEIVER_UDPATED"

    // Usa el BroadcastReceiver para escuchar eventos del sistema
    SystemBroadcastReceiver(
        systemAction = systemAction,
        onSystemEvent = { intent ->
            // Aquí obtienes el ID de la tarea desde el Intent
            val taskId = intent?.getIntExtra("taskId", -1) ?: -1
            if (taskId != -1) {
                // Actualiza la lista de tareas en el ViewModel
                taskViewModel.refreshTask()
            }
        }
    )

    Scaffold(
        topBar = {
            TopBarApp(
                selectedTask = selectedTask,
                filtersCount = countActiveFilters(filters),
                taskCompleted = {
                    Log.d("TaskViewModel", "complete: $selectedTask")
                    taskViewModel.updateTask(
                        task = selectedTask.copy(isCompleted = true)
                    )
                    selectedTask = emptyTask
                },
                taskDeleted = {
                    Log.d("TaskViewModel", "delete: $selectedTask")

                    if(selectedTask.reminder) taskViewModel.cancelNotification(context, selectedTask.title, "Acaba de expirar", selectedTask.id)
                    taskViewModel.deleteTask(task = selectedTask)
                    selectedTask = emptyTask
                },
                shareInformation = {
                    taskViewModel.shareTask(selectedTask.title, shareLauncher)
                },
                hideFilters = {
                    isVisibleFilter = !isVisibleFilter
                }
            )
        },
        floatingActionButton = {
            FloatingActionApp(taskViewModel = taskViewModel)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            selectedTask = emptyTask
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // FOR lista de filtros
                if(isVisibleFilter){
                    // tipos
                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // todas
                        FilterChip(
                            selected = filters.type == TypeFilter.ALL,
                            onClick = {
                                updateFilters(TypeFilter.ALL, StatusFilter.NONE, DateFilter.NONE)
                            },
                            label = { Text(text = stringResource(id = R.string.fchAll)) },
                            leadingIcon = {
                                if (filters.type == TypeFilter.ALL) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )

                        // sin fecha
                        FilterChip(
                            selected = filters.type == TypeFilter.NOREMINDER,
                            onClick = {
                                if(filters.type == TypeFilter.NOREMINDER) {
                                    updateFilters(TypeFilter.ALL, StatusFilter.NONE, DateFilter.NONE)
                                } else {
                                    updateFilters(TypeFilter.NOREMINDER, StatusFilter.NONE, DateFilter.NONE)
                                }
                            },
                            label = { Text(text = stringResource(id = R.string.fchNoReminder)) },
                            leadingIcon = {
                                if (filters.type == TypeFilter.NOREMINDER) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )

                        // con fecha
                        FilterChip(
                            selected = filters.type == TypeFilter.REMINDER,
                            onClick = {
                                if(filters.type == TypeFilter.REMINDER) {
                                    updateFilters(TypeFilter.ALL, StatusFilter.NONE, DateFilter.NONE)
                                } else {
                                    updateFilters(TypeFilter.REMINDER, StatusFilter.NONE, DateFilter.NONE)
                                }
                            },
                            label = { Text(text = stringResource(id = R.string.fchReminder)) },
                            leadingIcon = {
                                if (filters.type == TypeFilter.REMINDER) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )
                    }

                    // estatus
                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // completadas
                        FilterChip(
                            selected = filters.status == StatusFilter.COMPLETED,
                            onClick = {
                                if(filters.status == StatusFilter.COMPLETED) {
                                    updateFilters(TypeFilter.ALL, StatusFilter.NONE, filters.date)
                                } else {
                                    updateFilters(TypeFilter.ALL, StatusFilter.COMPLETED, filters.date)
                                }
                            },
                            label = { Text(text = stringResource(id = R.string.fchCompleted)) },
                            leadingIcon = {
                                if (filters.status == StatusFilter.COMPLETED) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )

                        // sin completar
                        FilterChip(
                            selected = filters.status == StatusFilter.UNCOMPLETED,
                            onClick = {
                                if(filters.status == StatusFilter.UNCOMPLETED) {
                                    updateFilters(TypeFilter.ALL, StatusFilter.NONE, filters.date)
                                } else {
                                    updateFilters(TypeFilter.ALL, StatusFilter.UNCOMPLETED, filters.date)
                                }
                            },
                            label = { Text(text = stringResource(id = R.string.fchUncompleted)) },
                            leadingIcon = {
                                if (filters.status == StatusFilter.UNCOMPLETED) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )
                    }

                    // fecha
                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // hoy
                        FilterChip(
                            selected = filters.date == DateFilter.TODAY,
                            onClick = {
                                if(filters.date == DateFilter.TODAY){
                                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.NONE)
                                } else {
                                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.TODAY)
                                }
                            },
                            label = { Text(text = stringResource(id = R.string.fchToday)) },
                            leadingIcon = {
                                if (filters.date == DateFilter.TODAY) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )

                        // semana
                        FilterChip(
                            selected = filters.date == DateFilter.WEEK,
                            onClick = {
                                if(filters.date == DateFilter.WEEK){
                                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.NONE)
                                } else {
                                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.WEEK)
                                }
                            },
                            label = { Text(text = stringResource(id = R.string.fchWeek)) },
                            leadingIcon = {
                                if (filters.date == DateFilter.WEEK) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )

                        // mes
                        FilterChip(
                            selected = filters.date == DateFilter.MONTH,
                            onClick = {
                                if(filters.date == DateFilter.MONTH){
                                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.NONE)
                                } else {
                                    updateFilters(TypeFilter.ALL, filters.status, DateFilter.MONTH)
                                }
                            },
                            label = { Text(text = stringResource(id = R.string.fchMonth)) },
                            leadingIcon = {
                                if (filters.date == DateFilter.MONTH) {
                                    Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                                }
                            }
                        )
                    }
                }
                // END FOR lista de filtros

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // FOR lista de tareas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(filteredTasks){ task ->
                        TaskObject(
                            task = task,
                            selectedTask = selectedTask,
                            updateSelectedTask = { selectedTask = it }
                        )
                    }
                }
                // END FOR lista de tareas
            }
        }
    }
}

@Composable
fun getShareLauncher(
    context: Context,
    taskSuccess: String,
    taskFailure: String,
    unSelectTask: () -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, taskSuccess, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, taskFailure, Toast.LENGTH_SHORT).show()
        }
        unSelectTask()
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
            .fillMaxWidth(0.95f)
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
        border = if(selectedTask == task) BorderStroke(2.dp, colorScheme.secondary) else null
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
    filtersCount: Int,
    taskCompleted: () -> Unit = {},
    taskDeleted: () -> Unit = {},
    shareInformation: () -> Unit = {},
    hideFilters: () -> Unit
) {
    var openAbout by remember {
        mutableStateOf(false)
    }

    // dialogo con el numero de version
    if(openAbout) {
        AboutDialog(
            closeDialog = {
                openAbout = false
            }
        )
    }

    // barra superior
    TopAppBar(
        title = {
            // titulo
            Text(
                text = stringResource(id = R.string.lblPending),
                style = typography.headlineSmall,
                fontWeight = FontWeight.W900
            )
        },
        actions = {
            if(selectedTask.id == -1){
                // icono de filtros
                Box(
                    modifier = Modifier
                        .size(40.dp) // Tamaño del contenedor del icono
                        .clickable(onClick = { hideFilters() }),
                    contentAlignment = Alignment.Center
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                modifier = Modifier
                                    .offset(1.dp, 1.dp)
                            ) {
                                Text(text = "$filtersCount")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = stringResource(id = R.string.filterApplication)
                        )
                    }
                }


                // icono de informacion
                IconButton(onClick = { openAbout = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }
            }

            if(selectedTask.id != -1){
                // compartir tarea
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorScheme.inverseSurface
                    ),
                    onClick = {
                        shareInformation()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        tint = colorScheme.inverseOnSurface,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }

                // borrar tarea
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorScheme.errorContainer
                    ),
                    onClick = { taskDeleted() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        tint = colorScheme.onErrorContainer,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }

                // terminar tarea
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorScheme.primary
                    ),
                    onClick = { taskCompleted() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        tint = colorScheme.onPrimary,
                        contentDescription = stringResource(id = R.string.infoApplication)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FloatingActionApp(taskViewModel: TaskViewModel) {
    val context = LocalContext.current

    var openDialog by remember {
        mutableStateOf(false)
    }

    if(openDialog){
        AddTaskDialog(
            taskViewModel = taskViewModel,
            closeDialog = { openDialog = false }
        )
    }

    FloatingActionButton(onClick = { openDialog = !openDialog }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
    }
}