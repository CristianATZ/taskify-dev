package com.devtorres.taskalarm.ui.task

import androidx.lifecycle.ViewModel
import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(TaskState())
    val uiState: StateFlow<TaskState> =_uiState.asStateFlow()

    // jalar todos las notas de room
    init {

    }


}

data class TaskState(
    val taskList: List<Task> = emptyList()
)
