package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f

class Rotation2D(val cos: Double, val sin: Double) : State<Rotation2D> {

    override fun unaryMinus(): Rotation2D = inverse

    override fun unaryPlus(): Rotation2D = copy

    override val isIdentity: Boolean get() = epsilonEquals(identity)

    override fun epsilonEquals(state: Rotation2D, epsilon: Double): Boolean =
            cos.epsilonEquals(state.cos, epsilon) && sin.epsilonEquals(state.sin, epsilon)

    override fun epsilonEquals(state: Rotation2D): Boolean = epsilonEquals(state, 1E-12)

    override fun transform(by: Rotation2D): Rotation2D =
            Rotation2D(cos * by.cos - sin * by.sin, cos * by.sin + sin * by.cos).norm

    override fun plus(by: Rotation2D): Rotation2D = transform(by)

    override fun minus(by: Rotation2D): Rotation2D = transform(by.inverse)

    override fun scaled(by: Double): Rotation2D = Rotation2D(cos * by, sin * by)

    override fun times(by: Double): Rotation2D = scaled(by)

    override fun div(by: Double): Rotation2D = scaled(1.0 / by)

    override fun distanceTo(state: Rotation2D): Double = (state - this).radians

    override val state: Rotation2D get() = this

    override fun rangeTo(state: Rotation2D): Interpolator<Rotation2D> =
            object : Interpolator<Rotation2D> {
                override fun get(x: Double) = interpolate(state, x)
            }

    override fun interpolate(other: Rotation2D, x: Double): Rotation2D = when {
        x <= 0 -> copy
        x >= 1 -> other.copy
        else -> transform(Rotation2D.fromRadians(radians = distanceTo(other) * x))
    }

    override val copy: Rotation2D get() = Rotation2D(cos, sin)

    override val inverse: Rotation2D get() = Rotation2D(cos, -sin)

    override fun toString(): String {
        return "⟳${degrees.f}°"
    }

    companion object {
        val identity = Rotation2D(1.0, 0.0)
    }
}