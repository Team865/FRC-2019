package ca.warp7.frc.drive

@Suppress("unused")
data class DifferentialDriveModel(
        val wheelRadius: Double,
        val wheelbaseRadius: Double,
        val maxVelocity: Double,
        val maxAcceleration: Double,
        val maxFreeSpeed: Double,
        val speedPerVolt: Double,
        val torquePerVolt: Double,
        val frictionVolts: Double,
        val linearInertia: Double,
        val angularInertia: Double,
        val maxVolts: Double
)