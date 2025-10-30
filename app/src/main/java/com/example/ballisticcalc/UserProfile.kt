package com.example.ballisticcalc

enum class WeaponType {
    SNIPER_RIFLES,
    ASSAULT_RIFLES,
    MACHINE_GUNS,  // ← добавлено!
    MORTARS,
    ARTILLERY,
    RSZO
}

data class UserProfile(
    val callsign: String,
    val division: String,
    val weaponType: WeaponType
)