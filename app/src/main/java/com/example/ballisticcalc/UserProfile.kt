package com.example.ballisticcalc

enum class WeaponType {
    SNIPER_RIFLES,
    MORTARS,
    ASSAULT_RIFLES,
    MACHINE_GUNS
}

data class UserProfile(
    val callsign: String,
    val division: String,
    val weaponType: WeaponType
)