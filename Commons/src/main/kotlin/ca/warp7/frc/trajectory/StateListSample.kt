package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView

data class StateListSample<T : State<T>, V : StateView<T, V>, S : StateListView<T, V, S>>(
        val stateView: StateView<T, V>,
        val listView: S,
        val start: Double,
        val end: Double
)