package com.devtorres.taskalarm.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class UserPreferences(context: Context){

    private val Context.dataStore by preferencesDataStore("user_prefs")

    private val THEME_KEY = booleanPreferencesKey("dark_theme")

    val theme = context.dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: false }

    suspend fun saveTheme(isDarkMode: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkMode
        }
    }
}