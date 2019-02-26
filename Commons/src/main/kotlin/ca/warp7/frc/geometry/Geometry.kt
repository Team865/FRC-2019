@file:Suppress("unused")

package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin


/*
 * ROTATION FUNCTIONS
 */

fun rotation(radians: Double): Rotation2D = Rotation2D(cos(radians), sin(radians))

fun rotationInDegrees(degrees: Double): Rotation2D = rotation(Math.toRadians(degrees))

val Rotation2D.pose: Pose2D get() = Pose2D(translation = Translation2D.identity, rotation = this)

val Rotation2D.radians: Double get() = atan2(y = sin, x = cos)

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

fun Translation2D.scaled(by: Double): Translation2D = Translation2D(x * by, y * by)

fun Translation2D.translate(by: Translation2D) = Translation2D(x + by.x, y + by.y)

fun Translation2D.rotate(by: Rotation2D) = Translation2D(x * by.cos - y * by.sin, x * by.sin + y * by.cos)

fun Translation2D.epsilonEquals(other: Translation2D, epsilon: Double) =
        x.epsilonEquals(other.x, epsilon) && y.epsilonEquals(other.y, epsilon)

infix fun Translation2D.dot(other: Translation2D) = x * other.x + y * other.y

infix fun Translation2D.cross(other: Translation2D) = x * other.y - y * other.x

operator fun Translation2D.times(by: Double) = scaled(by)

operator fun Translation2D.plus(by: Translation2D) = translate(by)

operator fun Translation2D.minus(by: Translation2D) = translate(by.inverse)

operator fun Translation2D.unaryPlus() = copy

operator fun Translation2D.unaryMinus() = inverse


/*
 * POSE FUNCTIONS
 */

fun Pose2D.transform(by: Pose2D) = Pose2D(translation.translate(by.translation), rotation.rotate(by.rotation))

fun Pose2D.transform(by: Translation2D) = transform(Pose2D(by, Rotation2D.identity))

fun Pose2D.transform(by: Rotation2D) = transform(by.pose)

operator fun Pose2D.plus(by: Pose2D) = transform(by)

operator fun Pose2D.plus(by: Translation2D) = transform(by)

operator fun Pose2D.plus(by: Rotation2D) = transform(by)