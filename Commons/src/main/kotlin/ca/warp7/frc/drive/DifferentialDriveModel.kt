package ca.warp7.frc.drive

@Suppress("unused")
data class DifferentialDriveModel(
        val wheelbaseRadius: Double,
        val maxVelocity: Double,
        val maxAcceleration: Double,
        val maxFreeSpeedVelocity: Double,
        val speedPerVolt: Double,
        val torquePerVolt: Double,
        val frictionVoltage: Double,
        val linearInertia: Double,
        val angularInertia: Double
)