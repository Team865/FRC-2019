package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.f
import ca.warp7.frc.geometry.ArcPose2D

data class TrajectoryPoint(
        val arcPose: ArcPose2D,
        var velocity: Double = 0.0,
        var acceleration: Double = 0.0,
        var jerk: Double = 0.0,
        var t: Double = 0.0
) {
    override fun toString(): String {
        return "T(t=${t.f}, $arcPose, v=${velocity.f}, a=${acceleration.f}, j=${jerk.f})"
    }

    val chassisVelocity get() = ChassisState(velocity, velocity * arcPose.curvature)
    val chassisAcceleration get() = ChassisState(acceleration, acceleration * arcPose.curvature)
}