package com.devtorres.taskalarm.ui.task

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.DoneOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.DateFilter
import com.devtorres.taskalarm.data.model.Filters
import com.devtorres.taskalarm.data.model.StatusFilter
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.model.TypeFilter
import com.devtorres.taskalarm.ui.components.AnimatedStatusBanner
import com.devtorres.taskalarm.ui.components.FiltersColumn
import com.devtorres.taskalarm.ui.components.PermissionDialog
import com.devtorres.taskalarm.ui.components.SystemBroadcastReceiver
import com.devtorres.taskalarm.ui.components.TaskActionsBottomSheet
import com.devtorres.taskalarm.ui.components.TaskItem
import com.devtorres.taskalarm.ui.components.shareIntentResult
import com.devtorres.taskalarm.ui.dialog.AboutDialog
import com.devtorres.taskalarm.ui.dialog.addTaskDialog
import com.devtorres.taskalarm.ui.theme.doneScheme
import com.devtorres.taskalarm.ui.viewmodel.TaskViewModel
import com.devtorres.taskalarm.util.TaskUtils.emptyTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun TaskScreen(taskViewModel: TaskViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTask by remember { mutableStateOf(emptyTask) }
    val taskUiState by taskViewModel.uiState.collectAsState()
    var filters by remember { mutableStateOf(Filters()) }
    val snackbarHostState = remember { SnackbarHostState() }

    val shareLauncher = shareIntentResult(
        context = context,
        taskSuccess = stringResource(id = R.string.lblShareSuccess),
        taskFailure = stringResource(id = R.string.lblShareCancel)
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

    // Funcion de filtrado por tipo
    fun isTypeMatching(task: Task): Boolean {
        return when (filters.type) {
            TypeFilter.NOREMINDER -> !task.reminder
            TypeFilter.REMINDER -> task.reminder
            TypeFilter.EXPIRED -> task.expired
            else -> true
        }
    }

    // Función de filtrado por estado
    fun isStatusMatching(task: Task): Boolean {
        return when (filters.status) {
            StatusFilter.COMPLETED -> task.isCompleted && task.reminder && !task.expired
            StatusFilter.UNCOMPLETED -> !task.isCompleted && task.reminder && !task.expired
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
    }.sortedByDescending { task -> task.finishDate }

    // FOR BOTTOM SHEET
    var isDeleted by remember {
        mutableStateOf(false)
    }

    var isCompleted by remember {
        mutableStateOf(false)
    }

    // Mostrar el mensaje de eliminación durante
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            delay(2000) // 5 segundos
            isDeleted = false
        }
    }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            delay(2000) // 5 segundos
            isCompleted = false
        }
    }

    var openDialog by remember {
        mutableStateOf(false)
    }

    if(openDialog){
        addTaskDialog(
            taskViewModel = taskViewModel,
            task = selectedTask,
            closeDialog = {
                openDialog = false
                selectedTask = emptyTask
            }
        )
    }

    if(selectedTask.id != -1){
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        val expiredMessage = stringResource(id = R.string.lblTaskExpired)
        val preMessage = stringResource(id = R.string.lblPreTaskExpired)
        TaskActionsBottomSheet(
            sheetState = sheetState,
            selectedTask = selectedTask,
            onDismiss = {
                selectedTask = emptyTask
            },
            onUpdate = {
                openDialog = true
            },
            onComplete = {
                taskViewModel.completeTask(
                    task = selectedTask.copy(isCompleted = true),
                    context = context,
                    message = expiredMessage,
                    preMessage = preMessage
                )
                selectedTask = emptyTask
                isCompleted = true
            },
            onDelete = {
                taskViewModel.deleteTask(task = selectedTask, context = context, message = expiredMessage, preMessage = preMessage)
                selectedTask = emptyTask
                isDeleted = true
            },
            onShare = {
                taskViewModel.shareTask(selectedTask.title, shareLauncher)
            }
        )
    }
    // END FOR BOTTOM SHEET

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

    // Dialogo para la peticion de permisos
    PermissionDialog(
        showMessage = { message, messageAction, close ->
            scope.launch {

                val resultActionSnack: SnackbarResult = if(close){
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short,
                        actionLabel = messageAction
                    )
                } else {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }

                if(close && resultActionSnack == SnackbarResult.ActionPerformed){
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopBarApp(
                filtersCount = countActiveFilters(filters),
                hideFilters = {
                    isVisibleFilter = !isVisibleFilter
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // FOR lista de filtros
                AnimatedVisibility(visible = isVisibleFilter) {
                    FiltersColumn(filters = filters) { type, status, date ->
                        updateFilters(type, status, date)
                    }
                }
                // END FOR lista de filtros

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .graphicsLayer(alpha = 0.25f)
                ) {
                    Text(
                        text = "${filteredTasks.size} resultados",
                        style = typography.titleMedium
                    )
                }

                // FOR lista de tareas
                AnimatedContent(
                    targetState = filteredTasks,
                    label = "listAnimation",
                    transitionSpec = {
                        // Define la animación de transición
                        if (targetState.size > initialState.size) {
                            (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                        } else {
                            (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                        }
                    }
                ) { task ->
                    if(task.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.lblNoResults),
                                    style = typography.displayMedium,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.W900,
                                    modifier = Modifier.graphicsLayer(alpha = 0.25f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(task){ task ->
                                TaskItem(
                                    task = task,
                                    selectedTask = selectedTask,
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = {
                                                selectedTask = emptyTask
                                            },
                                            onLongClick = {
                                                selectedTask = task
                                            }
                                        ),
                                    onChangeSubTask = {
                                        taskViewModel.updateSubTask(task.id, it)
                                    }
                                )
                            }
                        }
                    }
                }
                // END FOR lista de tareas
            }

            AnimatedStatusBanner(
                isVisible = isDeleted,
                color = colorScheme.error,
                onColor = colorScheme.onError,
                icon = Icons.Outlined.DeleteSweep,
                text = stringResource(id = R.string.lblTaskEliminated)
            )

            AnimatedStatusBanner(
                isVisible = isCompleted,
                color = doneScheme.color,
                onColor = doneScheme.onColor,
                icon = Icons.Outlined.DoneOutline,
                text = stringResource(id = R.string.lblTaskCompleted)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarApp(
    filtersCount: Int,
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
    )
}