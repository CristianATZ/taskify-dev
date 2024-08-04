package com.devtorres.taskalarm.ui.dialog

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
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
import androidx.core.text.HtmlCompat
import com.devtorres.taskalarm.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
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
    addTask: (String) -> Unit,
    addReminder: (String, Calendar) -> Unit
) {
    val scope = rememberCoroutineScope()

    // FOR propiedades de la tarea
    var titleTask by remember {
        mutableStateOf("")
    }
    // END FOR propiedades de la tarea

    // FOR chips
    var defaultDate by remember {
        mutableStateOf(false)
    }

    var defaultTime by remember {
        mutableStateOf(false)
    }

    var noReminder by remember {
        mutableStateOf(false)
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

    var hourSelected by remember {
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
                hourSelected = it
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
                Card(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = colorScheme.surfaceContainerHighest
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = defaultDate,
                            onClick = {
                                defaultDate = !defaultDate
                                noReminder = false
                            },
                            label = {
                                Text(text = stringResource(id = R.string.fchDate))
                            }
                        )

                        FilterChip(
                            selected = defaultTime,
                            onClick = {
                                defaultTime = !defaultTime
                                noReminder = false
                            },
                            label = {
                                Text(text = stringResource(id = R.string.fchTime))
                            }
                        )

                        FilterChip(
                            selected = noReminder,
                            onClick = {
                                noReminder = !noReminder
                                defaultDate = false
                                defaultTime = false
                            },
                            label = {
                                Text(text = stringResource(id = R.string.fchNoDate))
                            }
                        )
                    }
                }

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

                if(defaultDate){
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

                if(defaultTime){
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
                            text = hourSelected,
                            style = typography.bodyLarge,
                            fontWeight = FontWeight.W500
                        )
                    }
                }

                Spacer(modifier = Modifier.size(32.dp))

                Button(
                    onClick = {
                        val milis = datePickerState.selectedDateMillis ?: 0
                        calendar = Calendar.getInstance().apply {
                            timeInMillis = milis + (1000 * 60 * 60 * 24)
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        Log.d("FECHA", "Year: ${calendar.get(Calendar.YEAR)}, Month: ${calendar.get(Calendar.MONTH)}, Day: ${calendar.get(Calendar.DAY_OF_MONTH)}, Hour: ${calendar.get(Calendar.HOUR_OF_DAY)}, Minute: ${calendar.get(Calendar.MINUTE)}")

                        addTask(titleTask)
                        if(!noReminder){
                            addReminder(titleTask, calendar)
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