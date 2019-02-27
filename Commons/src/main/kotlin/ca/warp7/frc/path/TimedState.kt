package ca.warp7.frc.path

data class TimedState<T>(
        val v: T,
        var vel: Double = 0.0,
        var acc: Double = 0.0
)