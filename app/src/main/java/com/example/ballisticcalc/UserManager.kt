package com.example.ballisticcalc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
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

    /**
     * Всегда возвращает UserProfile (никогда null).
     * Если пользователь не залогинен — возвращается гостевой профиль.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val userFlow: Flow<UserProfile> = context.userDataStore.data.map { prefs ->
        val isLoggedIn = prefs[USER_LOGGED_IN] ?: false
        val callsign = prefs[USER_CALLSIGN] ?: "Guest"
        val division = prefs[USER_DIVISION] ?: "00"

        val roleString = prefs[USER_CURRENT_ROLE] ?: "SNIPER_RIFLES"
        val weaponType = runCatching {
            enumValueOf<WeaponType>(roleString)
        }.getOrElse {
            WeaponType.SNIPER_RIFLES // fallback
        }

        if (isLoggedIn) {
            UserProfile(callsign = callsign, division = division, weaponType = weaponType)
        } else {
            // Гостевой режим: можно использовать те же данные или дефолтные
            UserProfile(callsign = "Guest", division = "00", weaponType = WeaponType.SNIPER_RIFLES)
        }
    }

    suspend fun login(callsign: String, division: String) {
        context.userDataStore.edit { prefs ->
            prefs[USER_LOGGED_IN] = true
            prefs[USER_CALLSIGN] = callsign.trim().ifEmpty { "Guest" }
            prefs[USER_DIVISION] = division.trim().ifEmpty { "00" }
            prefs[USER_CURRENT_ROLE] = WeaponType.SNIPER_RIFLES.name
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
            // Опционально: можно очистить данные или оставить для быстрого входа
        }
    }
}