package com.devtorres.taskalarm.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.repository.TaskRepository
import com.devtorres.taskalarm.util.NotificationHelper
import com.devtorres.taskalarm.util.ShareHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class TaskState(
    val taskList: List<Task> = emptyList()
)

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(TaskState())
    val uiState: StateFlow<TaskState> = _uiState.asStateFlow()

    // jalar todos las notas de room
    init {
        viewModelScope.launch {
            getAllTask()
        }
    }

    private fun getAllTask() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                taskList = taskRepository.getAllTasks()
            )
        }
    }

    fun addtask(task: Task, context: Context, content: String, expiredCalendar: Calendar, preCalendar: Calendar, message: String, preMessage: String) {
        viewModelScope.launch {
            val id = taskRepository.insertTask(task)

            NotificationHelper.scheduleInstantTaskNotification(
                context = context,
                title = task.title,
                content = content,
                requestCode = id.toString()
            )

            if(task.reminder){
                NotificationHelper.scheduleExactNotification(
                    context = context,
                    title = task.title,
                    content = message,
                    calendar = expiredCalendar,
                    requestCode = "$id".toInt()
                )

                NotificationHelper.scheduleExactNotification(
                    context = context,
                    title = task.title,
                    content = preMessage,
                    calendar = preCalendar,
                    requestCode = "$id$id".toInt()
                )
            }

            getAllTask()
        }
    }

    fun updateTask(task: Task, context: Context, content: String, expiredCalendar: Calendar, preCalendar: Calendar, message: String, preMessage: String) {
        viewModelScope.launch {
            taskRepository.updateTask(task)

            NotificationHelper.scheduleInstantTaskNotification(
                context = context,
                title = task.title,
                content = content,
                requestCode = task.id.toString()
            )

            if(!task.reminder) {
                NotificationHelper.cancelNotification(context, task.title, message, "${task.id}".toInt())
                NotificationHelper.cancelNotification(context, task.title, preMessage, "${task.id}${task.id}".toInt())
            }

            if(task.reminder) {
                NotificationHelper.scheduleExactNotification(
                    context = context,
                    title = task.title,
                    content = message,
                    calendar = expiredCalendar,
                    requestCode = "${task.id}".toInt()
                )

                NotificationHelper.scheduleExactNotification(
                    context = context,
                    title = task.title,
                    content = preMessage,
                    calendar = preCalendar,
                    requestCode = "${task.id}${task.id}".toInt()
                )
            }

            getAllTask()
        }
    }

    fun completeTask(task: Task, context: Context, message: String, preMessage: String) {
        viewModelScope.launch {
            taskRepository.completeTask(task)

            if(task.reminder) {
                NotificationHelper.cancelNotification(context, task.title, message, "${task.id}".toInt())
                NotificationHelper.cancelNotification(context, task.title, preMessage, "${task.id}${task.id}".toInt())
            }

            getAllTask()
        }
    }

    fun deleteTask(task: Task, context: Context, message: String, preMessage: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)

            if(task.reminder) {
                NotificationHelper.cancelNotification(context, task.title, message, "${task.id}".toInt())
                NotificationHelper.cancelNotification(context, task.title, preMessage, "${task.id}${task.id}".toInt())
            }

            getAllTask()
        }
    }

    fun shareTask(
        information: String,
        shareLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) {
        viewModelScope.launch {
            ShareHelper.sendTask(information, shareLauncher)
        }
    }

    fun refreshTask() {
        viewModelScope.launch {
            getAllTask()
        }
    }
}

@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)){
            return TaskViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}