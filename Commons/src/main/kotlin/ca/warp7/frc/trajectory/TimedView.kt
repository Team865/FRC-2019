package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State

@Suppress("unused")
class TimedView<T : State<T>, V : TimedState<T, V>>(
        override val points: List<V>
) : StateListView<T, V, TimedView<T, V>> {

    override val end: Double
        get() = TODO("not implemented")

    override fun get(x: Double): StateListSample<T, V, TimedView<T, V>> {
        TODO("not implemented")
    }

    override val start: Double
        get() = TODO("not implemented")

    override fun toString(): String {
        return "Trajectory size: ${points.size}\n" + points.joinToString("\n")
    }
}