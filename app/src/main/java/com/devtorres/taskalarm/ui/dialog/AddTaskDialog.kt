package com.devtorres.taskalarm.ui.dialog

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.AssigmentTask
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.model.TaskValidationsBoolean
import com.devtorres.taskalarm.ui.task.TaskViewModel
import com.devtorres.taskalarm.util.TaskUtils.emptyValidationsState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat", "NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskDialog(
    taskViewModel: TaskViewModel,
    closeDialog: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // FOR propiedades de la tarea
    var titleTask by remember {
        mutableStateOf("")
    }
    // END FOR propiedades de la tarea

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

    var datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    var timePickerState = rememberTimePickerState(
        initialHour = 7,
        initialMinute = 0,
        is24Hour = false
    )
    // END FOR dialog pickers

    // FOR fecha y hora
    var selectedDate by remember {
        mutableStateOf(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )
    }
    var selectedHour by remember {
        mutableStateOf("07:00 a. m.")
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

    fun updateValidationState(title: Boolean, date: Boolean, time: Boolean) {
        validationsState = TaskValidationsBoolean(title = title, date = date, time = time)
    }
    // END FOR validaciones

    // FOR asignaciones
    var assigment by remember {
        mutableStateOf(AssigmentTask())
    }

    fun updateAssigments(date: Boolean, hour: Boolean, noreminder: Boolean) {
        assigment = AssigmentTask(date, hour, noreminder)
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
            isTimeValid
        )

        if(validationsState.isValid()){
            // crear una tarea para guardarla
            val currentTask = Task(
                title = titleTask,
                isCompleted = false,
                reminder = assigment.date || assigment.hour,
                finishDate = localDate.atTime(localTime.hour, localTime.minute)
            )

            // llamar la implementacion de agregar tarea del viewmodel
            taskViewModel.addtask(
                task = currentTask,
                content = context.getString(R.string.lblTaskAdd),
                context = context,
                expiredCalendar = expiredCalendar,
                preCalendar = precalendar,
                message = context.getString(R.string.lblTaskExpired),
                preMessage = context.getString(R.string.lblPreTaskExpired),
            )

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // FOR Fecha
                    FilterChip(
                        selected = assigment.date,
                        onClick = {
                            selectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            if(assigment.date){
                                updateAssigments(date = false, hour = assigment.hour, noreminder = !assigment.hour)
                            } else {
                                updateAssigments(date = true, hour = assigment.hour, noreminder = false)
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
                                updateAssigments(date = assigment.date, hour = false, noreminder = !assigment.date)
                            } else {
                                updateAssigments(date = assigment.date, hour = true, noreminder = false)
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
                            updateAssigments(date = false, hour = false, noreminder = true)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.fchNoReminder))
                        }
                    )
                    // END FOR Sin aviso
                }

                Spacer(modifier = Modifier.size(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = titleTask,
                    onValueChange = { titleTask = it },
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

                AnimatedVisibility(visible = assigment.date) {
                    Column {
                        Spacer(modifier = Modifier.size(16.dp))

                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = {  },
                            readOnly = true,
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

                AnimatedVisibility(visible = assigment.hour) {
                    Column {
                        Spacer(modifier = Modifier.size(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FilledTonalButton(onClick = { openTimePicker = true }) {
                                Text(
                                    text = stringResource(id = R.string.btnSelectHour)
                                )
                            }

                            Text(
                                text = selectedHour,
                                style = typography.bodyLarge,
                                fontWeight = FontWeight.W500
                            )
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

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    datePickerState: DatePickerState,
    closeDialog: () -> Unit,
    selectedDate: (String) -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = { closeDialog() },
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        formatter.format(Date(it+TimeUnit.DAYS.toMillis(1L)))
                    }?.let {
                        selectedDate(it)
                    }

                    closeDialog()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.btnAccept)
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    timePickerState: TimePickerState,
    closeDialog: () -> Unit,
    selectedHour: (String) -> Unit
) {
    Dialog(onDismissRequest = { closeDialog() }) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timePickerState)

                Button(
                    onClick = {
                        selectedHour(
                            LocalTime.of(timePickerState.hour,timePickerState.minute).format(
                                DateTimeFormatter.ofPattern("hh:mm a"))
                        )

                        closeDialog()
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