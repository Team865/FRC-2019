package ca.warp7.frc2019.math

import kotlin.math.PI

data class Pose2d(var x: Double, var y: Double, var heading: Double) {
    operator fun plus(other: Pose2d): Pose2d {
        return Pose2d(x + other.x, y + other.y, (heading + other.heading) % (2 * PI))
    }
}