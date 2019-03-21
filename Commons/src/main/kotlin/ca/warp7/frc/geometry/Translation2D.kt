package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.hypot

data class Translation2D(val x: Double, val y: Double) : State<Translation2D> {

    override val state: Translation2D get() = this

    override fun rangeTo(state: Translation2D) = object : Interpolator<Translation2D> {
        override fun get(n: Double) = interpolate(state, n)
    }

    override fun interpolate(other: Translation2D, n: Double): Translation2D = when {
        n <= 0 -> copy
        n >= 1 -> other.copy
        else -> Translation2D(n * (other.x - x) + x, n * (other.y - y) + y)
    }

    override fun unaryMinus(): Translation2D = inverse

    override val inverse: Translation2D get() = Translation2D(-x, -y)

    override fun unaryPlus(): Translation2D = copy

    override val copy: Translation2D get() = Translation2D(x, y)

    override val isIdentity: Boolean get() = x == 0.0 && y == 0.0

    override fun epsilonEquals(state: Translation2D, epsilon: Double) =
            x.epsilonEquals(state.x, epsilon) && y.epsilonEquals(state.y, epsilon)

    override fun transform(by: Translation2D): Translation2D {
        return Translation2D(x + by.x, y + by.y)
    }

    override fun toString(): String {
        return "Translation(${x.f}, ${y.f})"
    }

    val mag: Double get() = hypot(x, y)

    companion object {
        val identity = Translation2D(0.0, 0.0)
    }
}