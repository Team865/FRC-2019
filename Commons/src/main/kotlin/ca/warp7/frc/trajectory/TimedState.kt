package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Interpolator
import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView
import ca.warp7.frc.interpolate

data class TimedState<T : State<T>>(override val state: T, val t: Double) : StateView<T, TimedState<T>> {
    override fun rangeTo(state: T): Interpolator<T> {
        TODO("not implemented")
    }

    override fun interpolate(other: TimedState<T>, x: Double): StateView<T, TimedState<T>> =
            TimedState(state.interpolate(other.state, x).state, interpolate(t, other.t, x))
}