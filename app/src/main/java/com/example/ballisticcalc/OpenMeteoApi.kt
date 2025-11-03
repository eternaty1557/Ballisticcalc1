package com.example.ballisticcalc

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,pressure_msl,windspeed_10m,winddirection_10m",
        @Query("forecast_days") days: Int = 1
    ): WeatherResponse
}