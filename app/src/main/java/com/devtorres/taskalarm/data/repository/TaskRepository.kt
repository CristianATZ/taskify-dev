package com.devtorres.taskalarm.data.repository

import com.devtorres.taskalarm.data.database.Converters
import com.devtorres.taskalarm.data.database.TaskDao
import com.devtorres.taskalarm.data.model.SubTask
import com.devtorres.taskalarm.data.model.Task

interface TaskRepository {
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun updateSubTask(taskId: Int, subTaskList: List<SubTask>)
    suspend fun completeTask(task: Task)
    suspend fun updateTaskById(taskId: Int)
    suspend fun deleteTask(task: Task)
    suspend fun getAllTasks(): List<Task>
}

class TaskRepositoryImpl(
    private val taskDao: TaskDao
): TaskRepository{
    // funcion para insertar tarea
    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    // funcion para actualizar tarea
    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    override suspend fun updateSubTask(taskId: Int, subTaskList: List<SubTask>) {
        // Convierte la lista de subtareas a una cadena JSON
        val subtasksJson = Converters().fromSubtaskList(subTaskList)

        // Llama al DAO para actualizar la tarea
        taskDao.updateSubTask(taskId, subtasksJson)
    }

    override suspend fun completeTask(task: Task) {
        taskDao.completeTask(task)
    }

    // funcion para actualizar tarea en base al id
    override suspend fun updateTaskById(taskId: Int) {
        taskDao.udpateTaskById(taskId)
    }

    // funcion para eliminar tarea
    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    // funcion para obtener todas las tareas
    override suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRepositoryImpl? = null

        fun getInstance(taskDao: TaskDao): TaskRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskRepositoryImpl(taskDao).also { INSTANCE = it }
            }
        }
    }
}