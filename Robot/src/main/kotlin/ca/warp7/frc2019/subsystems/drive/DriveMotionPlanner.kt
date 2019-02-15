package ca.warp7.frc2019.subsystems.drive

object DriveMotionPlanner {
    var lastDt = 0.0
    fun updateMeasurements(dt: Double) {
        lastDt = dt
    }
}