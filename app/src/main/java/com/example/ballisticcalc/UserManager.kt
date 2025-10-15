package com.example.ballisticcalc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserManager(private val context: Context) {

    companion object {
        private val USER_LOGGED_IN = booleanPreferencesKey("user_logged_in")
        private val USER_CALLSIGN = stringPreferencesKey("user_callsign")
        private val USER_DIVISION = stringPreferencesKey("user_division")
        private val USER_CURRENT_ROLE = stringPreferencesKey("user_current_role")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val userFlow: Flow<UserProfile?> = context.userDataStore.data.map { prefs ->
        if (prefs[USER_LOGGED_IN] == true) {
            UserProfile(
                callsign = prefs[USER_CALLSIGN] ?: "Unknown",
                division = prefs[USER_DIVISION] ?: "00",
                weaponType = enumValueOf(prefs[USER_CURRENT_ROLE] ?: "SNIPER_RIFLES")
            )
        } else {
            null
        }
    }

    suspend fun login(callsign: String, division: String) {
        context.userDataStore.edit { prefs ->
            prefs[USER_LOGGED_IN] = true
            prefs[USER_CALLSIGN] = callsign
            prefs[USER_DIVISION] = division
            prefs[USER_CURRENT_ROLE] = "SNIPER_RIFLES"
        }
    }

    suspend fun setCurrentRole(weaponType: WeaponType) {
        context.userDataStore.edit { prefs ->
            prefs[USER_CURRENT_ROLE] = weaponType.name
        }
    }

    suspend fun logout() {
        context.userDataStore.edit { prefs ->
            prefs[USER_LOGGED_IN] = false
            prefs.remove(USER_CALLSIGN)
            prefs.remove(USER_DIVISION)
            prefs.remove(USER_CURRENT_ROLE)
        }
    }
}