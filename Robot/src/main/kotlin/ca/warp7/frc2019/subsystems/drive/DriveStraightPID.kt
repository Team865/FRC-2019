package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner.robotState
import ca.warp7.frc.PID
import com.ctre.phoenix.motorcontrol.ControlMode

class DriveStraightPID(val distance: Double, val straightPID: PID = PID(
        kP = 3.0, kI = 0.00001, kD = 16.0, kF = 0.0,
        errorEpsilon = 0.07, dErrorEpsilon = 0.04, minTimeInEpsilon = 0.3,
        maxOutput = DriveConstants.kMaxVelocity
)) : Action {

    override fun start() {
        Drive.controlMode = ControlMode.Velocity
        robotState = Pose2D(Translation2D.identity, Rotation2D.identity)
    }

    override val shouldFinish: Boolean
        get() = straightPID.isDone()

    override fun update() {
        straightPID.dt = DriveMotionPlanner.dt
        val error = distance-robotState.translation.x
        println(error)

        Drive.leftDemand = straightPID.updateByError(error) * DriveConstants.kTicksPerMeterPer100ms
        Drive.rightDemand = straightPID.updateByError(error) * DriveConstants.kTicksPerMeterPer100ms
    }
}