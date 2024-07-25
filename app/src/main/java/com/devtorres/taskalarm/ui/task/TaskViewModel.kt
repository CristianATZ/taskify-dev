package com.devtorres.taskalarm.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.repository.TaskRepository
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
            _uiState.value = _uiState.value.copy(
                taskList = taskRepository.getAllTasks()
            )
        }
    }

    fun addtask(task: Task){
        viewModelScope.launch {
            taskRepository.insertTask(task)

            _uiState.value = _uiState.value.copy(
                taskList = _uiState.value.taskList + task
            )
        }
    }

    fun updateTask(task: Task){
        viewModelScope.launch {
            taskRepository.updateTask(task)

            _uiState.value = _uiState.value.copy(
                taskList = _uiState.value.taskList.map {
                    if (it.id == task.id) task else it
                }
            )
        }
    }

    fun deleteTask(task: Task){
        viewModelScope.launch {
            taskRepository.deleteTask(task)

            _uiState.value = _uiState.value.copy(
                taskList = _uiState.value.taskList.filter {
                    it.id != task.id
                }
            )
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