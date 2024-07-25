package com.devtorres.taskalarm.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.ui.theme.TaskAlarmTheme

@Composable
fun TaskScreen(modifier: Modifier = Modifier) {
    val task = listOf(
        Task(
            id = 1,
            title = "Comprar víveres",
            isCompleted = false
        ),
        Task(
            id = 2,
            title = "Reunión con equipo",
            isCompleted = true
        ),
        Task(
            id = 3,
            title = "Llamar al doctor",
            isCompleted = false
        ),
        Task(
            id = 4,
            title = "Ejercicio en el gimnasio",
            isCompleted = true
        ),
        Task(
            id = 5,
            title = "Leer libro",
            isCompleted = false
        )
    )

    val taskCompleted = task.filter {
        it.isCompleted
    }

    val taskUncompleted = task.filter {
        !it.isCompleted
    }

    Scaffold(
        topBar = {
            TopBarApp()
        },
        bottomBar = {
            BottomBarApp()
        },
        floatingActionButton = {
            FloatingActionApp()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // tareas no completadas
            LazyColumn {

                items( taskUncompleted.size ){ index ->
                    TaskObject(
                        task = taskUncompleted[index],
                    ){
                        task[index].isCompleted = it
                    }
                }

                items( taskCompleted.size ){ index ->
                    TaskObject(
                        task = taskCompleted[index],
                        isCompleted = taskCompleted[index].isCompleted
                    )
                }
            }

            // tareas completadas
        }
    }
}

@Composable
fun TaskObject(
    task: Task,
    isCompleted: Boolean = false,
    updateStatus: (Boolean) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(0.9f)
            .height(50.dp)
            .background(colorScheme.surface) // Color de fondo
            .clickable { /* Acción al hacer clic en el Card */ },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ) // Elevación del Card
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Espacio entre el texto y el Checkbox
        ) {
            Text(
                text = task.title,
                style = typography.titleLarge,
                color = colorScheme.onSurface // Color del texto
            )

            Spacer(modifier = Modifier.weight(1f))

            if(!isCompleted){
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { updateStatus(it) },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary) // Color del Checkbox
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarApp() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.lblTareas)
            )
        }
    )
}

@Composable
fun FloatingActionApp() {
    SmallFloatingActionButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
    }
}

@Composable
fun BottomBarApp() {
    BottomAppBar(
        actions = {
            NavigationDrawerItem(
                label = {
                    Text(text = stringResource(id = R.string.lblTask))
                },
                selected = false,
                onClick = {

                }
            )
            NavigationDrawerItem(
                label = {
                    Text(text = stringResource(id = R.string.lblTheme))
                },
                selected = false,
                onClick = {

                }
            )
        }

    )
}


@Preview
@Composable
private fun TaskDarkPreview() {
    TaskAlarmTheme(true) {
        TaskScreen()
    }
}

@Preview
@Composable
private fun TaskLightPreview() {
    TaskAlarmTheme(false) {
        TaskScreen()
    }
}
