package com.example.ballisticcalc.utils

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.first
import com.example.ballisticcalc.data.ThemePreferenceRepository
import kotlinx.coroutines.tasks.await

class LocationAndThemeManager(
    private val context: Context,
    private val themeRepo: ThemePreferenceRepository
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var lastLocation: Location? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getLastKnownLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun shouldUseNightMode(): Boolean {
        val auto = themeRepo.autoTheme.first()
        if (!auto) return themeRepo.nightMode.first()

        val loc = getLastKnownLocation() ?: return false // fallback to day

        return SunriseCalculator.isNight(loc.latitude, loc.longitude)
    }

    companion object {
        @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
        suspend fun shouldUseNightMode(locationAndThemeManager: LocationAndThemeManager): Boolean {
            val loc =
                locationAndThemeManager.getLastKnownLocation() ?: return false // fallback to day
            return SunriseCalculator.isNight(loc.latitude, loc.longitude)
        }
    }
}