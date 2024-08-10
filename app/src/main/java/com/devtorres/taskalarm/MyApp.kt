package com.devtorres.taskalarm

import android.annotation.SuppressLint
import android.app.Application
import com.devtorres.taskalarm.data.database.AppDataBase
import com.devtorres.taskalarm.data.repository.TaskRepositoryImpl

class MyApp : Application() {
    private val database by lazy { AppDataBase.getInstance(this) }
    val taskRepository by lazy { TaskRepositoryImpl.getInstance(database.taskDao()) }

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        // Inicialización global aquí
    }
}