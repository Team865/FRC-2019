package ca.warp7.frc.drive

data class TankTrajectoryState<T>(
        val state: T
) {
    var leftVelocity = 0.0
    var rightVelocity = 0.0
    var leftAcceleration = 0.0
    var rightAcceleration = 0.0
}