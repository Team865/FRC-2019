package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs

class TurnAngle(val angle: Double) : Action {
    private val totalAngle
        get() = (Drive.leftPosition - Drive.rightPosition) /
                (1024 * 2 * DriveConstants.kWheelCircumference)
    var tolerance = 1E-2 // angle

    private var initialAngle = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.Position
        initialAngle = totalAngle

        val distance = DriveConstants.kTurningCircumference * angle / 360
        Drive.leftDemand = 1024 * distance
        Drive.rightDemand = -1024 * distance
    }

    override val shouldFinish
        get() = abs(angle - (totalAngle - initialAngle)) <= tolerance
}