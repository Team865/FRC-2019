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

fun Rotation2D.rotate(by: Rotation2D): Rotation2D =
        Rotation2D(cos * by.cos - sin * by.sin, cos * by.sin + sin * by.cos).norm

infix fun Rotation2D.parallelTo(other: Rotation2D) = (translation cross other.translation).epsilonEquals(0.0)

/*
 * TRANSLATION FUNCTIONS
 */

val Translation2D.direction: Rotation2D get() = Rotation2D(x, y).norm

fun Translation2D.rotate(by: Rotation2D) = Translation2D(x * by.cos - y * by.sin, x * by.sin + y * by.cos)

infix fun Translation2D.dot(other: Translation2D) = x * other.x + y * other.y

infix fun Translation2D.cross(other: Translation2D) = x * other.y - y * other.x