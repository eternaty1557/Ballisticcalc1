package com.example.ballisticcalc

data class WeatherResponse(
    val current: Current
)

data class Current(
    val temperature_2m: Double,
    val pressure_msl: Double,       // в гПа (hPa)
    val windspeed_10m: Double,
    val winddirection_10m: Double
)