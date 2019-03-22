package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State
import ca.warp7.frc.geometry.StateView

@Suppress("unused")
data class Trajectory<T : State<T>>(private val points: List<StateView<T>>) : StateListView<T, Trajectory<T>> {
    override fun get(x: Double): StateListSample<T, Trajectory<T>> {
        if (x < start) return StateListSample(points.first(), this, start, start + 1)
        if (x > end) return StateListSample(points.last(), this, end - 1, end)
        val i = x.toInt()
        return StateListSample(points[i], this, i.toDouble(), (i + 1).toDouble())
    }

    override val start: Double = 0.0
    override val end: Double = points.size.toDouble()
}