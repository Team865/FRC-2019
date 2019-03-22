package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State

interface StateListView<T : State<T>, V : StateListView<T, V>> {
    val start: Double
    val end: Double
    operator fun get(x: Double): StateListSample<T, V>
}