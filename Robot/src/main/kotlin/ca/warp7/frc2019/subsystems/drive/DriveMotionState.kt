package ca.warp7.frc2019.subsystems.drive

data class DriveMotionState(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var yaw: Double = 0.0,
        var vel: Double = 0.0
)