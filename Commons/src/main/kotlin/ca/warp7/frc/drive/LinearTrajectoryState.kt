package ca.warp7.frc.drive

data class LinearTrajectoryState<T>(
        val state: T,
        var velocity: Double = 0.0,
        var acceleration: Double = 0.0,
        var constrained: Boolean = false
)