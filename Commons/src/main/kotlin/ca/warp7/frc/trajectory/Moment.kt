package ca.warp7.frc.trajectory

data class Moment<T>(
        var t: Double,
        val v: T
)