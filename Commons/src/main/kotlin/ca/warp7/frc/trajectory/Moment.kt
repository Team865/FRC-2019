package ca.warp7.frc.trajectory

import ca.warp7.frc.f

data class Moment<T>(
        var t: Double,
        val v: T
) {
    override fun toString(): String {
        return "Moment(t=${t.f}, $v)"
    }
}