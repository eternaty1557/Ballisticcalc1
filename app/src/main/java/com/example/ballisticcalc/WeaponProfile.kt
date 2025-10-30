package com.example.ballisticcalc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "weapon_profiles")
data class WeaponProfile(
    @PrimaryKey val id: String,
    val weaponName: String,
    val projectileName: String,
    val sightType: String,

    val angle: String,
    val targetDistance: String,
    val temperature: String,
    val windSpeed: String,
    val windDirection: String,
    val pressure: String,

    val resultText: String,

    @ColumnInfo(name = "trajectory_json")
    val trajectoryJson: String = "",

    val notes: String = ""
) {
    // ✅ Используем @Transient — работает с Room 2.4+

    val trajectoryPoints: List<Pair<Double, Double>>
        get() = try {
            trajectoryJson
                .removePrefix("[")
                .removeSuffix("]")
                .split("),(")
                .map { pair ->
                    val clean = pair.replace("(", "").replace(")", "")
                    val (x, y) = clean.split(",")
                    x.toDouble() to y.toDouble()
                }
        } catch (e: Exception) {
            emptyList()
        }

    // Конструктор для удобного создания из UI
    @Ignore
    constructor(
        id: String,
        weaponName: String,
        projectileName: String,
        sightType: String,
        angle: String,
        targetDistance: String,
        temperature: String,
        windSpeed: String,
        windDirection: String,
        pressure: String,
        resultText: String,
        trajectoryPoints: List<Pair<Double, Double>>,
        notes: String
    ) : this(
        id = id,
        weaponName = weaponName,
        projectileName = projectileName,
        sightType = sightType,
        angle = angle,
        targetDistance = targetDistance,
        temperature = temperature,
        windSpeed = windSpeed,
        windDirection = windDirection,
        pressure = pressure,
        resultText = resultText,
        trajectoryJson = trajectoryPoints.joinToString(prefix = "[", postfix = "]") { "(${it.first},${it.second})" },
        notes = notes
    )
}