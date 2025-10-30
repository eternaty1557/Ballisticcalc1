package com.example.ballisticcalc

object WeaponRepository {
    val allWeapons = listOf(
        // --- Снайперские винтовки ---
        Weapon(
            name = "СВД",
            caliber = "7.62x54R",
            sightType = SightType.OPTICAL_MIL,
            weaponType = WeaponType.SNIPER_RIFLES,
            projectiles = listOf(Projectile("7Н1", 830.0, 0.415, cd = 0.33, diameter = 0.00762, mass = 0.011))
        ),
        Weapon(
            name = "СВ-98",
            caliber = "7.62x54R",
            sightType = SightType.OPTICAL_MOA,
            weaponType = WeaponType.SNIPER_RIFLES,
            projectiles = listOf(Projectile("7Н14", 850.0, 0.450, cd = 0.31, diameter = 0.00762, mass = 0.0115))
        ),
        Weapon(
            name = "АСВК",
            caliber = "12.7x108",
            sightType = SightType.OPTICAL_MIL,
            weaponType = WeaponType.SNIPER_RIFLES,
            projectiles = listOf(Projectile("Б-32", 820.0, 0.750, cd = 0.38, diameter = 0.0127, mass = 0.047))
        ),

        // --- Пулемёты ---
        Weapon(
            name = "ПКМ",
            caliber = "7.62x54R",
            sightType = SightType.IRON_SIGHTS,
            weaponType = WeaponType.MACHINE_GUNS,
            projectiles = listOf(Projectile("ЛПС", 825.0, 0.380, cd = 0.30, diameter = 0.00762, mass = 0.0097))
        ),
        Weapon(
            name = "ПКП \"Печенег\"",
            caliber = "7.62x54R",
            sightType = SightType.IRON_SIGHTS,
            weaponType = WeaponType.MACHINE_GUNS,
            projectiles = listOf(Projectile("7Н13", 825.0, 0.400, cd = 0.29, diameter = 0.00762, mass = 0.0098))
        ),
        Weapon(
            name = "НСВ \"Утёс\"",
            caliber = "12.7x108",
            sightType = SightType.IRON_SIGHTS,
            weaponType = WeaponType.MACHINE_GUNS,
            projectiles = listOf(Projectile("Б-32", 820.0, 0.750, cd = 0.38, diameter = 0.0127, mass = 0.047))
        ),

        // --- Штурмовые винтовки ---
        Weapon(
            name = "АК-74М",
            caliber = "5.45x39",
            sightType = SightType.IRON_SIGHTS,
            weaponType = WeaponType.ASSAULT_RIFLES,
            projectiles = listOf(Projectile("7Н6", 900.0, 0.300, cd = 0.28, diameter = 0.00545, mass = 0.0034))
        ),
        Weapon(
            name = "АК-12",
            caliber = "5.45x39",
            sightType = SightType.OPTICAL_MIL,
            weaponType = WeaponType.ASSAULT_RIFLES,
            projectiles = listOf(Projectile("7Н39", 920.0, 0.320, cd = 0.26, diameter = 0.00545, mass = 0.0036))
        ),

        // --- Миномёты ---
        Weapon(
            name = "2Б14 \"Поднос\" (82-мм)",
            caliber = "82 мм",
            sightType = SightType.MORTAR,
            weaponType = WeaponType.MORTARS,
            projectiles = listOf(Projectile("ОФ-843Б", 200.0, 0.400, cd = 0.45, diameter = 0.082, mass = 3.1))
        ),
        Weapon(
            name = "2Б11 \"Сани\" (120-мм)",
            caliber = "120 мм",
            sightType = SightType.MORTAR,
            weaponType = WeaponType.MORTARS,
            projectiles = listOf(Projectile("3ВО17", 270.0, 0.550, cd = 0.40, diameter = 0.120, mass = 16.0))
        ),

        // --- Артиллерия ---
        Weapon(
            name = "2С19 \"Мста-С\" (152-мм гаубица)",
            caliber = "152 мм",
            sightType = SightType.ARTILLERY,
            weaponType = WeaponType.ARTILLERY,
            projectiles = listOf(Projectile("ОФ-540", 828.0, 0.650, cd = 0.35, diameter = 0.152, mass = 46.0))
        ),
        Weapon(
            name = "Д-30 (122-мм гаубица)",
            caliber = "122 мм",
            sightType = SightType.ARTILLERY,
            weaponType = WeaponType.ARTILLERY,
            projectiles = listOf(Projectile("ОФ-462", 690.0, 0.500, cd = 0.40, diameter = 0.122, mass = 21.8))
        )
    )
}