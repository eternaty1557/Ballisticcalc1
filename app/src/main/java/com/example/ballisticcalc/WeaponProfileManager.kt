package com.example.ballisticcalc

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weapon_profiles")

class WeaponProfileManager(private val context: Context) {

    companion object {
        private val PROFILE_LIST_KEY = stringSetPreferencesKey("profile_list")
    }

    suspend fun saveProfile(profile: WeaponProfile) {
        val profileKey = stringPreferencesKey("profile_${profile.id}")
        context.dataStore.edit { prefs ->
            prefs[profileKey] = profile.toString()
            val currentList = prefs[PROFILE_LIST_KEY] ?: emptySet()
            prefs[PROFILE_LIST_KEY] = currentList + profile.id
        }
    }

    fun getProfileFlow(id: String): Flow<WeaponProfile?> = context.dataStore.data
        .map { prefs ->
            val profileKey = stringPreferencesKey("profile_$id")
            prefs[profileKey]?.let { profileStr ->
                parseProfile(profileStr)
            }
        }

    fun getProfileIdsFlow(): Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[PROFILE_LIST_KEY] ?: emptySet() }

    suspend fun deleteProfile(id: String) {
        context.dataStore.edit { prefs ->
            val profileKey = stringPreferencesKey("profile_$id")
            prefs.remove(profileKey)
            val currentList = prefs[PROFILE_LIST_KEY] ?: emptySet()
            prefs[PROFILE_LIST_KEY] = currentList - id
        }
    }

    private fun parseProfile(profileStr: String): WeaponProfile? {
        return try {
            val regex = Regex(
                """WeaponProfile\(id='([^']*)', weaponName='([^']*)', projectileName='([^']*)', sightType='([^']*)'(?:, zeroDistance=(\d+))?(?:, notes='([^']*)')?\)"""
            )
            val matchResult = regex.find(profileStr)
            if (matchResult != null) {
                val groups = matchResult.groupValues
                WeaponProfile(
                    id = groups[1],
                    weaponName = groups[2],
                    projectileName = groups[3],
                    sightType = groups[4],
                    zeroDistance = groups.getOrNull(5)?.toIntOrNull() ?: 100,
                    notes = groups.getOrNull(6) ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}