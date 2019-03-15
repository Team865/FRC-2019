package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.PI
import kotlin.math.abs

object TurnAngle : Action {
    private val totalAngle
        get() = 360 * (Drive.leftPosition - Drive.rightPosition) /
                (1024 * 2 * DriveConstants.kWheelCircumference)
    var angle = 0.0 // angle in radians
    var tolerance = 1E-2 // angle

    private var initialAngle = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.Position
        initialAngle = totalAngle

        val distance = DriveConstants.kTurningCircumference * angle / (2*PI)
        Drive.leftDemand = 1024 * distance
        Drive.rightDemand = -1024 * distance
    }

    override val shouldFinish
        get() = abs(angle - (totalAngle - initialAngle)) <= tolerance
}