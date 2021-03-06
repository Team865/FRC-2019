package ca.warp7.frc2019.actions.drive.unused

import ca.warp7.frc.action.Action
import ca.warp7.frc.control.PID
import ca.warp7.frc.control.PIDControl
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode

class DriveStraightPID(val distance: Double, val straightPID: PIDControl = PIDControl(
        PID(kP = 3.0, kI = 0.00001, kD = 16.0, kF = 0.0),
        errorEpsilon = 0.07, dErrorEpsilon = 0.04, minTimeInEpsilon = 0.3,
        maxOutput = DriveConstants.kMaxVelocity
)) : Action {
    private val io: BaseIO = ioInstance()

    override fun firstCycle() {
        io.driveControlMode = ControlMode.Velocity
        Drive.odometry.resetPosition(Pose2D.identity, io.yaw)
    }

    override fun shouldFinish(): Boolean {
        return straightPID.isDone()
    }

    override fun update() {
        straightPID.dt = io.dt
        val error = distance - Drive.robotState.translation.x
        println(error)

        io.leftDemand = straightPID.updateByError(error) * Drive.kTicksPerMeterPer100ms
        io.rightDemand = straightPID.updateByError(error) * Drive.kTicksPerMeterPer100ms
    }
}