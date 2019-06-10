package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.hypot

class Translation2D(val x: Double, val y: Double) : State<Translation2D> {

    override val state: Translation2D get() = this

    override fun rangeTo(state: Translation2D): Interpolator<Translation2D> =
            object : Interpolator<Translation2D> {
        override fun get(x: Double) = interpolate(state, x)
    }

    override fun interpolate(other: Translation2D, x: Double): Translation2D = when {
        x <= 0 -> copy
        x >= 1 -> other.copy
        else -> Translation2D(x * (other.x - this.x) + this.x, x * (other.y - y) + y)
    }

    override fun unaryMinus(): Translation2D = inverse

    override val inverse: Translation2D get() = Translation2D(-x, -y)

    override fun unaryPlus(): Translation2D = copy

    override val copy: Translation2D get() = Translation2D(x, y)

    override val isIdentity: Boolean get() = epsilonEquals(identity)

    override fun epsilonEquals(state: Translation2D, epsilon: Double): Boolean =
            x.epsilonEquals(state.x, epsilon) && y.epsilonEquals(state.y, epsilon)

    override fun epsilonEquals(state: Translation2D): Boolean = epsilonEquals(state, 1E-12)

    override fun transform(by: Translation2D): Translation2D {
        return Translation2D(x + by.x, y + by.y)
    }

    override fun plus(by: Translation2D): Translation2D = transform(by)

    override fun minus(by: Translation2D): Translation2D = transform(by.inverse)

    override fun toString(): String {
        return "↘(${x.f}, ${y.f})"
    }

    override fun scaled(by: Double): Translation2D = Translation2D(x * by, y * by)

    override fun times(by: Double): Translation2D = scaled(by)

    override fun div(by: Double): Translation2D = scaled(1.0 / by)

    override fun distanceTo(state: Translation2D): Double = hypot(state.x - x, state.y - y)

    val mag: Double get() = hypot(x, y)

    companion object {
        val identity = Translation2D(0.0, 0.0)
    }
}