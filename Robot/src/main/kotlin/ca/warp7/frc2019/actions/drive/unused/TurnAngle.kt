package ca.warp7.frc2019.actions.drive.unused

import ca.warp7.frc.action.Action
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs

class TurnAngle(val angle: Double) : Action {

    private val io: BaseIO = ioInstance()

    private val totalAngle
        get() = (io.leftPosition - io.rightPosition) /
                (1024 * 2 * DriveConstants.kWheelCircumference)
    var tolerance = 1E-2 // angle

    private var initialAngle = 0.0

    override fun firstCycle() {
        io.driveControlMode = ControlMode.Position
        initialAngle = totalAngle

        val distance = DriveConstants.kTurningCircumference * angle / 360
        io.leftDemand = 1024 * distance
        io.rightDemand = -1024 * distance
    }

    override fun shouldFinish(): Boolean {
        return abs(angle - (totalAngle - initialAngle)) <= tolerance
    }
}