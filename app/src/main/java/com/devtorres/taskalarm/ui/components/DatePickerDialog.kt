package com.devtorres.taskalarm.ui.components

import android.annotation.SuppressLint
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.devtorres.taskalarm.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    datePickerState: DatePickerState,
    closeDialog: () -> Unit,
    selectedDate: (String) -> Unit,
) {
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = { closeDialog() },
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        formatter.format(Date(it + TimeUnit.DAYS.toMillis(1L)))
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