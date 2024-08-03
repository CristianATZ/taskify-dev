package com.devtorres.taskalarm.ui.dialog

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R
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
    val context = LocalContext.current

    var titleTask by remember {
        mutableStateOf("")
    }

    var openDatePicker by remember {
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

    var selectedDate by remember {
        mutableStateOf("dd/MM/yyyy")
    }

    var hourSelected by remember {
        mutableStateOf("--:--")
    }

    var calendar by remember {
        mutableStateOf(Calendar.getInstance())
    }

    if(openDatePicker) {
        DateTimeDialog(
            datePickerState = datePickerState,
            timePickerState = timePickerState,
            closeDialog = {
                openDatePicker = false
            },
            selectedDate = {
                selectedDate = it
            },
            selectedHour = {
                hourSelected = it
            },
            updateCalendar = {
                calendar = it
            }
        )
    }
    

    Dialog(onDismissRequest = { closeDialog() }) {
        OutlinedCard {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.95f)
            ) {
                Text(
                    text = stringResource(id = R.string.lblTask),
                    style = typography.bodyLarge,
                    fontWeight = FontWeight.W900
                )

                Spacer(modifier = Modifier.size(16.dp))

                OutlinedTextField(
                    value = titleTask,
                    onValueChange = { titleTask = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.lblTaskName),
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.5f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { openDatePicker = true },
                        shape = OutlinedTextFieldDefaults.shape,
                        modifier = Modifier.height(OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {  },
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.lblCreatedFor),
                        style = typography.bodyLarge
                    )

                    Text(
                        text = hourSelected,
                        style = typography.bodyLarge,
                        fontWeight = FontWeight.W500
                    )
                }

                Spacer(modifier = Modifier.size(32.dp))

                Button(
                    onClick = {
                        addTask(titleTask)
                        addReminder(titleTask, calendar)
                        Toast.makeText(context, calendar.timeInMillis.toString(), Toast.LENGTH_SHORT).show()
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

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeDialog(
    datePickerState: DatePickerState,
    timePickerState: TimePickerState,
    closeDialog: () -> Unit,
    selectedDate: (String) -> Unit,
    selectedHour: (String) -> Unit,
    updateCalendar: (Calendar) -> Unit
) {

    var onNext by remember {
        mutableStateOf(false)
    }

    var date by remember {
        mutableStateOf("")
    }

    if(!onNext){
        DatePickerDialog(
            onDismissRequest = { closeDialog() },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val formatter = SimpleDateFormat("dd/MM/yyyy")
                            formatter.format(Date(it+TimeUnit.DAYS.toMillis(1L)))
                        }?.let {
                            date = it
                        }
                        onNext = true
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.btnNext)
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
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

                            val milis = datePickerState.selectedDateMillis ?: 0
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = milis + (1000 * 60 * 60 * 24)
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            Log.d("FECHA", "Year: ${calendar.get(Calendar.YEAR)}, Month: ${calendar.get(Calendar.MONTH)}, Day: ${calendar.get(Calendar.DAY_OF_MONTH)}, Hour: ${calendar.get(Calendar.HOUR_OF_DAY)}, Minute: ${calendar.get(Calendar.MINUTE)}")
                            selectedDate(date)
                            selectedHour(
                                LocalTime.of(timePickerState.hour,timePickerState.minute).format(
                                    DateTimeFormatter.ofPattern("hh:mm a"))
                            )
                            updateCalendar(calendar)

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
}