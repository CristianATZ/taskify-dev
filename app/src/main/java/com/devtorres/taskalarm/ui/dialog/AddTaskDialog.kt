package com.devtorres.taskalarm.ui.dialog

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.devtorres.taskalarm.data.model.Task
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskDialog(
    closeDialog: () -> Unit,
    addTask: (Task, String) -> Unit,
    addReminder: (String, Calendar, Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    // FOR propiedades de la tarea
    var titleTask by remember {
        mutableStateOf("")
    }
    // END FOR propiedades de la tarea

    // FOR chips
    var filterDate by remember {
        mutableStateOf(false)
    }

    var filterTime by remember {
        mutableStateOf(false)
    }

    var filterNo by remember {
        mutableStateOf(true)
    }

    fun selectFilter(selected: String) {
        filterDate = selected == "date" || selected == "datetime"
        filterTime = selected == "time" || selected == "datetime"
        filterNo = selected == "no"
    }
    // END FOR chips

    val toolTipState = rememberTooltipState(
        isPersistent = true
    )

    // FOR dialog pickers
    var openDatePicker by remember {
        mutableStateOf(false)
    }

    var openTimePicker by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val timePickerState = rememberTimePickerState(
        initialHour = 7,
        initialMinute = 0,
        is24Hour = false
    )
    // END FOR dialog pickers

    // FOR fecha y hora
    var selectedDate by remember {
        mutableStateOf("")
    }

    var selectedHour by remember {
        mutableStateOf("--:--")
    }
    // END FOR fecha y hora

    // FOR calendario alarManager
    var calendar by remember {
        mutableStateOf(Calendar.getInstance())
    }
    // EN FOR calendario alarManager


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
                                    Text(text = stringResource(id = R.string.lblDefaultDateTime))
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
                        selected = filterDate,
                        onClick = {
                            if(filterTime) selectFilter("datetime")
                            else selectFilter("date")
                        },
                        label = {
                            Text(text = stringResource(id = R.string.fchDate))
                        }
                    )
                    // END FOR fecha

                    // FOR Hora
                    FilterChip(
                        selected = filterTime,
                        onClick = {
                            if(filterDate) selectFilter("datetime")
                            else selectFilter("time")
                        },
                        label = {
                            Text(text = stringResource(id = R.string.fchTime))
                        }
                    )
                    // END FOR Hora

                    // FOR Sin aviso
                    FilterChip(
                        selected = filterNo,
                        onClick = {
                            selectedDate = ""
                            selectedHour = "--:--"

                            selectFilter("no")
                        },
                        label = {
                            Text(text = stringResource(id = R.string.fchNoReminder))
                        }
                    )
                    // END FOR Sin aviso
                }

                HorizontalDivider()

                Spacer(modifier = Modifier.size(32.dp))

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

                if(filterDate){
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
                }

                if(filterTime){
                    Spacer(modifier = Modifier.size(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilledTonalButton(onClick = { openTimePicker = true }) {
                            Text(
                                text = stringResource(id = R.string.btnCreatedFor)
                            )
                        }

                        Text(
                            text = selectedHour,
                            style = typography.bodyLarge,
                            fontWeight = FontWeight.W500
                        )
                    }
                }

                Spacer(modifier = Modifier.size(32.dp))

                Button(
                    onClick = {
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        val milis = datePickerState.selectedDateMillis ?: 0
                        calendar = Calendar.getInstance().apply {
                            timeInMillis = milis + (1000 * 60 * 60 * 24 * 1)
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        Log.d("FECHA", "Year: ${calendar.get(Calendar.YEAR)}, Month: ${calendar.get(Calendar.MONTH)}, Day: ${calendar.get(Calendar.DAY_OF_MONTH)}, Hour: ${calendar.get(Calendar.HOUR_OF_DAY)}, Minute: ${calendar.get(Calendar.MINUTE)}")

                        val reminder = filterDate || filterTime

                        // Crea un LocalDateTime con la fecha deseada y luego ajusta la hora y minuto
                        val localDateTime = when {
                            !reminder -> LocalDateTime.now().withHour(hour).withMinute(minute).withSecond(0).withNano(0)
                            !filterDate -> LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(milis + (1000 * 60 * 60 * 24 * 2)),
                                ZoneId.systemDefault()
                            ).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
                            else -> LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(milis + (1000 * 60 * 60 * 24 * 1)),
                                ZoneId.systemDefault()
                            ).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
                        }


                        Log.d("FECHA", "$reminder y $localDateTime")
                        val currentTask = Task(
                            title = titleTask,
                            isCompleted = false,
                            reminder = reminder,
                            finishDate = localDateTime
                        )

                        val requestCode = currentTask.toUniqueInt()

                        Log.d("REQUESTCODE", requestCode.toString())

                        addTask(currentTask, requestCode.toString())
                        if(!filterNo){
                            addReminder(titleTask, calendar, requestCode)
                        }
                        titleTask = ""
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
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
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