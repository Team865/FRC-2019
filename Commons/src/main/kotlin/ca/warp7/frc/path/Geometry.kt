@file:Suppress("unused")

package ca.warp7.frc.path

import kotlin.math.*


/*
 * ROTATION FUNCTIONS
 */

fun radiansToRotation(radians: Double): Rotation2D = Rotation2D(cos(radians), sin(radians))

fun degreesToRotation(degrees: Double): Rotation2D = radiansToRotation(Math.toRadians(degrees))

val Rotation2D.pose: Pose2D get() = Pose2D(translation = Translation2D.identity, rotation = this)

val Rotation2D.angle: Double get() = atan2(y = sin, x = cos)

val Rotation2D.mag: Double get() = hypot(sin, cos)

val Rotation2D.normalized: Rotation2D get() = scaled(by = 1 / mag)

fun Rotation2D.scaled(by: Double): Rotation2D = Rotation2D(cos * by, sin * by)

fun Rotation2D.rotate(by: Rotation2D): Rotation2D =
        Rotation2D(cos * by.cos - sin * by.sin, cos * by.sin + sin * by.cos).normalized

val Rotation2D.inverse get() = Rotation2D(cos, -sin)

val Rotation2D.normal: Rotation2D get() = Rotation2D(-sin, cos)

operator fun Rotation2D.times(by: Double) = scaled(by)

operator fun Rotation2D.plus(by: Rotation2D) = rotate(by)


/*
 * TRANSLATION FUNCTIONS
 */

val Translation2D.pose: Pose2D get() = Pose2D(translation = this, rotation = Rotation2D.identity)

val Translation2D.dist: Double get() = sqrt(x * x + y * y)

val Translation2D.normal: Translation2D get() = scaled(by = 1 / dist)

fun Translation2D.scaled(by: Double): Translation2D = Translation2D(x * by, y * by)

fun Translation2D.translate(by: Translation2D) = Translation2D(x + by.x, y + by.y)

operator fun Translation2D.times(by: Double) = scaled(by)

operator fun Translation2D.plus(by: Translation2D) = translate(by)


/*
 * POSE FUNCTIONS
 */

fun Pose2D.transform(by: Pose2D) = Pose2D(translation.translate(by.translation), rotation.rotate(by.rotation))

fun Pose2D.transform(by: Translation2D) = transform(by.pose)

fun Pose2D.transform(by: Rotation2D) = transform(by.pose)

operator fun Pose2D.plus(by: Pose2D) = transform(by)

operator fun Pose2D.plus(by: Translation2D) = transform(by)

operator fun Pose2D.plus(by: Rotation2D) = transform(by)