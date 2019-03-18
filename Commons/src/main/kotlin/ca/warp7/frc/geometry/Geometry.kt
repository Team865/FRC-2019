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

fun Rotation2D.Companion.fromRadians(radians: Double): Rotation2D = Rotation2D(cos(radians), sin(radians))

fun Rotation2D.Companion.fromDegrees(degrees: Double) = fromRadians(Math.toRadians(degrees))

val Rotation2D.radians: Double get() = atan2(y = sin, x = cos)

val Rotation2D.degrees: Double get() = Math.toDegrees(radians)

val Rotation2D.mag: Double get() = hypot(sin, cos)

val Rotation2D.norm: Rotation2D get() = scaled(by = 1 / mag)

val Rotation2D.translation: Translation2D get() = Translation2D(cos, sin)

fun Rotation2D.scaled(by: Double): Rotation2D = Rotation2D(cos * by, sin * by)

fun Rotation2D.rotate(by: Rotation2D): Rotation2D =
        Rotation2D(cos * by.cos - sin * by.sin, cos * by.sin + sin * by.cos).norm

fun Rotation2D.distance(other: Rotation2D) = inverse.rotate(other).radians

fun Rotation2D.interpolate(other: Rotation2D, x: Double) = when {
    x <= 0 -> copy
    x >= 1 -> other.copy
    else -> rotate(Rotation2D.fromRadians(radians = distance(other) * x))
}

fun Rotation2D.interpolator(other: Rotation2D) = object : Interpolator<Rotation2D> {
    override fun get(n: Double) = interpolate(other, n)
}

infix fun Rotation2D.parallelTo(other: Rotation2D) = (translation cross other.translation).epsilonEquals(0.0)

operator fun Rotation2D.times(by: Double) = scaled(by)

operator fun Rotation2D.plus(by: Rotation2D) = rotate(by)

operator fun Rotation2D.unaryPlus() = copy

operator fun Rotation2D.rangeTo(other: Rotation2D) = interpolator(other)

/*
 * TRANSLATION FUNCTIONS
 */

val Translation2D.norm: Translation2D get() = scaled(by = 1 / mag)

val Translation2D.direction: Rotation2D get() = Rotation2D(x, y).norm

fun Translation2D.scaled(by: Double): Translation2D = Translation2D(x * by, y * by)

fun Translation2D.translate(by: Translation2D) = Translation2D(x + by.x, y + by.y)

fun Translation2D.rotate(by: Rotation2D) = Translation2D(x * by.cos - y * by.sin, x * by.sin + y * by.cos)

fun Translation2D.epsilonEquals(other: Translation2D, epsilon: Double) =
        x.epsilonEquals(other.x, epsilon) && y.epsilonEquals(other.y, epsilon)

fun Translation2D.extrapolate(other: Translation2D, n: Double) =
        Translation2D(n * (other.x - x) + x, n * (other.y - y) + y)

fun Translation2D.interpolate(other: Translation2D, n: Double) = when {
    n <= 0 -> copy
    n >= 1 -> other.copy
    else -> extrapolate(other, n)
}

fun Translation2D.interpolator(other: Translation2D) = object : Interpolator<Translation2D> {
    override fun get(n: Double) = interpolate(other, n)
}

infix fun Translation2D.dot(other: Translation2D) = x * other.x + y * other.y

infix fun Translation2D.cross(other: Translation2D) = x * other.y - y * other.x

operator fun Translation2D.times(by: Double) = scaled(by)

operator fun Translation2D.plus(by: Translation2D) = translate(by)

operator fun Translation2D.minus(by: Translation2D) = translate(by.inverse)

operator fun Translation2D.unaryPlus() = copy

operator fun Translation2D.unaryMinus() = inverse

operator fun Translation2D.rangeTo(other: Translation2D) = interpolator(other)

/*
 * POSE FUNCTIONS
 */

fun Pose2D.transform(by: Pose2D) =
        Pose2D(translation.translate(by.translation.rotate(by.rotation)), rotation.rotate(by.rotation))

fun Pose2D.transform(by: Translation2D) = transform(Pose2D(by, Rotation2D.identity))

fun Pose2D.transform(by: Rotation2D) = transform(Pose2D(Translation2D.identity, by))

operator fun Pose2D.plus(by: Pose2D) = transform(by)

operator fun Pose2D.plus(by: Translation2D) = transform(by)

operator fun Pose2D.plus(by: Rotation2D) = transform(by)