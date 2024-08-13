package com.devtorres.taskalarm.ui.task

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.DoneOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.DateFilter
import com.devtorres.taskalarm.data.model.Filters
import com.devtorres.taskalarm.data.model.StatusFilter
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.model.TypeFilter
import com.devtorres.taskalarm.ui.dialog.AboutDialog
import com.devtorres.taskalarm.ui.theme.doneScheme
import com.devtorres.taskalarm.util.TaskUtils.emptyTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(taskViewModel: TaskViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTask by remember { mutableStateOf(emptyTask) }
    val taskUiState by taskViewModel.uiState.collectAsState()
    var filters by remember { mutableStateOf(Filters()) }
    val snackbarHostState = remember { SnackbarHostState() }

    val shareLauncher = getShareLauncher(
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
    }.sortedByDescending { task -> task.finishDate }

    // FOR BOTTOM SHEET
    var isDeleted by remember {
        mutableStateOf(false)
    }

    // Mostrar el mensaje de eliminación durante
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            delay(2000) // 5 segundos
            isDeleted = false
        }
    }

    if(selectedTask.id != -1){
        val message = stringResource(id = R.string.lblTaskExpired)
        TaskActionsBottomSheet(
            selectedTask = selectedTask,
            onDismiss = {
                selectedTask = emptyTask
            },
            onComplete = {
                taskViewModel.updateTask(
                    task = selectedTask.copy(isCompleted = true)
                )
                selectedTask = emptyTask
            },
            onDelete = {
                if(selectedTask.reminder) taskViewModel.cancelNotification(context, selectedTask.title, message, selectedTask.id)
                taskViewModel.deleteTask(task = selectedTask)
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
                var resultActionSnack: SnackbarResult? = null

                resultActionSnack = if(close){
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
                                        )
                                )
                            }
                        }
                    }
                }
                // END FOR lista de tareas
            }

            AnimatedVisibility(
                visible = isDeleted,
                enter = slideInVertically(
                    initialOffsetY = { it }, // Enter from the bottom
                    animationSpec = tween(durationMillis = 300) // Duration of the slide in
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it }, // Exit to the bottom
                    animationSpec = tween(durationMillis = 300) // Duration of the slide out
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize(0.25f)
                            .background(colorScheme.error),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = null,
                            tint = colorScheme.onError,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        Text(
                            text = stringResource(id = R.string.lblTaskEliminated),
                            style = typography.headlineSmall,
                            color = colorScheme.onError
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FiltersColumn(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
    Column {
        // tipos
        FilterTypeRow(filters, updateFilters)

        // estatus
        FilterStatusRow(filters, updateFilters)

        // fecha
        FilterDateRow(filters, updateFilters)
    }
}

@Composable
fun FilterTypeRow(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
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
                if (filters.type == TypeFilter.NOREMINDER) {
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
                if (filters.type == TypeFilter.REMINDER) {
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
}

@Composable
fun FilterStatusRow(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // completadas
        FilterChip(
            selected = filters.status == StatusFilter.COMPLETED,
            onClick = {
                if (filters.status == StatusFilter.COMPLETED) {
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
                if (filters.status == StatusFilter.UNCOMPLETED) {
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
}

@Composable
fun FilterDateRow(
    filters: Filters,
    updateFilters: (TypeFilter, StatusFilter, DateFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // hoy
        FilterChip(
            selected = filters.date == DateFilter.TODAY,
            onClick = {
                if (filters.date == DateFilter.TODAY) {
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
                if (filters.date == DateFilter.WEEK) {
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
                if (filters.date == DateFilter.MONTH) {
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskActionsBottomSheet(
    selectedTask: Task,
    onDismiss: () -> Unit,
    onComplete: () -> Unit = {},
    onShare: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        properties = ModalBottomSheetProperties(
            isFocusable = true,
            securePolicy = SecureFlagPolicy.SecureOn,
            shouldDismissOnBackPress = true
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TaskItem(task = selectedTask)

            Spacer(modifier = Modifier.size(32.dp))

            if(!selectedTask.isCompleted && selectedTask.reminder){
                ActionButtonBottomSheet(
                    onClick = onComplete,
                    icon = Icons.Outlined.Done,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    textResId = R.string.btnDoneTask
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            ActionButtonBottomSheet(
                onClick = onShare,
                icon = Icons.Outlined.Share,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorScheme.tertiary,
                    contentColor = colorScheme.onTertiary
                ),
                textResId = R.string.btnShareTask
            )

            Spacer(modifier = Modifier.size(16.dp))

            ActionButtonBottomSheet(
                onClick = onDelete,
                icon = Icons.Outlined.Delete,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorScheme.error,
                    contentColor = colorScheme.onError
                ),
                textResId = R.string.btnDeleteTask
            )

            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}
@Composable
fun ActionButtonBottomSheet(
    onClick: () -> Unit,
    icon: ImageVector,
    textResId: Int,
    colors: ButtonColors,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = { onClick() },
        shape = CardDefaults.shape,
        colors = colors,
        modifier = modifier
            .fillMaxWidth(0.95f)
            .height(50.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = stringResource(id = textResId))
    }
}

@Composable
fun getShareLauncher(
    context: Context,
    taskSuccess: String,
    taskFailure: String,
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, taskSuccess, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, taskFailure, Toast.LENGTH_SHORT).show()
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    selectedTask: Task = emptyTask,
) {
    val localDate = task.finishDate.toLocalDate()
    val localTime = task.finishDate.toLocalTime()

    val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
        .uppercase(Locale.getDefault())

    val month = localDate.month.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
        .uppercase(Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .border(
                        if (selectedTask == task) 2.dp else 1.dp,
                        if (selectedTask == task) colorScheme.inversePrimary else colorScheme.outline.copy(
                            0.25f
                        ),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                // dia y hora
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayOfWeek,
                        style = typography.headlineSmall,
                        fontWeight = FontWeight.W900,
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.75f)
                    )
                    Text(
                        text = localTime.toString(),
                        style = typography.labelMedium,
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                // titulo tarea
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = task.title,
                        style = typography.labelMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            if(task.reminder){
                if(task.isCompleted){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(doneScheme.color),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DoneOutline,
                            contentDescription = null,
                            tint = doneScheme.onColor,
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(colorScheme.surfaceVariant),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = localDate.year.toString(),
                            style = typography.titleSmall,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.graphicsLayer(alpha = 0.5f)
                        )
                        Text(
                            text = localDate.dayOfMonth.toString(),
                            style = typography.headlineSmall,
                            fontWeight = FontWeight.W900,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.graphicsLayer(alpha = 0.75f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorScheme.primary),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = month,
                                style = typography.titleSmall,
                                fontWeight = FontWeight.W900,
                                letterSpacing = 4.sp,
                                color = colorScheme.onPrimary,
                                modifier = Modifier.graphicsLayer(alpha = 0.75f)
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(colorScheme.error),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsOff,
                        contentDescription = null,
                        tint = colorScheme.onError,
                        modifier = Modifier
                            .size(35.dp)
                            .graphicsLayer(alpha = 0.75f),
                    )
                }
            }
        }
    }
}