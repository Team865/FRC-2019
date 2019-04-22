package ca.warp7.frc2019.actions.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs

class TurnAngle(val angle: Double) : Action {

    private val io: RobotIO = RobotIO

    private val totalAngle
        get() = (io.leftPosition - io.rightPosition) /
                (1024 * 2 * DriveConstants.kWheelCircumference)
    var tolerance = 1E-2 // angle

    private var initialAngle = 0.0

    override fun start() {
        io.driveControlMode = ControlMode.Position
        initialAngle = totalAngle

        val distance = DriveConstants.kTurningCircumference * angle / 360
        io.leftDemand = 1024 * distance
        io.rightDemand = -1024 * distance
    }

    override val shouldFinish
        get() = abs(angle - (totalAngle - initialAngle)) <= tolerance
}