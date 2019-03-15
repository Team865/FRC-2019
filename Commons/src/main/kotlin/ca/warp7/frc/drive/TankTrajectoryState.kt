package ca.warp7.frc.drive

data class TankTrajectoryState<T>(
        val state: T,
        var leftVelocity: Double = 0.0,
        var rightVelocity: Double = 0.0,
        var leftAcceleration: Double = 0.0,
        var rightAcceleration: Double = 0.0
)