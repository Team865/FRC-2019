package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView

interface StateListView<T : State<T>, V : StateView<T, V>, S : StateListView<T, V, S>> {
    val points: List<V>
    val start: Double
    val end: Double
    operator fun get(x: Double): StateListSample<T, V, S>
}