package com.example.ballisticcalc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weapon_profiles")
data class WeaponProfile(
    @PrimaryKey val id: String,
    val weaponName: String = "",
    val projectileName: String = "",
    val sightType: String = "",
    val notes: String = ""
)