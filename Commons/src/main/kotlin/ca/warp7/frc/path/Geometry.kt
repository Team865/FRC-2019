@file:Suppress("unused")

package ca.warp7.frc.path

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun radiansToRotation(radians: Double): Rotation2D = Rotation2D(cos(radians), sin(radians))

fun degreesToRotation(degrees: Double): Rotation2D = radiansToRotation(Math.toRadians(degrees))

val Translation2D.pose: Pose2D get() = Pose2D(translation = this, rotation = Rotation2D.identity)

val Rotation2D.pose: Pose2D get() = Pose2D(translation = Translation2D.identity, rotation = this)

val Translation2D.dist: Double get() = sqrt(x * x + y * y)

val Rotation2D.angle: Double get() = atan2(y = sin, x = cos)

val Rotation2D.mag: Double get() = sqrt(cos * cos + sin * sin)

val Rotation2D.normal: Rotation2D get() = scaled(by = 1 / mag)

val Translation2D.normal: Translation2D get() = scaled(by = 1 / dist)

fun Rotation2D.scaled(by: Double): Rotation2D = Rotation2D(cos * by, sin * by)

fun Translation2D.scaled(by: Double): Translation2D = Translation2D(x * by, y * by)