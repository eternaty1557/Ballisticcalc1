package com.example.ballisticcalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeaponProfileManager(private val db: AppDatabase) {

    // Получаем все профили как Flow
    fun getAllProfiles(): Flow<List<WeaponProfile>> {
        return db.weaponDao().getAllProfiles()
    }

    // Получаем список ID профилей (например, для фильтрации по роли)
    fun getProfileIdsFlow(): Flow<Set<String>> {
        return db.weaponDao().getAllProfiles()
            .map { profiles ->
                profiles.map { it.id }.toSet()
            }
    }

    // Получаем профиль по ID
    fun getProfileFlow(id: String): Flow<WeaponProfile?> {
        return db.weaponDao().getAllProfiles()
            .map { profiles ->
                profiles.firstOrNull { it.id == id }
            }
    }

    // Сохраняем профиль
    suspend fun saveProfile(profile: WeaponProfile) {
        db.weaponDao().insertProfile(profile)
    }

    // Удаляем профиль по ID
    suspend fun deleteProfile(profileId: String) {
        db.weaponDao().deleteProfile(profileId)
    }

    // Удаляем все профили
    suspend fun deleteAllProfiles() {
        db.weaponDao().deleteAllProfiles()
    }
}