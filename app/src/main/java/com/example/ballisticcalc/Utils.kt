package com.example.ballisticcalc


import java.lang.Math.toRadians
import kotlin.math.*
import kotlin.math.pow

fun calculateTrajectoryWithDrag(
    projectile: Projectile,
    angleDegrees: Double,
    rho: Double = 1.204, // Плотность воздуха при 20°C и 760 мм рт.ст.
    timeStep: Double = 0.01,
    maxTime: Double = 60.0
): List<Pair<Double, Double>> {
    val g = 9.81
    val cd = projectile.cd
    val area = PI * (projectile.diameter / 2.0).pow(2.0)
    val mass = projectile.mass
    val v0 = projectile.muzzleVelocity

    var x = 0.0
    var y = 0.0
    var vx = v0 * cos(toRadians(angleDegrees))
    var vy = v0 * sin(toRadians(angleDegrees))
    var t = 0.0

    val trajectory = mutableListOf<Pair<Double, Double>>()
    trajectory.add(x to y)

    while (y >= 0 && t < maxTime) {
        val v = sqrt(vx * vx + vy * vy)
        if (v < 0.1) break

        val fd = 0.5 * cd * rho * area * v * v
        val ax = -fd * vx / (mass * v)
        val ay = -g - (fd * vy / (mass * v))

        vx += ax * timeStep
        vy += ay * timeStep

        x += vx * timeStep
        y += vy * timeStep

        trajectory.add(x to y)
        t += timeStep
    }

    return trajectory
}

fun findHeightAtDistance(trajectory: List<Pair<Double, Double>>, distance: Double): Double {
    for (i in 1 until trajectory.size) {
        val (x0, y0) = trajectory[i - 1]
        val (x1, y1) = trajectory[i]
        if (x1 >= distance) {
            val t = (distance - x0) / (x1 - x0)
            return y0 + t * (y1 - y0)
        }
    }
    return 0.0
}