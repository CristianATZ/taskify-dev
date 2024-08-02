package com.devtorres.taskalarm.ui.task

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.repository.TaskRepository
import com.devtorres.taskalarm.util.ShareHelper
import com.devtorres.taskalarm.work.LocalNotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun addtask(task: Task){
        viewModelScope.launch {
            taskRepository.insertTask(task)

            getAllTask()
        }
    }

    fun updateTask(task: Task){
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

    private fun getAllTask(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                taskList = taskRepository.getAllTasks()
            )
        }
    }

    fun scheduleTaskNotification(context: Context, title: String, content: String){
        viewModelScope.launch {
            val data  = Data.Builder()
                .putString("title", title)
                .putString("content", content)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<LocalNotificationWorker>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    fun shareTask(
        context: Context,
        information: String,
        shareLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) {
        viewModelScope.launch {
            ShareHelper.sendTask(context, information, shareLauncher)
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