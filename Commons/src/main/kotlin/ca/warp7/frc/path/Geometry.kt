@file:Suppress("unused")

package ca.warp7.frc.path

import kotlin.math.cos
import kotlin.math.sin

fun radiansToRotation(radians: Double): Rotation2D = Rotation2D(cos(radians), sin(radians))

fun degreesToRotation(degrees: Double): Rotation2D = radiansToRotation(Math.toRadians(degrees))

fun Translation2D.toPose() = Pose2D(this, Rotation2D.identity)