package ca.warp7.frc

import kotlin.math.abs

fun Double.epsilonEquals(other: Double, epsilon: Double) = this - epsilon <= other && this + epsilon >= other

fun Double.epsilonEquals(other: Double) = epsilonEquals(other, 1E-12)

fun interpolate(a: Double, b: Double, x: Double) = a + (b - a) * x.coerceIn(0.0, 1.0)

val Double.f get() = "%.4f".format(this)

fun applyDeadband(value: Double, max: Double, deadband: Double) = when {
    abs(value) > deadband -> (abs(value) - deadband) / (max - deadband)
    else -> 0.0
}