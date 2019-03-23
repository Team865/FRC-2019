package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView

interface TimedState<T : State<T>, V : TimedState<T, V>> : StateView<T, V> {
    val t: Double
}