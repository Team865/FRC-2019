@file:Suppress("unused")

package ca.warp7.frc.path

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun radiansToRotation(radians: Double): Rotation2D = Rotation2D(cos(radians), sin(radians))

fun degreesToRotation(degrees: Double): Rotation2D = radiansToRotation(Math.toRadians(degrees))

val Translation2D.pose: Pose2D get() = Pose2D(this, Rotation2D.identity)

val Rotation2D.pose: Pose2D get() = Pose2D(Translation2D.identity, this)

val Translation2D.distance: Double get() = sqrt(x * x + y * y)

val Rotation2D.angle: Double get() = atan2(sin, cos)

val Rotation2D.mag: Double get() = sqrt(cos * cos + sin * sin)

val Rotation2D.normalized: Rotation2D
    get() {
        val mag = mag
        return Rotation2D(cos / mag, sin / mag)
    }