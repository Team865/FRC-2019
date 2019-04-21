package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc.PID
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode

class DriveStraightPID(val distance: Double, val straightPID: PID = PID(
        kP = 3.0, kI = 0.00001, kD = 16.0, kF = 0.0,
        errorEpsilon = 0.07, dErrorEpsilon = 0.04, minTimeInEpsilon = 0.3,
        maxOutput = DriveConstants.kMaxVelocity
)) : Action {
    private val io: RobotIO = RobotIO

    override fun start() {
        io.driveControlMode = ControlMode.Velocity
        Drive.robotState = Pose2D(Translation2D.identity, Rotation2D.identity)
    }

    override val shouldFinish: Boolean
        get() = straightPID.isDone()

    override fun update() {
        straightPID.dt = io.dt
        val error = distance - Drive.robotState.translation.x
        println(error)

        io.leftDemand = straightPID.updateByError(error) * DriveConstants.kTicksPerMeterPer100ms
        io.rightDemand = straightPID.updateByError(error) * DriveConstants.kTicksPerMeterPer100ms
    }
}