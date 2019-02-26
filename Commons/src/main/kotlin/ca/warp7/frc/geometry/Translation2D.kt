package ca.warp7.frc.geometry

import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class Translation2D(val x: Double, val y: Double) {

    val copy: Translation2D get() = Translation2D(x, y)

    val mag: Double get() = sqrt(x * x + y * y)

    val normal: Translation2D get() = scaled(by = 1 / mag)

    val direction: Rotation2D get() = Rotation2D(x, y).normalized

    val inverse: Translation2D get() = Translation2D(-x, -y)

    companion object {
        val identity = Translation2D(0.0, 0.0)
    }
}