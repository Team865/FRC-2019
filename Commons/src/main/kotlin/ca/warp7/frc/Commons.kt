package ca.warp7.frc

import kotlin.math.abs

fun Double.epsilonEquals(other: Double, epsilon: Double) = this - epsilon <= other && this + epsilon >= other

fun Double.epsilonEquals(other: Double) = epsilonEquals(other, 1E-12)

fun linearInterpolate(a: Double, b: Double, x: Double) = a + (b - a) * x.coerceIn(0.0, 1.0)

val Double.f get() = "%.3f".format(this)
val Double.f1 get() = "%.1f".format(this)

fun applyDeadband(value: Double, max: Double, deadband: Double): Double {
    val v = value.coerceIn(-max, max)
    return if (abs(v) > deadband) {
        if (v > 0.0) (v - deadband) / (max - deadband)
        else (v + deadband) / (max - deadband)
    } else 0.0
}

const val kFeetToMeters: Double = 0.3048

const val kInchesToMeters: Double = 0.0254

const val kMetersToFeet: Double = 1 / kFeetToMeters

const val kMetersToInches: Double = 1 / kInchesToMeters

val Number.feet: Double get() = this.toDouble() * kFeetToMeters

val Number.inches: Double get() = this.toDouble() * kInchesToMeters

val Double.squared: Double get() = this * this