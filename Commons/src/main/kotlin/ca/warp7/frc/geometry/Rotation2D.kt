package ca.warp7.frc.geometry

import ca.warp7.frc.f

@Suppress("unused")
data class Rotation2D(val cos: Double, val sin: Double) {

    val copy: Rotation2D get() = Rotation2D(cos, sin)

    val inverse: Rotation2D get() = Rotation2D(cos, -sin)

    val normal: Rotation2D get() = Rotation2D(-sin, cos)

    override fun toString(): String {
        return "Rotation(${cos.f}, ${sin.f}, ${degrees.f}°)"
    }

    companion object {
        val identity = Rotation2D(1.0, 0.0)
    }
}