package com.example.ballisticcalc

import com.example.ballisticcalc.data.BallisticDao
import com.example.ballisticcalc.data.WeaponProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeaponProfileManager(private val dao: BallisticDao) {

    fun getProfileIdsFlow(): Flow<Set<String>> {
        return dao.getAllProfileIds().map { list ->
            list.map { it.id }.toSet()
        }
    }

    fun getProfileFlow(id: String): Flow<WeaponProfile?> {
        return dao.getProfilesByPrefix(id).map { list ->
            list.firstOrNull { it.id == id }?.toDomain()
        }
    }

    suspend fun saveProfile(profile: WeaponProfile) {
        dao.insertProfile(profile.toEntity())
    }

    suspend fun deleteProfile(id: String) {
        dao.deleteProfile(id)
    }

    suspend fun deleteProfilesByPrefix(prefix: String) {
        dao.deleteProfilesByPrefix(prefix)
    }

    private fun WeaponProfileEntity.toDomain() = WeaponProfile(
        id = id,
        weaponName = weaponName,
        projectileName = projectileName,
        sightType = sightType,
        notes = notes
    )

    private fun WeaponProfile.toEntity() = WeaponProfileEntity(
        id = id,
        weaponName = weaponName,
        projectileName = projectileName,
        sightType = sightType,
        notes = notes
    )
}