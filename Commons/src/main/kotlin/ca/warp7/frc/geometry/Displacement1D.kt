package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals

@Suppress("unused")
class Displacement1D(val x: Double) : State<Displacement1D> {

    override fun unaryMinus(): Displacement1D = inverse

    override val inverse: Displacement1D
        get() = Displacement1D(-x)

    override fun unaryPlus(): Displacement1D = copy

    override val copy: Displacement1D
        get() = Displacement1D(x)

    override val isIdentity: Boolean
        get() = epsilonEquals(identity)

    override fun epsilonEquals(state: Displacement1D, epsilon: Double): Boolean =
            x.epsilonEquals(state.x, epsilon)

    override fun epsilonEquals(state: Displacement1D): Boolean = epsilonEquals(state, 1E-12)

    override fun transform(by: Displacement1D): Displacement1D = Displacement1D(x + by.x)

    override fun plus(by: Displacement1D): Displacement1D = transform(by)

    override fun minus(by: Displacement1D): Displacement1D = transform(by.inverse)

    override fun scaled(by: Double): Displacement1D = Displacement1D(x * by)

    override fun times(by: Double): Displacement1D = scaled(by)

    override fun div(by: Double): Displacement1D = scaled(1.0 / by)

    override fun distanceTo(state: Displacement1D): Double = state.x - x

    override val state: Displacement1D get() = this

    override fun rangeTo(state: Displacement1D): Interpolator<Displacement1D> =
            object : Interpolator<Displacement1D> {
                override fun get(x: Double): Displacement1D = interpolate(state, x)
            }

    override fun interpolate(other: Displacement1D, x: Double): Displacement1D =
            Displacement1D(this.x + (other.x - this.x) * x)

    companion object {
        val identity = Displacement1D(0.0)
    }
}