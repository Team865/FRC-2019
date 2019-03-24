package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView

@Suppress("unused")
data class IndexedTrajectory<T : State<T>, V : StateView<T, V>>(

        override val points: List<V>

) : StateListView<T, V, IndexedTrajectory<T, V>> {

    override fun get(x: Double): StateListSample<T, V, IndexedTrajectory<T, V>> {
        if (x < start) return StateListSample(points.first(), this, start, start + 1)
        if (x > end) return StateListSample(points.last(), this, end - 1, end)
        val i = x.toInt()
        return StateListSample(points[i], this, i.toDouble(), (i + 1).toDouble())
    }

    override val start: Double = 0.0
    override val end: Double = points.size.toDouble()

    override fun toString(): String {
        return "Trajectory size: ${points.size}\n" + points.joinToString("\n")
    }
}