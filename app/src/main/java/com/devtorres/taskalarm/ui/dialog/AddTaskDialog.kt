package com.devtorres.taskalarm.ui.dialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.Task
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskDialog(
    closeDialog: () -> Unit,
    addTask: (String) -> Unit
) {
    val context = LocalContext.current

    var titleTask by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { closeDialog() }) {
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
                    onClick = {
                        addTask(titleTask)
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