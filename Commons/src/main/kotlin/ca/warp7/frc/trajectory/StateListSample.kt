package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView

data class StateListSample<T : State<T>, V : StateListView<T, V>>(
        val stateView: StateView<T>,
        val listView: V,
        val start: Double,
        val end: Double
)