package ca.warp7.frc.geometry

import ca.warp7.frc.f
import kotlin.math.hypot

data class Translation2D(val x: Double, val y: Double) {

    val copy: Translation2D get() = Translation2D(x, y)

    val mag: Double get() = hypot(x, y)

    val inverse: Translation2D get() = Translation2D(-x, -y)

    override fun toString(): String {
        return "Translation(${x.f}, ${y.f})"
    }

    companion object {
        val identity = Translation2D(0.0, 0.0)
    }
}