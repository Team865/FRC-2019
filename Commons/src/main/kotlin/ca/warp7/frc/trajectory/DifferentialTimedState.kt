package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Interpolator
import ca.warp7.frc.geometry.State
import ca.warp7.frc.interpolate

data class DifferentialTimedState<T : State<T>>(
        override val state: T,
        override val t: Double,
        val lp: Double,
        val lv: Double,
        val la: Double,
        val rp: Double,
        val rv: Double,
        val ra: Double
) : TimedState<T, DifferentialTimedState<T>> {

    override fun rangeTo(state: DifferentialTimedState<T>): Interpolator<DifferentialTimedState<T>> =
            object : Interpolator<DifferentialTimedState<T>> {
                override fun get(x: Double): DifferentialTimedState<T> {
                    return interpolate(state, x)
                }
            }

    override fun interpolate(other: DifferentialTimedState<T>, x: Double): DifferentialTimedState<T> =
            DifferentialTimedState(
                    state.interpolate(other.state, x).state,
                    interpolate(t, other.t, x),
                    interpolate(lp, other.lp, x),
                    interpolate(lv, other.lv, x),
                    interpolate(la, other.la, x),
                    interpolate(rp, other.rp, x),
                    interpolate(rv, other.rv, x),
                    interpolate(ra, other.ra, x)
            )
}