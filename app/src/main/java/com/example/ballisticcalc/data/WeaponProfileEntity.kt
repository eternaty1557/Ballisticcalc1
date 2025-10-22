// com.example.ballisticcalc.data.WeaponProfileEntity.kt

package com.example.ballisticcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class WeaponProfileEntity(
    @PrimaryKey val id: String,
    val weaponName: String,
    val projectileName: String,
    val sightType: String,
    val notes: String
)