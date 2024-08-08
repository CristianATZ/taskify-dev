package com.devtorres.taskalarm.ui.task

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
import com.devtorres.taskalarm.util.AlarmScheduler
import com.devtorres.taskalarm.util.ShareHelper
import com.devtorres.taskalarm.util.WorkScheduler
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

    fun addtask(task: Task, context: Context, content: String, calendar: Calendar) {
        viewModelScope.launch {
            val id = taskRepository.insertTask(task)

            Log.d("REQUESTCODE", "view ${id.toInt()}")

            scheduleTaskNotification(
                context = context,
                title = task.title,
                content = content,
                requestCode = id.toString()
            )

            if(task.reminder){
                scheduleExactNotification(
                    context = context,
                    title = task.title,
                    content = "Acaba de expirar",
                    calendar = calendar,
                    requestCode = id.toInt()
                )
            }

            getAllTask()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)

            getAllTask()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            Log.d("TaskViewModel", "Deleting task: $task")
            taskRepository.deleteTask(task)

            getAllTask()
            Log.d("TaskViewModel", "Tasks after delete: ${_uiState.value.taskList}")
        }
    }

    private fun getAllTask() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                taskList = taskRepository.getAllTasks()
            )
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

    fun scheduleTaskNotification(
        context: Context,
        title: String,
        content: String,
        requestCode: String
    ) {
        viewModelScope.launch {
            WorkScheduler.scheduleInstantNotification(context, title, content, requestCode)
        }
    }

    fun scheduleExactNotification(
        context: Context,
        title: String,
        content: String,
        calendar: Calendar,
        requestCode: Int
    ) {
        viewModelScope.launch {
            AlarmScheduler.scheduleAlarmOnExactDate(
                context = context,
                title = title,
                content = content,
                calendar = calendar,
                requestCode = requestCode
            )
        }
    }

    fun cancelNotification(
        context: Context,
        title: String,
        content: String,
        requestCode: Int
    ) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(context, title, content, requestCode)
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