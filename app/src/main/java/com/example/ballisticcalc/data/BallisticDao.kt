// com.example.ballisticcalc.data.BallisticDao.kt

package com.example.ballisticcalc.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BallisticDao {

    @Query("SELECT * FROM profiles WHERE id LIKE :prefix || '%'")
    fun getProfilesByPrefix(prefix: String): Flow<List<WeaponProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: WeaponProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfile(id: String)

    @Query("DELETE FROM profiles WHERE id LIKE :prefix || '%'")
    suspend fun deleteProfilesByPrefix(prefix: String)

    @Query("SELECT * FROM profiles")
    fun getAllProfileIds(): Flow<List<WeaponProfileEntity>>
}