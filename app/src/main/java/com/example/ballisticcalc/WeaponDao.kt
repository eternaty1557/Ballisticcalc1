package com.example.ballisticcalc

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeaponDao {
    @Query("SELECT * FROM weapon_profiles ORDER BY weaponName ASC")
    fun getAllProfiles(): Flow<List<WeaponProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: WeaponProfile)

    @Query("SELECT * FROM weapon_profiles WHERE id = :profileId")
    suspend fun getProfileById(profileId: String): WeaponProfile?

    @Query("DELETE FROM weapon_profiles WHERE id = :profileId")
    suspend fun deleteProfile(profileId: String)

    @Query("DELETE FROM weapon_profiles")
    suspend fun deleteAllProfiles()
}