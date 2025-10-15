package com.example.ballisticcalc

enum class SightType {
    OPTICAL_MIL,
    OPTICAL_MOA,
    IRON_SIGHTS,
    MORTAR
}

data class Weapon(
    val name: String,
    val caliber: String,
    val sightType: SightType,
    val weaponType: WeaponType,
    val projectiles: List<Projectile>
)

data class Projectile(
    val name: String,
    val muzzleVelocity: Double,
    val ballisticCoefficient: Double,
    val cd: Double = 0.3,
    val diameter: Double = 0.01,
    val mass: Double = 0.01
)

val weapons = listOf(
    Weapon(
        name = "СВД",
        caliber = "7.62x54R",
        sightType = SightType.OPTICAL_MIL,
        weaponType = WeaponType.SNIPER_RIFLES,
        projectiles = listOf(
            Projectile("Снайперская пуля 7Н1", 830.0, 0.415, cd = 0.33, diameter = 0.00762, mass = 0.011)
        )
    ),
    Weapon(
        name = "Миномёт 82-мм 2Б14",
        caliber = "82 мм",
        sightType = SightType.MORTAR,
        weaponType = WeaponType.MORTARS,
        projectiles = listOf(
            Projectile("Осколочная мина", 200.0, 0.400, cd = 0.45, diameter = 0.082, mass = 3.1)
        )
    ),
    Weapon(
        name = "AK-74",
        caliber = "5.45x39",
        sightType = SightType.IRON_SIGHTS,
        weaponType = WeaponType.ASSAULT_RIFLES,
        projectiles = listOf(
            Projectile("Пуля 7Н6", 900.0, 0.300, cd = 0.28, diameter = 0.00545, mass = 0.0034)
        )
    ),
    Weapon(
        name = "Barrett M82",
        caliber = ".50 BMG",
        sightType = SightType.OPTICAL_MOA,
        weaponType = WeaponType.SNIPER_RIFLES,
        projectiles = listOf(
            Projectile("M33 Ball", 887.0, 0.650, cd = 0.35, diameter = 0.0127, mass = 0.046)
        )
    )
)