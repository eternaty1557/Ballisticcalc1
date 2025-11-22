package com.example.ballisticcalc.utils

import java.util.Calendar
import java.util.TimeZone

object SunriseCalculator {

    fun isNight(latitude: Double, longitude: Double): Boolean {
        val now = Calendar.getInstance()
        val sunrise = getSunriseTime(latitude, longitude, now)
        val sunset = getSunsetTime(latitude, longitude, now)
        val currentTime = now.timeInMillis

        return currentTime !in sunrise..sunset
    }

    private fun getSunriseTime(latitude: Double, longitude: Double, date: Calendar): Long {
        // Простой алгоритм — для примера
        // Реальный алгоритм можно взять из NOAA или других источников
        val dayOfYear = date.get(Calendar.DAY_OF_YEAR)
        val hourAngle = calculateHourAngle(latitude, dayOfYear)
        val sunriseHour = 12 - hourAngle / 15
        val sunriseMinute = (sunriseHour - Math.floor(sunriseHour)) * 60
        val sunriseSecond = (sunriseMinute - Math.floor(sunriseMinute)) * 60

        val sunriseCalendar = Calendar.getInstance().apply {
            set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, sunriseHour.toInt())
            set(Calendar.MINUTE, sunriseMinute.toInt())
            set(Calendar.SECOND, sunriseSecond.toInt())
        }

        return sunriseCalendar.timeInMillis
    }

    private fun getSunsetTime(latitude: Double, longitude: Double, date: Calendar): Long {
        val dayOfYear = date.get(Calendar.DAY_OF_YEAR)
        val hourAngle = calculateHourAngle(latitude, dayOfYear)
        val sunsetHour = 12 + hourAngle / 15
        val sunsetMinute = (sunsetHour - Math.floor(sunsetHour)) * 60
        val sunsetSecond = (sunsetMinute - Math.floor(sunsetMinute)) * 60

        val sunsetCalendar = Calendar.getInstance().apply {
            set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, sunsetHour.toInt())
            set(Calendar.MINUTE, sunsetMinute.toInt())
            set(Calendar.SECOND, sunsetSecond.toInt())
        }

        return sunsetCalendar.timeInMillis
    }

    private fun calculateHourAngle(latitude: Double, dayOfYear: Int): Double {
        // Упрощённый расчёт — для примера
        val declination = 23.45 * Math.sin(Math.toRadians(360.0 * (284 + dayOfYear) / 365))
        val cosHourAngle = -Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(declination))
        return Math.toDegrees(Math.acos(cosHourAngle))
    }
}