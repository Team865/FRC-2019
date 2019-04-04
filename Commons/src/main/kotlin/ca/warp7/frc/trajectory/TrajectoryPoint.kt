package ca.warp7.frc.trajectory

import ca.warp7.frc.f
import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D

data class TrajectoryPoint(
        var state: CurvatureState<Pose2D>,
        var velocity: Double = 0.0,
        var acceleration: Double = 0.0,
        var t: Double = 0.0
) {
    override fun toString(): String {
        return "Timed(t=${t.f}, $state, v=${velocity.f}, a=${acceleration.f})"
    }
}