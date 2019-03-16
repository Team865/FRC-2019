package ca.warp7.frc.drive

import ca.warp7.frc.f

data class TankTrajectoryState<T>(
        val state: T,
        val curvature: Double,
        var leftVelocity: Double = 0.0,
        var rightVelocity: Double = 0.0,
        var leftAcceleration: Double = 0.0,
        var rightAcceleration: Double = 0.0
) {
    override fun toString(): String {
        return "State($state, k=${curvature.f}, lv=${leftVelocity.f}, rv=${rightVelocity.f}, " +
                "la=${leftAcceleration.f}, ra=${rightAcceleration.f})"
    }
}