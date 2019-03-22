package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Interpolator
import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView
import ca.warp7.frc.interpolate

data class TimedState<T : State<T>>(override val state: T, val t: Double) : StateView<T, TimedState<T>> {

    override fun rangeTo(state: TimedState<T>): Interpolator<TimedState<T>> =
            object : Interpolator<TimedState<T>> {
                override fun get(x: Double): TimedState<T> {
                    return interpolate(state, x)
                }
            }

    override fun interpolate(other: TimedState<T>, x: Double): TimedState<T> =
            TimedState(state.interpolate(other.state, x).state, interpolate(t, other.t, x))
}