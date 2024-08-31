package com.devtorres.taskalarm.ui.dialog

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.AssigmentTask
import com.devtorres.taskalarm.data.model.SubTask
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.model.TaskValidationsBoolean
import com.devtorres.taskalarm.ui.components.DatePickerDialog
import com.devtorres.taskalarm.ui.components.TimePickerDialog
import com.devtorres.taskalarm.ui.viewmodel.TaskViewModel
import com.devtorres.taskalarm.util.TaskUtils.emptyValidationsState
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@SuppressLint("SimpleDateFormat", "NewApi", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun addTaskDialog(
    taskViewModel: TaskViewModel,
    task: Task? = null,
    closeDialog: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // FOR propiedades de la tarea
    var titleTask by remember {
        mutableStateOf(
            task?.title ?: ""
        )
    }
    // END FOR propiedades de la tarea
    var isSubTaskTitleEmpty by remember {
        mutableStateOf(false)
    }

    val listSubTask = remember {
        mutableStateListOf<SubTask>().apply {
            addAll(task?.subtasks ?: emptyList())
        }
    }

    var titleSub by remember {
        mutableStateOf("")
    }

    // FOR tip
    val toolTipState = rememberTooltipState(
        isPersistent = true
    )
    // END FOR tip

    // FOR dialog pickers
    var openDatePicker by remember {
        mutableStateOf(false)
    }

    var openTimePicker by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis =
            task?.finishDate?.toLocalDate()?.atStartOfDay()?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?:
            LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    var timePickerState = rememberTimePickerState(
        initialHour = task?.finishDate?.toLocalTime()?.hour ?: 7,
        initialMinute = task?.finishDate?.toLocalTime()?.minute ?: 0,
        is24Hour = false
    )
    // END FOR dialog pickers

    // FOR fecha y hora
    var selectedDate by remember {
        mutableStateOf(
            task?.finishDate?.toLocalDate()?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?:
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )
    }
    var selectedHour by remember {
        mutableStateOf(
            if(task?.finishDate == null) {
                LocalTime.of(timePickerState.hour,timePickerState.minute).format(
                    DateTimeFormatter.ofPattern("hh:mm a"))
            } else {
                task.finishDate.let {
                    LocalTime.of(it.hour,it.minute).format(
                        DateTimeFormatter.ofPattern("hh:mm a"))
                }
            }
        )
    }
    // END FOR fecha y hora

    // FOR calendario alarManager
    var calendar by remember {
        mutableStateOf(Calendar.getInstance())
    }
    // EN FOR calendario alarManager

    // FOR validaciones
    var validationsState by remember {
        mutableStateOf(TaskValidationsBoolean())
    }

    fun updateValidationState(title: Boolean, date: Boolean, time: Boolean, subtask: Boolean) {
        validationsState = TaskValidationsBoolean(title = title, date = date, time = time, subtask = subtask)
    }
    // END FOR validaciones

    // FOR asignaciones
    var assigment by remember {
        mutableStateOf(
            if(task == null){
                AssigmentTask()
            } else {
                AssigmentTask(date = true, hour = true, noreminder = false, subtask = true)
            }
        )
    }

    fun updateAssigments(date: Boolean, hour: Boolean, noreminder: Boolean, subtask: Boolean) {
        assigment = AssigmentTask(date, hour, noreminder, subtask)
        validationsState = emptyValidationsState
    }
    // END FOR asignaciones


    // FOR validaciones
    fun validateAndSaveTask() {
        // obtener hora y minuto del timepicker
        val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

        // convertir la fecha seleccionada a milisegundos
        val milis = datePickerState.selectedDateMillis ?: 0

        // convertir la fecha a un localdate
        val localDate = when {
            !assigment.date && assigment.hour -> LocalDate.now().plusDays(1)
            assigment.date -> Instant.ofEpochMilli(milis).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1)
            else -> LocalDate.now()
        }

        // obtener la fecha seleccionada
        calendar = Calendar.getInstance().apply {
            timeInMillis = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            set(Calendar.HOUR_OF_DAY, localTime.hour)
            set(Calendar.MINUTE, localTime.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val expiredCalendar = calendar.clone() as Calendar

        val precalendar = calendar.apply {
            add(Calendar.HOUR_OF_DAY, -1)
        }

        val isCurrentDate = localDate == LocalDate.now()
        val isPastDate = localDate < LocalDate.now()
        val isTimeValid = if (assigment.date && assigment.hour) {
            if (isCurrentDate || isPastDate) validationsState.isTime(localTime) else validationsState.isDateTime(localDate, localTime)
        } else true

        updateValidationState(
            validationsState.isNoAssigment(titleTask),
            if (assigment.date) validationsState.isDate(localDate) else true,
            isTimeValid,
            if(assigment.subtask) !listSubTask.isEmpty() else true
        )

        if(validationsState.isValid()){
            // crear una tarea para guardarla
            val currentTask = Task(
                id = task?.id ?: 0,
                title = titleTask,
                subtasks = listSubTask,
                isCompleted = false,
                reminder = assigment.date || assigment.hour,
                finishDate = localDate.atTime(localTime.hour, localTime.minute),
                expired = false
            )

            // llamar la implementacion de agregar tarea del viewmodel
            if(task == null){
                taskViewModel.addtask(
                    task = currentTask,
                    content = context.getString(R.string.lblTaskAdd),
                    context = context,
                    expiredCalendar = expiredCalendar,
                    preCalendar = precalendar,
                    message = context.getString(R.string.lblTaskExpired),
                    preMessage = context.getString(R.string.lblPreTaskExpired),
                )
            } else {
                taskViewModel.updateTask(
                    task = currentTask,
                    content = context.getString(R.string.lblUpdatedTask),
                    context = context,
                    expiredCalendar = expiredCalendar,
                    preCalendar = precalendar,
                    message = context.getString(R.string.lblTaskExpired),
                    preMessage = context.getString(R.string.lblPreTaskExpired),
                )
            }

            // limpiar la caja de texto
            titleTask = ""

            // cerrar dialogo
            closeDialog()
        }
    }
    // END FOR validaciones

    if(openDatePicker) {
        DatePickerDialog(
            datePickerState = datePickerState,
            closeDialog = {
                openDatePicker = false
            },
            selectedDate = {
                selectedDate = it
            }
        )
    }

    if(openTimePicker){
        TimePickerDialog(
            timePickerState = timePickerState,
            closeDialog = { openTimePicker = false },
            selectedHour = {
                selectedHour = it
            }
        )
    }


    Dialog(onDismissRequest = { closeDialog() }) {
        OutlinedCard {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // titulo e informacion extra
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.lblTask),
                        style = typography.bodyLarge,
                        fontWeight = FontWeight.W900
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            RichTooltip(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.lblDefaultDateTime),
                                        style = typography.titleMedium,
                                        fontWeight = FontWeight.W900
                                    )
                                },
                                text = {
                                    Text(text = getFormattedAnnotatedString())
                                }
                            )
                        },
                        state = toolTipState
                    ) {
                        IconButton(onClick = { scope.launch { toolTipState.show() } }) {
                            Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                        }
                    }
                }

                // chips para fecha hora o instantanea
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // FOR Fecha
                    FilterChip(
                        selected = assigment.date,
                        onClick = {
                            selectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            if(assigment.date){
                                updateAssigments(date = false, hour = assigment.hour, noreminder = !assigment.hour, subtask = assigment.subtask)
                            } else {
                                updateAssigments(date = true, hour = assigment.hour, noreminder = false, subtask = assigment.subtask)
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.fchDate))
                        }
                    )
                    // END FOR fecha

                    // FOR Hora
                    FilterChip(
                        selected = assigment.hour,
                        onClick = {
                            selectedHour = "07:00 a.m."
                            timePickerState = TimePickerState(
                                initialHour = 7,
                                initialMinute = 0,
                                is24Hour = false
                            )
                            if(assigment.hour){
                                updateAssigments(date = assigment.date, hour = false, noreminder = !assigment.date, subtask = assigment.subtask)
                            } else {
                                updateAssigments(date = assigment.date, hour = true, noreminder = false, subtask = assigment.subtask)
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.fchTime))
                        }
                    )
                    // END FOR Hora

                    // FOR Sin aviso
                    FilterChip(
                        selected = assigment.noreminder,
                        onClick = {
                            updateAssigments(date = false, hour = false, noreminder = true, subtask = false)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.fchNoReminder))
                        }
                    )
                    // END FOR Sin aviso

                    // FOR subtareas
                    FilterChip(
                        selected = assigment.subtask,
                        onClick = {
                            if(assigment.subtask){
                                listSubTask.clear()
                                updateAssigments(date = assigment.date, hour = assigment.hour, noreminder = assigment.noreminder, subtask = false)
                            } else {
                                updateAssigments(date = assigment.date, hour = assigment.hour, noreminder = assigment.noreminder, subtask = true)
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.lblAddSubTask),
                            )
                        }
                    )
                    // END FOR subtareas
                }

                // mensaje de aviso previo de notificacion
                AnimatedVisibility(visible = !assigment.noreminder) {
                    Text(text = stringResource(id = R.string.lblPreTaskWarn), textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.size(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = titleTask,
                    onValueChange = { titleTask = it },
                    label = {
                        Text(text = stringResource(id = R.string.lblTaskName))
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.txtTaskName),
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.5f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(visible = !validationsState.title) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = stringResource(id = R.string.lblTitleEmpty),
                            style = typography.labelSmall,
                            color = colorScheme.error
                        )
                    }
                }

                // fecha
                AnimatedVisibility(visible = assigment.date) {
                    Column {
                        Spacer(modifier = Modifier.size(16.dp))

                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = {  },
                            readOnly = true,
                            label = {
                                Text(text = stringResource(id = R.string.lblDate))
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.txtDate),
                                    modifier = Modifier
                                        .graphicsLayer(alpha = 0.5f)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { openDatePicker = true }) {
                                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AnimatedVisibility(visible = !validationsState.date) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = stringResource(id = R.string.lblDateWrong),
                                    style = typography.labelSmall,
                                    color = colorScheme.error
                                )
                            }
                        }
                    }
                }

                // hora
                AnimatedVisibility(visible = assigment.hour) {
                    Column {
                        Spacer(modifier = Modifier.size(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedHour,
                                style = typography.bodyLarge,
                                fontWeight = FontWeight.W500
                            )

                            FilledTonalIconButton(onClick = { openTimePicker = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreTime,
                                    contentDescription = stringResource(id = R.string.iconClock)
                                )
                            }
                        }

                        AnimatedVisibility(visible = !validationsState.time) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = stringResource(id = R.string.lblTimeWrong),
                                    style = typography.labelSmall,
                                    color = colorScheme.error
                                )
                            }
                        }
                    }
                }

                // subtareas
                AnimatedVisibility(visible = assigment.subtask) {
                    Spacer(modifier = Modifier.size(16.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.size(16.dp))
                    
                    Column {
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(id = R.string.lblSubTask),
                            style = typography.titleMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                OutlinedTextField(
                                    value = titleSub,
                                    onValueChange = { titleSub = it },
                                    label = {
                                        Text(text = stringResource(id = R.string.lblSubTaskName))
                                    },
                                    placeholder = {
                                        Text(
                                            text = stringResource(id = R.string.txtSubTaskName),
                                            modifier = Modifier
                                                .graphicsLayer(alpha = 0.5f)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                )

                                AnimatedVisibility(visible = isSubTaskTitleEmpty) {
                                    Text(
                                        text = stringResource(id = R.string.lblTitleEmpty),
                                        style = typography.labelSmall,
                                        color = colorScheme.error
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.size(8.dp))

                            FilledTonalIconButton(
                                onClick = {
                                    if(titleSub.isEmpty()){
                                        isSubTaskTitleEmpty = true
                                    } else {
                                        listSubTask.add(
                                            SubTask(
                                                title = titleSub
                                            )
                                        )
                                        isSubTaskTitleEmpty = false
                                        titleSub = ""
                                    }
                                },
                                shape = OutlinedTextFieldDefaults.shape
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Done,
                                    contentDescription = stringResource(id = R.string.iconAddSubTask)
                                )
                            }
                        }

                        AnimatedVisibility(visible = !validationsState.subtask) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = stringResource(id = R.string.lblSubTaskWrong),
                                    style = typography.labelSmall,
                                    color = colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 100.dp)
                        ) {
                            items(listSubTask) { sub ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sub.title,
                                        maxLines = 2,
                                        style = typography.labelMedium
                                    )

                                    IconButton(onClick = { listSubTask.remove(sub) }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = stringResource(id = R.string.iconDeleteSubTask)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.size(32.dp))

                Button(
                    onClick = {
                        validateAndSaveTask()
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

@Composable
fun getFormattedAnnotatedString(): AnnotatedString {
    val text = stringResource(id = R.string.lblDefaultDateTimeText)

    return buildAnnotatedString {
        // Reemplazar los marcadores específicos en el texto para aplicar estilos
        val boldParts = listOf(
            "Puedes elegir no",
            "Si no eliges una fecha",
            "Si no eliges una hora"
        )

        var startIndex = 0

        boldParts.forEach { boldPart ->
            val start = text.indexOf(boldPart, startIndex)
            if (start != -1) {
                val end = start + boldPart.length

                // Añadir texto antes del marcador, si hay
                if (start > startIndex) {
                    append(text.substring(startIndex, start))
                }

                // Aplicar negrita al marcador
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = colorScheme.primary)) {
                    append(text.substring(start, end))
                }

                startIndex = end
            }
        }

        // Añadir el texto restante después del último marcador
        if (startIndex < text.length) {
            append(text.substring(startIndex))
        }
    }
}