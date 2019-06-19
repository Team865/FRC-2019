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

val Number.radians: Rotation2D get() = Rotation2D.fromRadians(this.toDouble())

val Number.degrees: Rotation2D get() = Rotation2D.fromDegrees(this.toDouble())

val Rotation2D.radians: Double get() = atan2(y = sin, x = cos)

val Rotation2D.degrees: Double get() = Math.toDegrees(radians)

val Rotation2D.mag: Double get() = hypot(sin, cos)

val Rotation2D.norm: Rotation2D get() = scaled(by = 1 / mag)

val Rotation2D.translation: Translation2D get() = Translation2D(cos, sin)

val Rotation2D.normal: Rotation2D get() = Rotation2D(-sin, cos)

fun Rotation2D.rotate(by: Rotation2D): Rotation2D = transform(by)

val Rotation2D.tan: Double
    get() {
        return if (Math.abs(cos) < 1E-12) {
            if (sin >= 0.0) {
                Double.POSITIVE_INFINITY
            } else {
                Double.NEGATIVE_INFINITY
            }
        } else sin / cos
    }

infix fun Rotation2D.parallelTo(other: Rotation2D) = (translation cross other.translation).epsilonEquals(0.0)

/*
 * TRANSLATION FUNCTIONS
 */

val Translation2D.direction: Rotation2D get() = Rotation2D(x, y).norm

val Translation2D.transposed: Translation2D get() = Translation2D(y, x)

val Translation2D.norm: Translation2D get() = scaled(by = 1 / mag)

val Translation2D.flipX: Translation2D get() = Translation2D(-x, y)

val Translation2D.flipY: Translation2D get() = Translation2D(x, -y)

fun Translation2D.rotate(by: Rotation2D) = Translation2D(x * by.cos - y * by.sin, x * by.sin + y * by.cos)

infix fun Translation2D.dot(other: Translation2D) = x * other.x + y * other.y

infix fun Translation2D.cross(other: Translation2D) = x * other.y - y * other.x

fun fitParabola(p1: Translation2D, p2: Translation2D, p3: Translation2D): Double {
    val a = p3.x * (p2.y - p1.y) + p2.x * (p1.y - p3.y) + p1.x * (p3.y - p2.y)
    val b = p3.x * p3.x * (p1.y - p2.y) + p2.x * p2.x * (p3.y - p1.y) + p1.x * p1.x * (p2.y - p3.y)
    return -b / (2 * a)
}

/*
 * POSE FUNCTIONS
 */

fun Pose2D.isColinear(other: Pose2D): Boolean {
    if (!rotation.parallelTo(other.rotation)) return false
    val twist = (other - this).log
    return twist.dy.epsilonEquals(0.0) && twist.dTheta.epsilonEquals(0.0)
}

fun Pose2D.intersection(other: Pose2D): Translation2D {
    val otherRotation = other.rotation
    if (rotation.parallelTo(otherRotation)) {
        // Lines are parallel.
        return Translation2D(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY)
    }
    return if (Math.abs(rotation.cos) < Math.abs(otherRotation.cos)) {
        intersectionInternal(this, other)
    } else {
        intersectionInternal(other, this)
    }
}

private fun intersectionInternal(a: Pose2D, b: Pose2D): Translation2D {
    val ar = a.rotation
    val br = b.rotation
    val at = a.translation
    val bt = b.translation

    val tanB = br.tan
    val t = ((at.x - bt.x) * tanB + bt.y - at.y) / (ar.sin - ar.cos * tanB)
    return if (t.isNaN()) {
        Translation2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    } else at + ar.translation * t
}