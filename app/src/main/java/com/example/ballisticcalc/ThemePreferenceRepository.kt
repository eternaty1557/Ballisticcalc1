package com.example.ballisticcalc.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferenceRepository(private val dataStore: DataStore<Preferences>) {

    private val KEY_AUTO_THEME = booleanPreferencesKey("auto_theme")
    private val KEY_NIGHT_MODE = booleanPreferencesKey("night_mode")

    val autoTheme: Flow<Boolean> = dataStore.data
        .map { it[KEY_AUTO_THEME] ?: true }

    val nightMode: Flow<Boolean> = dataStore.data
        .map { it[KEY_NIGHT_MODE] ?: false }

    suspend fun setAutoTheme(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_THEME] = enabled }
    }

    suspend fun setNightMode(enabled: Boolean) {
        dataStore.edit { it[KEY_NIGHT_MODE] = enabled }
    }
}