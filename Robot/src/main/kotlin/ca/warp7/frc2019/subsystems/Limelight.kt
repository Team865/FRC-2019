package ca.warp7.frc2019.subsystems

import ca.warp7.frc.control.PID
import ca.warp7.frc.control.PIDControl
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromDegrees
import ca.warp7.frc.geometry.tan
import ca.warp7.frc2019.constants.LimelightConstants.hatchTargetHeightDiff
import ca.warp7.frc2019.constants.LimelightConstants.limelightAngleY
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import kotlin.math.abs

object Limelight {
    private val io: BaseIO = ioInstance()

    val dist get() = hatchTargetHeightDiff / ((Rotation2D.fromDegrees(io.visionErrorY) + limelightAngleY).tan)
    val lateral get() = dist * Rotation2D.fromDegrees(io.visionErrorX).sin

    var isDriver = false
    val visionPID = PIDControl(PID(kP = 0.2, kI = 0.06, kD = 0.0), maxOutput = 0.4)

    fun updateDriveAlignment(wantAligning: Boolean, xSpeed: Double) {
        val isAligning = wantAligning && io.foundVisionTarget && xSpeed >= 0 && abs(io.visionErrorX) < 15
        if (isAligning) {
            val speedLimit = 0.8 - 0.5 * io.visionArea
            io.leftDemand = io.leftDemand.coerceAtMost(speedLimit)
            io.rightDemand = io.rightDemand.coerceAtMost(speedLimit)
            if (xSpeed == 0.0) {
                io.leftDemand += Drive.model.frictionPercent
                io.rightDemand += Drive.model.frictionPercent
            }
            val correction = visionPID.updateByError(Math.toRadians(-io.visionErrorX), io.dt)
            if (correction > 0) io.rightDemand += correction
            else if (correction < 0) io.leftDemand += correction
        }
    }
}