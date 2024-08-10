package com.devtorres.taskalarm.ui.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devtorres.taskalarm.MyApp
import com.devtorres.taskalarm.data.datastore.UserPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val application: MyApp
) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)

    val theme = userPreferences.theme
    val notification = userPreferences.notification

    fun saveTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            userPreferences.saveTheme(isDarkMode, application)
        }
    }

    fun saveNotification(isEnabled: Boolean) {
        viewModelScope.launch {
            userPreferences.saveNotifications(isEnabled, application)
        }
    }
}

class SettingsViewModelFactory(private val application: MyApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}