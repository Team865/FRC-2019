package ca.warp7.frc.drive

@Suppress("unused")
data class DifferentialModel(
        val wheelBaseRadius: Double,
        val maxVel: Double,
        val maxAcc: Double
)