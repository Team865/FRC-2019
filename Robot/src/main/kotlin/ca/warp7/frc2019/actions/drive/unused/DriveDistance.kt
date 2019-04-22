package ca.warp7.frc2019.actions.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs

class DriveDistance : Action {

    private val io: RobotIO = RobotIO

    var distance = 0.0 // distance in inches
    var tolerance = 1E-2 //distance

    private var initialDistance: Double = 0.0

    override fun start() {
        io.driveControlMode = ControlMode.Position
        val leftInitialSensorTicks = io.leftPosition
        val rightInitialSensorTicks = io.rightPosition
        initialDistance = DriveConstants.kWheelCircumference * ((leftInitialSensorTicks + rightInitialSensorTicks) / 2.0) / 1024

        io.leftDemand = 1024 * distance / DriveConstants.kWheelCircumference + leftInitialSensorTicks
        io.rightDemand = 1024 * distance / DriveConstants.kWheelCircumference + rightInitialSensorTicks
    }

    override val shouldFinish
        get() = abs(distance - ((io.leftPosition + io.rightPosition) / 2.0 - initialDistance)) <= tolerance
}