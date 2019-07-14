package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.f
import ca.warp7.frc.geometry.ArcPose2D

data class TrajectoryState(
        val arcPose: ArcPose2D,
        var v: Double = 0.0,
        var w: Double = 0.0,
        var dv: Double = 0.0,
        var dw: Double = 0.0,
        var ddv: Double = 0.0,
        var ddw: Double = 0.0,
        var t: Double = 0.0
) {
    override fun toString(): String {
        return "T(t=${t.f}, $arcPose, v=${v.f}, ω=${w.f}, a=${dv.f}, dω=${dw.f}, j=${ddv.f}, ddω=${ddw.f})"
    }

    val velocity get() = ChassisState(v, w)
    val acceleration get() = ChassisState(dv, dw)
}