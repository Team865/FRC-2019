package ca.warp7.frc.path

@Suppress("unused")
data class DifferentialModel(
        val trackWidth: Double,
        val wheelBase: Double,
        val maxVel: Double,
        val maxAcc: Double
)