package com.example.ballisticcalc

import androidx.compose.runtime.Immutable

@Immutable
data class WeaponProfile(
    val id: String = "",
    val weaponName: String,
    val projectileName: String,
    val sightType: String,
    val zeroDistance: Int = 100,
    val notes: String = ""
)