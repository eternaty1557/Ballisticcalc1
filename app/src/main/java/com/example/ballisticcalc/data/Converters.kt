package com.example.ballisticcalc.data

import androidx.room.TypeConverter
import com.example.ballisticcalc.Projectile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromProjectileList(value: List<Projectile>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toProjectileList(value: String?): List<Projectile>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Projectile>>() {}.type
        return gson.fromJson(value, listType)
    }
}