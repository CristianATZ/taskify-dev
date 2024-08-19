package com.devtorres.taskalarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DoneOutline
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.ui.theme.doneScheme
import com.devtorres.taskalarm.util.TaskUtils.emptyTask
import java.time.format.TextStyle
import java.util.Locale

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
                if(task.expired) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(colorScheme.error),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = null,
                            tint = colorScheme.onError,
                            modifier = Modifier
                                .size(35.dp)
                                .graphicsLayer(alpha = 0.75f),
                        )
                    }
                }
                else if(task.isCompleted){
                    // completada
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
                }
                else {
                    // sin completar
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
                // sin recordatorio
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(colorScheme.secondary),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsOff,
                        contentDescription = null,
                        tint = colorScheme.onSecondary,
                        modifier = Modifier
                            .size(35.dp)
                            .graphicsLayer(alpha = 0.75f),
                    )
                }
            }
        }
    }
}