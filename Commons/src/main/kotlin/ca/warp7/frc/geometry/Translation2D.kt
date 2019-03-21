package ca.warp7.frc.geometry

import ca.warp7.frc.f
import kotlin.math.hypot

data class Translation2D(val x: Double, val y: Double) : State<Translation2D> {

    override val state: Translation2D get() = this

    override fun rangeTo(state: Translation2D) = object : Interpolator<Translation2D> {
        override fun get(n: Double) = interpolate(state, n)
    }

    override fun unaryMinus(): Translation2D = inverse

    override val inverse: Translation2D get() = Translation2D(-x, -y)

    override fun unaryPlus(): Translation2D = copy

    override val copy: Translation2D get() = Translation2D(x, y)

    override val isIdentity: Boolean get() = x == 0.0 && y == 0.0

    val mag: Double get() = hypot(x, y)

    override fun toString(): String {
        return "Translation(${x.f}, ${y.f})"
    }

    companion object {
        val identity = Translation2D(0.0, 0.0)
    }
}