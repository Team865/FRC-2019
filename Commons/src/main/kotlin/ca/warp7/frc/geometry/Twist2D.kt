package ca.warp7.frc.geometry

import ca.warp7.frc.f

data class Twist2D(val dx: Double, val dy: Double, val dTheta: Double) {
    override fun toString(): String {
        return "Twist(${dx.f}, ${dy.f}, ${dTheta.f})"
    }
}