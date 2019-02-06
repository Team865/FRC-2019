package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import kotlin.math.abs

object TurnAngle : Action {
    var angle = 0.0 // angle in degrees
    var tolerance = 1E-1 // angle

    private var initialAngle = 0.0

    override fun start() {
        Drive.outputMode = Drive.OutputMode.Position
        initialAngle = Drive.totalAngle

        val distance = DriveConstants.kTurningCircumference * angle / 360
        Drive.leftDemand = 1024 * distance
        Drive.rightDemand = -1024 * distance
    }

    override val shouldFinish
        get() = abs(angle - (Drive.totalAngle - initialAngle)) <= tolerance
}